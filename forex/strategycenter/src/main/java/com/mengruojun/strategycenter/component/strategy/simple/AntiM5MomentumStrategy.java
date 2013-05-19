package com.mengruojun.strategycenter.component.strategy.simple;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 5/18/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AntiM5MomentumStrategy  extends M5MomentumStrategy{


  public AntiM5MomentumStrategy(Instrument targetInstrument) {
    super(targetInstrument);
  }

  /**
   * Which should be implemented by subclasses.
   *
   * @param bc
   * @param currentTime
   * @return
   */
  @Override
  protected List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    return super.OnAnalysis(bc,currentTime);
  }


  protected List<TradeCommandMessage>  closePositionAnalysis(BrokerClient bc, long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    for(Position p : bc.getOpenPositions()){
      if(p.getPositionId().startsWith("HasNoTP_")){
        boolean close = false;

        Double currentAskClose = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5).getOhlc().getAskClose();
        Double currentEMA20Price = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
        if(p.getDirection() == Direction.Short){
          if(currentAskClose<currentEMA20Price && currentAskClose-p.getOpenPrice()>(p.getOpenPrice()-p.getTakeProfitPrice())){
            close = true;
          }
        } else {
          if(currentAskClose>currentEMA20Price && p.getOpenPrice()-currentAskClose>(p.getTakeProfitPrice()-p.getOpenPrice())){
            close = true;
          }
        }

        if(close){// construct close command
          TradeCommandMessage tcm = this.closePositionAtMarketPriceTCM(currentTime,p.getPositionId(),p.getAmount());
          tcmList.add(tcm);
        }
      }

    }
    return tcmList;
  }


  protected List<TradeCommandMessage> openPositionAnalysis(BrokerClient bc,long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    //determine if we should open position
    Double currentAskClose = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5).getOhlc().getAskClose();
    Double currentEMA20Price = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
    Double currentMACDHistgram = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.MACD_12_26_9_hist);
    long lastBarTime = currentTime - TimeWindowType.M5.getTimeInMillis();
    HistoryDataKBar lastBar = MarketDataManager.getKBarByEndTime_OnlySearch(lastBarTime, instrument, TimeWindowType.M5);
    if(lastBar == null) {
      return tcmList;
    }
    Double lastAskClose = lastBar.getOhlc().getAskClose();
    Double lastEMA20Price = MarketDataManager.getKBarAttributes(lastBarTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
    Double lastMACDHistgram = MarketDataManager.getKBarAttributes(lastBarTime, instrument, TimeWindowType.M5, KBarAttributeType.MACD_12_26_9_hist);


    if (determineEma20(currentAskClose, currentEMA20Price, lastAskClose, lastEMA20Price, currentTime) &&
      determineMACD(currentMACDHistgram, lastMACDHistgram, currentTime)) {
      //start to open a position
      Direction perferDirection = ema20ConditionPerfer == Direction.Long ? Direction.Short : Direction.Long;

      //set SL , usually as 20 pips. But we can enhance it  later
      double slPips = 20;
      //

      // verify if money is enough
      if (bc.getOpenPositions().size() < 5 && bc.getLeftMargin(currentPriceMap) > 0) {
        String positionId = this.getClass().getSimpleName() + bc.getClientId() + "_" + instrument.getCurrency1() + instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime));
        TradeCommandMessage tcm1 = this.openPositionAtMarketPriceTCM(currentTime, "HasTP_" + positionId, instrument,
          TradingUtils.getMinAmount(instrument) * 5, perferDirection, slPips,slPips );
        /*TradeCommandMessage tcm2 = this.openPositionAtMarketPriceTCM(currentTime, "HasNoTP_" + positionId, instrument,
          TradingUtils.getMinAmount(instrument) * 5, perferDirection, 0d, slPips);*/
        tcmList.add(tcm1);
        //tcmList.add(tcm2);
      }


      //clear
      ema20ConditionPerfer = null;
      countForNUM_PASS_MACD_BAR_CROSS = 0;

    }

    return tcmList;

  }
}
