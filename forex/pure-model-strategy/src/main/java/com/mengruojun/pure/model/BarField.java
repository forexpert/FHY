package com.mengruojun.pure.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Field, including
 * | openTime
 * | closeTime
 * | currency1
 * | currency2
 * | askClose
 * | askHigh
 * | askLow
 * | askOpen
 * | askVolume
 * | bidClose
 * | bidHigh
 * | bidLow
 * | bidOpen
 * | bidVolume
 * | timeWindowType
 *
 *
 */
public enum BarField {
  //FromDB
  openTime,
  closeTime,
  currency1,
  currency2,
  askClose,
  askHigh,
  askLow,
  askOpen,
  askVolume,
  bidClose,
  bidHigh,
  bidLow,
  bidOpen,
  bidVolume,
  timeWindowType,


  //Calculation
  EMA5,
  EMA10,
  EMA20,
  EMA30,
  EMA40,
  EMA60,

  MACD


  ;


  public static enum BarFieldType{
    /** the value can come from DB */
    FromDB,
    /** the value should come from Calculation */
    Calculation
  }

  public static enum BarFieldCalType{
    EMA,
    MACD
  }

  private BarFieldType type;
  private BarFieldCalType calType;
  private Object[] calParams;
  private String description;

  private BarField() {
    this(BarFieldType.FromDB, null, null, null);
  }

  private BarField(BarFieldCalType calType, Object[] calParams, String description) {
    this(BarFieldType.Calculation, calType, calParams, description);
  }

  private BarField(BarFieldType type, BarFieldCalType barFieldCalType, Object[] calParams, String description) {
    this.type = type;
    this.calParams = calParams;
    this.description = description;
  }



  //=========getter and setter===========


  public BarFieldType getType() {
    return type;
  }

  public void setType(BarFieldType type) {
    this.type = type;
  }

  public Object[] getCalParams() {
    return calParams;
  }

  public void setCalParams(Object[] calParams) {
    this.calParams = calParams;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BarFieldCalType getCalType() {
    return calType;
  }

  public void setCalType(BarFieldCalType calType) {
    this.calType = calType;
  }
}
