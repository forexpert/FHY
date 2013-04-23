package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.PositionStatus;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.jms.utils.JMSSender;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.simple.SampleStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Service
public class StrategyManager {
  Logger logger = Logger.getLogger(this.getClass());
  @Autowired
  JMSSender tradeCommandSender;

  private Map<String, BaseStrategy> strategyMap = new ConcurrentHashMap<String, BaseStrategy>();

  public StrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  private void init() {
    strategyMap.put("sample", new SampleStrategy());
  }


  public void handle(BrokerClient bc, Long endTime) {
    BaseStrategy strategy = strategyMap.get(bc.getStrategyName());
    if (strategy != null) {
      //pending orders && SL&TP execution determine
      updateBrokerClientPendingPositions(bc, endTime);
      //analyze tradeCommand and send them to broker server
      List<TradeCommandMessage> tradeCommandMessageList = strategy.analysis(bc, endTime);
      if (tradeCommandMessageList != null) {
        Map<String, Object> tradeCommand = new HashMap<String, Object>();
        tradeCommand.put("clientId", bc.getClientId());
        tradeCommand.put("tradeCommandList", tradeCommandMessageList);
        tradeCommandSender.sendObjectMessage(tradeCommand);

        //update local bc status. Normally, we think all the trade command could be executed immediately.
        // But later we open an interface to reconcile the real status from Broker Server.
        updateBrokerClientStatus(bc, tradeCommandMessageList, endTime);
      }
    }
  }

  protected synchronized void marginCutCheck(BrokerClient bc, Long endTime){
    // check margin cut  -- margin cut is complex to implement, since there is factor named weekend leverage.
    // But for now --, to use back testing,
    // let's simply think the margin call will happen if the equity is less than the balance's 0.3 times.
    Map<Instrument, HistoryDataKBar> allLastBars = MarketDataManager.getAllInterestInstrumentS10Bars(endTime);
    if(bc.getEquity(allLastBars) < bc.getCurrentBalance()*0.3) {   // margin call, close all positions
      for(Position openPosition : bc.getOpenPositions()){
        openPosition.setStatus(PositionStatus.CLOSED);
        openPosition.setCloseTime(endTime - 1);
        openPosition.setCloseReason(Position.CloseReason.ReachMarginCut);
        openPosition.setClosePrice(allLastBars.get(openPosition.getInstrument()).getOhlc().getAskClose());
        bc.getClosedPositions().add(openPosition);
        bc.setCurrentBalance(bc.getStartBalance()-bc.getClosedCommission(openPosition, MarketDataManager.getAllInterestInstrumentS10Bars(endTime)));
      }
      bc.getOpenPositions().clear();
    }
  }

