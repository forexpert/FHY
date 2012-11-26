package com.backtesttrade.strategy;

import com.backtesttrade.domain.Account;
import com.backtesttrade.domain.StrategyAction;
import com.historydatacenter.model.HistoryDataKBar;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/21/12
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractStrategy {/*

  *//**
   * @return Object o  -- show "Long, short, or none"
   *//*
  public abstract Object openOrNot();

  *//**
   * @return Object o  --
   *//*
  public abstract Object closeOrNot();
*/

  /**
   * compute actions by strategy. Strategy doesn't need to consider if account is able(has enough money,
   * or marget is closed or ant other special situation, which will be consider during executing actions
   *
   * @param account
   * @param bar
   * @return
   */
  public abstract List<StrategyAction> computeActions(Account account, HistoryDataKBar bar);
}

