package com.mengruojun.strategycenter.component.marketdata.Calculator;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import java.util.TreeMap;

/**
 * refer to http://en.wikipedia.org/wiki/MACD
 * <p/>
 * MACD = EMA[stockPrices,12] – EMA[stockPrices,26]
 * signal = EMA[MACD,9]
 * histogram = MACD – signal
 */
public class MACDCalculator extends AbstractCal {
  private RetCode retCode = RetCode.InternalError;
  private Core lib = new Core();

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
    double close[] = getAskClosePriceArray(openTimeKBars);
    double[] outMACD = new double[close.length];
    double[] outMACDSignal = new double[close.length];
    double[] outMACDHist = new double[close.length];
    MInteger outBegIdx = new MInteger();
    MInteger outNbElement = new MInteger();
    retCode = lib.macd(0, close.length - 1, close, shortTerm, longTerm, macdPeriod, outBegIdx, outNbElement, outMACD, outMACDSignal, outMACDHist);
    if (outNbElement.value == 0) {// it means no value is calculated.
      return null;
    }
    switch (outPutType) {
      case MACD:
        return outMACD[outNbElement.value - 1];
      case histogram:
        return outMACDHist[outNbElement.value - 1];
      case signial:
        return outMACDSignal[outNbElement.value - 1];
    }
    return null;
  }
}