  /**
   * pending orders && SL&TP execution determine
   *
   * @param bc      BrokerClient
   * @param endTime endTime
   */
  protected synchronized void updateBrokerClientPendingPositions(BrokerClient bc, Long endTime) {

    marginCutCheck(bc, endTime);

    List<Position> pendingToOpenPositions = new ArrayList<Position>();

    for (Position pendingPosition : bc.getPendingPositions()) {
      HistoryDataKBar last_s10 = MarketDataManager.getKBarByEndTime_OnlySearch(endTime, pendingPosition.getInstrument(), TimeWindowType.S10);
      Double setPrice = pendingPosition.getOpenPrice();
      if (pendingPosition.getDirection() == Direction.Long) {
        if (last_s10.getOhlc().getAskLow() <= setPrice) { // deal
          pendingPosition.setStatus(PositionStatus.OPEN);
          pendingPosition.setOpenTime(endTime - 1);
          pendingToOpenPositions.add(pendingPosition);
        }
      } else {//short
        if (last_s10.getOhlc().getBidHigh() >= setPrice) { // deal
          pendingPosition.setStatus(PositionStatus.OPEN);
          pendingPosition.setOpenTime(endTime - 1);
          pendingToOpenPositions.add(pendingPosition);
        }
      }
    }


    List<Position> openToClosePositions = new ArrayList<Position>();
    for (Position openPosition : bc.getOpenPositions()) {
      HistoryDataKBar last_s10 = MarketDataManager.getKBarByEndTime_OnlySearch(endTime, openPosition.getInstrument(), TimeWindowType.S10);
      Double slInPips = openPosition.getStopLossInPips();
      Double tpInPips = openPosition.getTakeProfitInPips();

      Double slPrice = null;
      Double tpPrice = null;
      if (slInPips != 0 && slInPips != null) {
        slPrice = TradingUtils.getSLPrice(slInPips, openPosition.getOpenPrice(), openPosition.getDirection(), openPosition.getInstrument());
      }
      if (tpInPips != 0 && tpInPips != null) {
        tpPrice = TradingUtils.getTPPrice(tpInPips, openPosition.getOpenPrice(), openPosition.getDirection(), openPosition.getInstrument());
      }

      if (openPosition.getDirection() == Direction.Long) {
        if (slPrice != null) {
          if (slPrice >= last_s10.getOhlc().getBidLow()) {//deal
            openPosition.setStatus(PositionStatus.CLOSED);
            openPosition.setCloseTime(endTime - 1);
            openPosition.setCloseReason(Position.CloseReason.Reach_ST);
            openPosition.setClosePrice(Math.min(slPrice, last_s10.getOhlc().getBidHigh()));
            openToClosePositions.add(openPosition);

          }
        }

        if (tpPrice != null) {
          if (tpPrice <= last_s10.getOhlc().getBidHigh()) {//deal
            openPosition.setStatus(PositionStatus.CLOSED);
            openPosition.setCloseTime(endTime - 1);
            openPosition.setCloseReason(Position.CloseReason.Reach_TP);
            openPosition.setClosePrice(Math.max(tpPrice, last_s10.getOhlc().getBidLow()));
            openToClosePositions.add(openPosition);
          }
        }
      } else {//short
        if (slPrice != null) {
          if (slPrice <= last_s10.getOhlc().getAskHigh()) {//deal
            openPosition.setStatus(PositionStatus.CLOSED);
            openPosition.setCloseTime(endTime - 1);
            openPosition.setCloseReason(Position.CloseReason.Reach_ST);
            openPosition.setClosePrice(Math.max(slPrice, last_s10.getOhlc().getAskLow()));
            openToClosePositions.add(openPosition);
          }
        }

        if (tpPrice != null) {
          if (tpPrice >= last_s10.getOhlc().getAskLow()) {//deal
            openPosition.setStatus(PositionStatus.CLOSED);
            openPosition.setCloseTime(endTime - 1);
            openPosition.setCloseReason(Position.CloseReason.Reach_TP);
            openPosition.setClosePrice(Math.min(tpPrice, last_s10.getOhlc().getAskHigh()));
            openToClosePositions.add(openPosition);
          }
        }
      }
    }


    //move pending to opening
    for (Position p : pendingToOpenPositions) {
      bc.getPendingPositions().remove(p);
      bc.getOpenPositions().add(p);
      // apply commission
      bc.setCurrentBalance(bc.getStartBalance()-bc.getOpenCommission(p, MarketDataManager.getAllInterestInstrumentS10Bars(endTime)));
      logger.debug("updateBrokerClientPendingPositions: [endTime is " + endTime + "], [open position from pending order: " + p.getPositionId() + " ]");
    }

    //move opening to close
    for (Position p : openToClosePositions) {
      bc.getOpenPositions().remove(p);
      bc.getClosedPositions().add(p);
      bc.setCurrentBalance(bc.getStartBalance()-bc.getClosedCommission(p, MarketDataManager.getAllInterestInstrumentS10Bars(endTime)));
      logger.debug("updateBrokerClientPendingPositions: [endTime is " + endTime + "], [close position" + p.getPositionId() + ", reason + " + p.getCloseReason() + " ]");
    }
  }


