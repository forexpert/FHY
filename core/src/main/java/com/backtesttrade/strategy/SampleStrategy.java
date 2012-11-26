package com.backtesttrade.strategy;

import com.backtesttrade.domain.*;
import com.historydatacenter.model.HistoryDataKBar;

import java.util.ArrayList;
import java.util.List;

/**
 * just a sample strategy which implement appropriate interface and subclass methods
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 11/23/12
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleStrategy extends AbstractStrategy {


  @Override
  public List<StrategyAction> computeActions(Account account, HistoryDataKBar bar) {
    List<StrategyAction> actions = new ArrayList<StrategyAction>();

    if (Math.random() > 0.9) {
      //open a position at Market price
      StrategyAction action = new StrategyAction(null, StrategyAction.PriceType.atMarket, 1000.0, OperationType.Open, Direction.Long, null, null);
      actions.add(action);

    }
    if (Math.random() < 0.1 && account.getOpenPositions().size() >= 0) {
      Position openPosition = account.getOpenPositions().get(0);
      StrategyAction action = new StrategyAction(null, StrategyAction.PriceType.atMarket, 1000.0, OperationType.Close, null, null, null);
      actions.add(action);
    }


    return actions;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
