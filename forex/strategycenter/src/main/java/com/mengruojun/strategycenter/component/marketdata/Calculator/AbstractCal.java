package com.mengruojun.strategycenter.component.marketdata.Calculator;

import com.mengruojun.common.domain.HistoryDataKBar;

import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 5/9/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCal implements Calculator{
  protected double[] getAskClosePriceArray(TreeMap<Long, HistoryDataKBar> openTimeKBars){
    double closeAskArray[] = new double[openTimeKBars.size()];
    int index = 0;
    for (Long openTime : openTimeKBars.navigableKeySet()) {
      HistoryDataKBar kbar = openTimeKBars.get(openTime);
      closeAskArray[index] = kbar.getOhlc().getAskClose();
      index++;
    }

    return closeAskArray;
  }

  //todo  we can add getBidClose, ask volume, total volume and etc below;
}
