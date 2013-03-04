package com.mengruojun.common.utils;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;


public class HistoryDataKBarUtils {
  static Logger logger = Logger.getLogger(HistoryDataKBarUtils.class);

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * get properties from a List of HistoryDataKBars, then return them as an array. Usually used for ta-lib API's input array data
   * @param list  list
   * @param barPropertyName possible value getAskClose, getBidClose, getAskXXX, getBidXXX.
   * @return Double[]
   * @throws NoSuchMethodException  NoSuchMethodException
   * @throws java.lang.reflect.InvocationTargetException InvocationTargetException
   * @throws IllegalAccessException IllegalAccessException
   */
  public static Double[] getPropertyArray(List<HistoryDataKBar> list, String barPropertyName){
     return (Double[]) getPropertyList(list, barPropertyName).toArray();
  }

  public static List<Double> getPropertyList(List<HistoryDataKBar> list, String barPropertyName){
    try{
      List<Double> reData = new ArrayList<Double>();
      for (int i = 0; i < list.size(); i++) {
        Method method = OHLC.class.getMethod(barPropertyName);
        double propertyValue = (Double) method.invoke(list.get(i).getOhlc());
        reData.add(propertyValue);
      }
      return reData;
    } catch (Exception e){
      logger.error("",e);
    }
    return null;
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
