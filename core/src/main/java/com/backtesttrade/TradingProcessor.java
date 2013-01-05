package com.backtesttrade;

import com.backtesttrade.domain.*;
import com.backtesttrade.strategy.AbstractStrategy;
import com.historydatacenter.model.HistoryDataKBar;

import java.util.ArrayList;
import java.util.List;

/**
 * TradingProcessor
 * Time Line
 * ---- LastBar ----Current Analysis Time Point ----- nextBar ----
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
   * @param kBars    Data list with Continual Time Serial
   * @param account  input account
   * @param strategy input strategy
   */
  private void CTSS(List<HistoryDataKBar> kBars, Account account, AbstractStrategy strategy) {
    for (HistoryDataKBar bar : kBars) {
      STSS(bar, bar, account, strategy);
    }

    //todo out put some performance data by looking into account

  }

  /**
   * Single Time Serial System.
   *
   * @param lastBar
   * @param nextBar
   * @param account  input account
   * @param strategy input strategy
   */
  private void STSS(HistoryDataKBar lastBar, HistoryDataKBar nextBar, Account account, AbstractStrategy strategy) {
    // 0. execute Orders
    marketExecutor.executeOrders(account, lastBar);
    // 1. compute actions by strategy. Strategy doesn't need to consider if account is able(has enough money,
    // or marget is closed or ant other special situation, which will be consider during executing actions
    List<StrategyAction> actions = strategy.computeActions(account, lastBar);
    // 2. execute actions
    marketExecutor.executeActions(account, actions, lastBar, nextBar);
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


