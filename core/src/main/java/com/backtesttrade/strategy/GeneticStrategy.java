package com.backtesttrade.strategy;

import com.backtesttrade.domain.Account;
import com.backtesttrade.domain.StrategyAction;
import com.historydatacenter.model.HistoryDataKBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Genetic  Strategy uses multiple factors to determine trade actions. Those factors are weighted by genetic algorithm
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 11/23/12
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneticStrategy extends AbstractStrategy{

  List<Factor> factors = new ArrayList<Factor>();
  Map<Factor, Double> factorsWeight = new HashMap<Factor, Double>();


  @Override
  public List<StrategyAction> computeActions(Account account, HistoryDataKBar bar) {
    //todo cmeng
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
