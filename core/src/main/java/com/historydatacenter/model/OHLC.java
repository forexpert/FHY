package com.historydatacenter.model;

import javax.persistence.Embeddable;

/**
 * open, high, low, close data
 */
@Embeddable
public class OHLC{
  private double askOpen;
  private double askHigh;
  private double askLow;
  private double askClose;
  private double bidOpen;
  private double bidHigh;
  private double bidLow;
  private double bidClose;

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
}
