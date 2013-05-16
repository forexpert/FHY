package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

/**
 * Created with IntelliJ IDEA.
 * User: Clyde
 * Date: 2/13/13
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseStrategy {
  protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

  static {
    sdf.setTimeZone(TradingUtils.GMT);
  }

  public List<TradeCommandMessage> analysis(BrokerClient bc, long currentTime) {
    if (bc.getAnalyzedTradeCommandMap().containsKey(currentTime)) {
      return null;
    } else {
      List<TradeCommandMessage> analysisResult = OnAnalysis(bc, currentTime);
      if (bc.getAnalyzedTradeCommandMap().size() >= BrokerClient.ANALYZED_TRADE_MAP_MAX_NUM) {
        bc.getAnalyzedTradeCommandMap().remove(((SortedMap)bc.getAnalyzedTradeCommandMap()).firstKey());
      }
      bc.getAnalyzedTradeCommandMap().put(currentTime, analysisResult);
      if (analysisResult.size() == 0) {
        return null;
      } else {
        return analysisResult;
      }
    }

  }

  /**
   * Which should be implemented by subclasses.
   *
   * @param bc
   * @param currentTime
   * @return
   */
  protected abstract List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime);

  /* Utilities */




  protected  TradeCommandMessage openPositionAtMarketPriceTCM(long analyzeTime,
                                                              String positionLabel, Instrument instrument, Double amount,
                                                              Direction direction, Double stInPips, Double tpInPips){
    TradeCommandMessage tcm = new TradeCommandMessage(analyzeTime);
    tcm.setTradeCommandType(TradeCommandType.openAtMarketPrice);
    tcm.setPositionId(positionLabel);
    tcm.setInstrument(instrument);
    tcm.setAmount(amount);
    tcm.setDirection(direction);
    tcm.setTakeProfitInPips(tpInPips);
    tcm.setStopLossInPips(stInPips);
    return tcm;
  }
  protected  TradeCommandMessage openPendingPositionTCM(long analyzeTime,
                                                        String positionLabel, Instrument instrument, Double amount,
                                                        Direction direction, Double openPrice, Double stInPips, Double tpInPips){
    TradeCommandMessage tcm = new TradeCommandMessage(analyzeTime);
    tcm.setTradeCommandType(TradeCommandType.openAtSetPrice);
    tcm.setPositionId(positionLabel);
    tcm.setInstrument(instrument);
    tcm.setAmount(amount);
    tcm.setOpenPrice(openPrice);
    tcm.setDirection(direction);
    tcm.setTakeProfitInPips(tpInPips);
    tcm.setStopLossInPips(stInPips);
    return tcm;
  }
  protected  TradeCommandMessage closePositionAtMarketPriceTCM(long analyzeTime,String positionLabel,Double amount){
    TradeCommandMessage tcm = new TradeCommandMessage(analyzeTime);
    tcm.setTradeCommandType(TradeCommandType.close);
    tcm.setPositionId(positionLabel);
    tcm.setAmount(amount);
    return tcm;
  }
  protected  TradeCommandMessage cancelPendingPositionTCM(long analyzeTime,String positionLabel){
    TradeCommandMessage tcm = new TradeCommandMessage(analyzeTime);
    tcm.setTradeCommandType(TradeCommandType.cancel);
    tcm.setPositionId(positionLabel);
    return tcm;
  }
  protected  TradeCommandMessage changePendingPositionTCM(long analyzeTime,String positionLabel, Double openPrice,
                                                          Double amount, Double stInPips, Double tpInPips){
    TradeCommandMessage tcm = new TradeCommandMessage(analyzeTime);
    tcm.setTradeCommandType(TradeCommandType.change);
    tcm.setPositionId(positionLabel);
    tcm.setAmount(amount);
    tcm.setOpenPrice(openPrice);

    tcm.setTakeProfitInPips(tpInPips);
    tcm.setStopLossInPips(stInPips);
    return tcm;
  }
  protected  TradeCommandMessage changeOpenPositionTCM(long analyzeTime,String positionLabel, Double stInPips, Double tpInPips){
    TradeCommandMessage tcm = new TradeCommandMessage(analyzeTime);
    tcm.setTradeCommandType(TradeCommandType.change);
    tcm.setPositionId(positionLabel);

    tcm.setTakeProfitInPips(tpInPips);
    tcm.setStopLossInPips(stInPips);
    return tcm;
  }

}
