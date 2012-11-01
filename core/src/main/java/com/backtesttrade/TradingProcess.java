package com.backtesttrade;

import com.backtesttrade.domain.AbstractStrategy;
import com.backtesttrade.domain.Account;
import com.historydatacenter.model.HistoryDataKBar;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/21/12
 * Time: 10:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingProcess {
  /**
   * start the history back testing investment
   */
  public void run() {
    List<HistoryDataKBar> list = new ArrayList<HistoryDataKBar>();  //todo init list
    Account account = null;                                         //todo init account
    AbstractStrategy strategy = null;                                       //todo init strategy

    CTSS(list, account, strategy);
  }

  /**
   * Continual Time Serial System.
   *
   * @param list     Data list with Continual Time Serial
   * @param account  input account
   * @param strategy input strategy
   */
  private void CTSS(List<HistoryDataKBar> list, Account account, AbstractStrategy strategy) {
    for (HistoryDataKBar bar : list) {
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
    //todo
  }


}
