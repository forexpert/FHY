package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.domain.BrokerClient;

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

}