  /**
   * update broker client's status, including:
   * <p/>
   * 1. position status
   * 2. account status
   *
   * @param bc                      BrokerClient
   * @param tradeCommandMessageList tradeCommandMessageList
   * @param endTime                 endTime
   */
  protected synchronized void updateBrokerClientStatus(BrokerClient bc, List<TradeCommandMessage> tradeCommandMessageList, Long endTime) {
    for (TradeCommandMessage tcm : tradeCommandMessageList) {

      logger.debug("updateBrokerClientStatus: [endTime is " + endTime + "], [TradeCommandType is " + tcm.getTradeCommandType() + "]");
      switch (tcm.getTradeCommandType()) {
        case openAtSetPrice: // this would result in a pending
          Position newPending = new Position();
          newPending.setPositionId(tcm.getPositionId());
          newPending.setStatus(PositionStatus.PENDING);
          newPending.setOpenPrice(tcm.getOpenPrice());
          newPending.setAmount(tcm.getAmount());
          newPending.setDirection(tcm.getDirection());
          newPending.setStopLossInPips(tcm.getStopLossPriceInPips());
          newPending.setTakeProfitInPips(tcm.getTakeProfitPriceInPips());
          newPending.setInstrument(tcm.getInstrument());
          bc.getPendingPositions().add(newPending);
          break;
        case openAtMarketPrice: //this would result adding a new open position
          Position newOpen = new Position();
          newOpen.setPositionId(tcm.getPositionId());
          newOpen.setStatus(PositionStatus.OPEN);
          newOpen.setOpenPrice(tcm.getOpenPrice());
          newOpen.setOpenTime(endTime + 1);
          newOpen.setAmount(tcm.getAmount());
          newOpen.setDirection(tcm.getDirection());
          newOpen.setStopLossInPips(tcm.getStopLossPriceInPips());
          newOpen.setTakeProfitInPips(tcm.getTakeProfitPriceInPips());
          newOpen.setInstrument(tcm.getInstrument());

          bc.getOpenPositions().add(newOpen);
          bc.setCurrentBalance(bc.getStartBalance()-bc.getOpenCommission(newOpen, MarketDataManager.getAllInterestInstrumentS10Bars(endTime)));

          break;
        case close:
          Position toBeClose = bc.getOpenPositionById(tcm.getPositionId());
          if (toBeClose != null) {
            Double originalAmount = toBeClose.getAmount();
            Double toBeClosedAmount = tcm.getAmount();
            if (originalAmount.equals(toBeClosedAmount)) {   // full close
              toBeClose.setStatus(PositionStatus.CLOSED);
              toBeClose.setCloseTime(endTime + 1);
              bc.getOpenPositions().remove(toBeClose);
              bc.getClosedPositions().add(toBeClose);
              bc.setCurrentBalance(bc.getStartBalance()-bc.getClosedCommission(toBeClose, MarketDataManager.getAllInterestInstrumentS10Bars(endTime)));
            } else if (originalAmount.compareTo(toBeClosedAmount) > 0) { // partial close
              Position newClosed = new Position();
              newClosed.setPositionId(tcm.getPositionId() + "_partial_close");
              newClosed.setInstrument(tcm.getInstrument());
              newClosed.setStatus(PositionStatus.CLOSED);
              newClosed.setOpenPrice(toBeClose.getOpenPrice());
              newClosed.setOpenTime(toBeClose.getOpenTime());
              newClosed.setAmount(toBeClosedAmount);
              newClosed.setDirection(toBeClose.getDirection());
              bc.getClosedPositions().add(newClosed);
              bc.setCurrentBalance(bc.getStartBalance()-bc.getClosedCommission(newClosed, MarketDataManager.getAllInterestInstrumentS10Bars(endTime)));
              toBeClose.setAmount(originalAmount - toBeClosedAmount);

            } else {
              logger.error("The amount to be closed is more than the original amount.", new Exception());
            }


          } else {
            logger.error("The position to be closed is null.", new Exception());
          }
          break;
        case cancel:
          // delete an pending order
          Position toBeCancel = bc.getPendingPositionById(tcm.getPositionId());
          if (toBeCancel != null) {
            bc.getPendingPositions().remove(toBeCancel);
          } else {
            logger.error("The pending position to be cancelled is null.", new Exception());
          }

          break;
        case change:
          // change an pending order or existing open position's SL/TP

          Position toBeChanged = bc.getPendingPositionById(tcm.getPositionId());
          if (toBeChanged != null) {  // the position to be changed is a pending position
            toBeChanged.setOpenPrice(tcm.getOpenPrice());
            toBeChanged.setAmount(tcm.getAmount());
            toBeChanged.setStopLossInPips(tcm.getStopLossPriceInPips());
            toBeChanged.setTakeProfitInPips(tcm.getStopLossPriceInPips());
          } else {
            toBeChanged = bc.getOpenPositionById(tcm.getPositionId());
            if (toBeChanged != null) { // the position to be changed is an open position
              toBeChanged.setStopLossInPips(tcm.getStopLossPriceInPips());
              toBeChanged.setTakeProfitInPips(tcm.getStopLossPriceInPips());
            } else {
              logger.error("The position to be change is null.", new Exception());
            }
          }
          break;
        default:
          break;
      }
    }
  }
}