package com.mengruojun.common.utils;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/20/12
 * Time: 9:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDataKBarUtils {

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

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


  /**
   * input an endTime point, output all TimeWindowType, which could end with such an endTime
   * @param endTime endTime
   * @return all TimeWindowType, which could end with such an endTime
   */
  public List<TimeWindowType> getTimeWindowTypesForEndTime(long endTime){
    List<TimeWindowType> timeWindowTypes = new ArrayList<TimeWindowType>();
    for(TimeWindowType timeWindowType: TimeWindowType.values()){
      if(timeWindowType.canEndWithTime(endTime)){
        timeWindowTypes.add(timeWindowType);
      }
    }
    return timeWindowTypes;
  }


}
