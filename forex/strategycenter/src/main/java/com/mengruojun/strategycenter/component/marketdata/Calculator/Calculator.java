package com.mengruojun.strategycenter.component.marketdata.Calculator;

import com.mengruojun.common.domain.HistoryDataKBar;

import java.util.TreeMap;

/**

 */
public interface Calculator {
  /**
   * calculate property value for only the last bars in the giving openTimeKBars
   *
   * Notes:
   * If the size of giving openTimeKBars is not enough to calculate (e.g. the giving size is 59, but the EMA(or something else) period is 60)
   *
   * @param openTimeKBars openTimeKBars
   * @return Double
   */
  public Double cal(TreeMap<Long, HistoryDataKBar> openTimeKBars);
}
