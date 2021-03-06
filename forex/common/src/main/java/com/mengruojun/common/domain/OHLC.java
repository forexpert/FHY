package com.mengruojun.common.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Embeddable;
import java.text.DecimalFormat;

/**
 * open, high, low, close data
 */
@Embeddable
public class OHLC{
  static DecimalFormat decimalFormat = new DecimalFormat("0.0");
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

    public  OHLC (){

    }

    public OHLC(double askOpen, double askHigh, double askLow, double askClose, double askVolume,
                double bidOpen, double bidHigh, double bidLow, double bidClose, double bidVolume) {
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


  @Override
  public String toString(){
    ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("askOpen", this.askOpen)
            .append("askHigh", this.askHigh)
            .append("askLow", this.askLow)
            .append("askClose", this.askClose)
            .append("askVolume", OHLC.decimalFormat.format(this.askVolume))
            .append("bidOpen", this.bidOpen)
            .append("bidHigh", this.bidHigh)
            .append("bidLow", this.bidLow)
            .append("bidClose", this.bidClose)
            .append("bidVolume", OHLC.decimalFormat.format(this.bidVolume));
    return sb.toString();

  }

  @Override
  public boolean equals(Object o){
    if (this == o) {
      return true;
    }
    if (!(o instanceof OHLC)) {
      return false;
    }

    final OHLC ohlc = (OHLC) o;

    return this.toString().equals(ohlc.toString());
  }

}
