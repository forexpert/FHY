package com.mengruojun.strategycenter.component.marketdata.Calculator;

import com.mengruojun.common.domain.HistoryDataKBar;

import java.util.TreeMap;

/**
 * refer to http://en.wikipedia.org/wiki/MACD
 *
 * MACD = EMA[stockPrices,12] – EMA[stockPrices,26]
 * signal = EMA[MACD,9]
 * histogram = MACD – signal
 */
public class MACDCalculator extends AbstractCal{

  public static enum MACD_VALUE_TYPE {
    MACD, signial, histogram
  }

  private int shortTerm;
  private int longTerm;
  private int macdPeriod;

  private MACD_VALUE_TYPE outPutType;

  public MACDCalculator(int shortTerm, int longTerm, int macdPeriod, MACD_VALUE_TYPE outPutType) {
    this.shortTerm = shortTerm;
    this.longTerm = longTerm;
    this.macdPeriod = macdPeriod;
    this.outPutType = outPutType;
  }

  /**
   * calculate property value for only the last bars in the giving openTimeKBars
   * <p/>
   * Notes:
   * If the size of giving openTimeKBars is not enough to calculate (e.g. the giving size is 59, but the EMA(or something else) period is 60)
   *
   * @param openTimeKBars openTimeKBars
   * @return Double
   */
  @Override
  public Double cal(TreeMap<Long, HistoryDataKBar> openTimeKBars) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
