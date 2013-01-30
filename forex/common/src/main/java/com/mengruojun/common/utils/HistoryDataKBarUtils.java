package com.mengruojun.common.utils;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.OHLC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/20/12
 * Time: 9:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDataKBarUtils {

  /**
   * get properties from a List of HistoryDataKBars, then return them as an array. Usually used for ta-lib API's input array data
   * @param list
   * @param BarPropertyName possible value getAskClose, getBidClose, getAskXXX, getBidXXX.
   * @return
   * @throws NoSuchMethodException
   * @throws java.lang.reflect.InvocationTargetException
   * @throws IllegalAccessException
   */
  public static double[] getPropertyArray(List<HistoryDataKBar> list, String BarPropertyName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    double[] reData = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      Method method = OHLC.class.getMethod(BarPropertyName);
      double propertyValue = (Double) method.invoke(list.get(i).getOhlc());
      reData[i] = propertyValue;
    }
    return reData;
  }

}
