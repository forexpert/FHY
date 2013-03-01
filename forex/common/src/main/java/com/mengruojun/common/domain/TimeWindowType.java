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

  public long getTimeInMillis() {
    return timeInMillis;
  }

  public boolean canEndWithTime(long endTime) {
    // todo  cmeng
    return false;
  }
}
