package com.backtesttrade;

import com.backtesttrade.strategy.AbstractStrategy;
import com.backtesttrade.domain.Account;
import com.backtesttrade.domain.StrategyAction;
import com.historydatacenter.model.HistoryDataKBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/21/12
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingProcessor {

  /*execute actions computed by strategy
  * which can be replaced in real trading system.
  */
  private MarketExecutor marketExecutor = new MarketExecutor();

  List<HistoryDataKBar> kBars = new ArrayList<HistoryDataKBar>();  //todo init list

  AbstractStrategy strategy = null;

  public TradingProcessor(MarketExecutor marketExecutor, List<HistoryDataKBar> kBars, AbstractStrategy strategy) {
    this.marketExecutor = marketExecutor;
    this.kBars = kBars;
    this.strategy = strategy;
  }

  /**
   * start the history back testing investment
   */
  public void run() {
    Account account = Account.getTypicalAccount();
    CTSS(kBars, account, strategy);
  }

  /**
   * Continual Time Serial System.
   *
   * @param kBars     Data list with Continual Time Serial
   * @param account  input account
   * @param strategy input strategy
   */
  private void CTSS(List<HistoryDataKBar> kBars, Account account, AbstractStrategy strategy) {
    for (HistoryDataKBar bar : kBars) {
      STSS(bar, account, strategy);
    }

    //todo out put some performance data by looking into account

  }

  /**
   * Single Time Serial System.
   *
   * @param bar      Input only Single Time Serial Bar
   * @param account  input account
   * @param strategy input strategy
   */
  private void STSS(HistoryDataKBar bar, Account account, AbstractStrategy strategy) {
    // 1. compute actions by strategy. Strategy doesn't need to consider if account is able(has enough money,
    // or marget is closed or ant other special situation, which will be consider during executing actions
    List<StrategyAction> actions = strategy.computeActions(account, bar);

    // 2. execute actions

    marketExecutor.executeActions(account, actions, bar);
  }

  //setter and getter


  public MarketExecutor getMarketExecutor() {
    return marketExecutor;
  }

  public void setMarketExecutor(MarketExecutor marketExecutor) {
    this.marketExecutor = marketExecutor;
  }

  public List<HistoryDataKBar> getKBars() {
    return kBars;
  }

  public void setList(List<HistoryDataKBar> kBars) {
    this.kBars = kBars;
  }

  public AbstractStrategy getStrategy() {
    return strategy;
  }

  public void setStrategy(AbstractStrategy strategy) {
    this.strategy = strategy;
  }
}


/**
 * MarketExecutor is responsible for trying to execute action.
 * rules:
 * 1. if the account doesn't have enough margin, this executor will return failure if a open position action is requested.
 * 2. if the actionPriceType is setManually, and it isn't in the price range of the next bar, the action result is failed.
 * ...
 */
class MarketExecutor {
  /**
   * execute actions computed by strategy
   * which can be replaced in real trading system.
   *
   * @param account
   * @param actions
   * @param bar
   */
  public void executeActions(Account account, List<StrategyAction> actions, HistoryDataKBar bar) {
    for (StrategyAction action : actions) {
      switch (action.getOperationType()){
        case Open:



          break;
        case Close:
          break;
      }
    }
  }



}