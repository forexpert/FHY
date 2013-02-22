package com.mengruojun.jms.domain;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;

import java.io.Serializable;
import java.util.Currency;

/**
 * MarketData JMS object domain
 */
public class MarketDataMessage implements Serializable {
  private static final long serialVersionUID = 1L;

  public MarketDataMessage(long startTime, double askOpen, double askHigh, double askLow, double askClose,
                           double bidOpen, double bidHigh, double bidLow, double bidClose, double askVolume,
                           double bidVolume, Currency currency1, Currency currency2, TimeWindowType timeWindowType) {
    this.startTime = startTime;
    this.askOpen = askOpen;
    this.askHigh = askHigh;
    this.askLow = askLow;
    this.askClose = askClose;
    this.bidOpen = bidOpen;
    this.bidHigh = bidHigh;
    this.bidLow = bidLow;
    this.bidClose = bidClose;
    this.askVolume = askVolume;
    this.bidVolume = bidVolume;
    this.currency1 = currency1;
    this.currency2 = currency2;
    this.timeWindowType = timeWindowType;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public double getAskOpen() {
    return askOpen;
  }

  public void setAskOpen(double askOpen) {
    this.askOpen = askOpen;
  }

  public double getAskHigh() {
    return askHigh;
  }

  public void setAskHigh(double askHigh) {
    this.askHigh = askHigh;
  }

  public double getAskLow() {
    return askLow;
  }

  public void setAskLow(double askLow) {
    this.askLow = askLow;
  }

  public double getAskClose() {
    return askClose;
  }

  public void setAskClose(double askClose) {
    this.askClose = askClose;
  }

  public double getBidOpen() {
    return bidOpen;
  }

  public void setBidOpen(double bidOpen) {
    this.bidOpen = bidOpen;
  }

  public double getBidHigh() {
    return bidHigh;
  }

  public void setBidHigh(double bidHigh) {
    this.bidHigh = bidHigh;
  }

  public double getBidLow() {
    return bidLow;
  }

  public void setBidLow(double bidLow) {
    this.bidLow = bidLow;
  }

  public double getBidClose() {
    return bidClose;
  }

  public void setBidClose(double bidClose) {
    this.bidClose = bidClose;
  }

  public double getAskVolume() {
    return askVolume;
  }

  public void setAskVolume(double askVolume) {
    this.askVolume = askVolume;
  }

  public double getBidVolume() {
    return bidVolume;
  }

  public void setBidVolume(double bidVolume) {
    this.bidVolume = bidVolume;
  }

  public Currency getCurrency1() {
    return currency1;
  }

  public void setCurrency1(Currency currency1) {
    this.currency1 = currency1;
  }

  public Currency getCurrency2() {
    return currency2;
  }

  public void setCurrency2(Currency currency2) {
    this.currency2 = currency2;
  }

  public TimeWindowType getTimeWindowType() {
    return timeWindowType;
  }

  public void setTimeWindowType(TimeWindowType timeWindowType) {
    this.timeWindowType = timeWindowType;
  }

  public HistoryDataKBar convertToHistorydataKBar(){

    Instrument instrument = new Instrument(this.getCurrency1() + "/" + this.getCurrency2());
    Long openTime = this.getStartTime();
    TimeWindowType timeWindowType = this.getTimeWindowType();

    double askOpen = this.getAskOpen();
    double askHigh = this.getAskHigh();
    double askLow = this.getAskLow();
    double askClose = this.getAskClose();
    double askVolume = this.getAskVolume();

    double bidOpen = this.getBidOpen();
    double bidHigh = this.getBidHigh();
    double bidLow = this.getBidLow();
    double bidClose = this.getBidClose();
    double bidVolume = this.getBidVolume();


    OHLC ohlc = new OHLC(askOpen, askHigh, askLow, askClose, askVolume,
            bidOpen, bidHigh, bidLow, bidClose, bidVolume);
    HistoryDataKBar kbar = new HistoryDataKBar(instrument, timeWindowType, openTime, openTime + timeWindowType.getTimeInMillis(), ohlc);
    return kbar;
  }

  private long startTime;
  private double askOpen;
  private double askHigh;
  private double askLow;
  private double askClose;
  private double bidOpen;
  private double bidHigh;
  private double bidLow;
  private double bidClose;
  private double askVolume;
  private double bidVolume;
  private Currency currency1;
  private Currency currency2;
  private TimeWindowType timeWindowType;
}