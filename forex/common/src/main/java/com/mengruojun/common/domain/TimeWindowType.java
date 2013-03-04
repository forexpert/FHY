package com.mengruojun.common.domain;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public enum TimeWindowType {

  S10(10 * 1000),
  S20(20 * 1000),
  S30(30 * 1000),
  M1(60 * 1000),
  M5(60 * 5 * 1000),
  M10(60 * 5 * 1000),
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
}
