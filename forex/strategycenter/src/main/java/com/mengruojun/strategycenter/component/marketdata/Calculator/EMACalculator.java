package com.mengruojun.strategycenter.component.marketdata.Calculator;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import java.util.TreeMap;

/**
 * EMA Calculator
 */
public class EMACalculator extends AbstractCal{
  private RetCode retCode = RetCode.InternalError;
  private Core lib = new Core();


  private int period;

  public EMACalculator(int period) {
    this.period = period;
  }

  @Override
  /**
   * performance issue may introduce. We may should use lib.ema(close.length-1, close.length-1, ....).
   * But as my test , it will return different value which is incorrect
   */
  public Double cal(TreeMap<Long, HistoryDataKBar> openTimeKBars) {
    double close[] = getAskClosePriceArray(openTimeKBars);
    double out[] = new double[close.length];
     MInteger outBegIdx = new MInteger();
     MInteger outNbElement = new MInteger();
    retCode = lib.ema(0, close.length - 1, close, period, outBegIdx, outNbElement, out);
    if(outNbElement.value == 0){// it means no value is calculated.
      return null;
    }
    return out[outNbElement.value - 1];
  }
}
