package com.mengruojun.common.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public enum TimeWindowType {

  S10(10 * 1000),
  S20(20 * 1000),
  S30(30 * 1000),
  M1(60 * 1000),
  M5(60 * 5 * 1000),
  M10(60 * 10 * 1000),
  M30(60 * 30 * 1000),
  H1(3600 * 1000),
  H4(4 * 3600 * 1000),
  D1(24 * 3600 * 1000);


  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  private long timeInMillis;

  private TimeWindowType(long timeInMillis) {
    this.timeInMillis = timeInMillis;
  }

  /**
   * get next level TimeWindowType. E.g.  if passing in D1, return H4; passing in H4, return H1; and etc.
   * @return
   */
  public static TimeWindowType getNextLevel(TimeWindowType twt){
    switch (twt){
      case D1:
        return H4;
      case H4 :
        return H1;
      case H1:
        return M30;
      case M30:
        return M10;
      case M10:
        return M5;
      case M5:
        return M1;
      case M1:
        return S30;
      /*S30 and S20 both have S10 as next level*/
      case S30:
      case S20:
        return S10;
      case S10:
        return null;
      default:
        return null;

    }
  }

  public long getTimeInMillis() {
    return timeInMillis;
  }

  public boolean canEndWithTime(long endTime) {
    // todo  cmeng
    return false;
  }

  /**
   * giving an endTime,  return the LastAvailableEndTime bar's endTime
   * @param endTime endTime
   * @return
   */
  public static Long getLastAvailableEndTime(TimeWindowType twt, Long endTime) {
    Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    calender.setTimeInMillis(endTime);

    switch (twt){
      case D1:
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        break;

      case H4 :
        calender.set(Calendar.HOUR_OF_DAY, calender.get(Calendar.HOUR_OF_DAY)/4*4);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        break;

      case H1:
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        break;

      case M30:
        calender.set(Calendar.MINUTE, calender.get(Calendar.MINUTE)/30*30);
        calender.set(Calendar.SECOND, 0);
        break;

      case M10:
        calender.set(Calendar.MINUTE, calender.get(Calendar.MINUTE)/10*10);
        calender.set(Calendar.SECOND, 0);
        break;

      case M5:
        calender.set(Calendar.MINUTE, calender.get(Calendar.MINUTE)/5*5);
        calender.set(Calendar.SECOND, 0);
        break;
      case M1:
        calender.set(Calendar.SECOND, 0);
        break;
      case S30:
        calender.set(Calendar.SECOND, calender.get(Calendar.SECOND)/30*30);
        break;
      case S20:
        calender.set(Calendar.SECOND, calender.get(Calendar.SECOND)/20*20);
        break;
      case S10:
        calender.set(Calendar.SECOND, calender.get(Calendar.SECOND)/10*10);
        break;
      default:
        break;
    }

    return calender.getTimeInMillis();

  }
}
