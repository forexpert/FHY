package com.mengruojun.jms.domain;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;
import com.mengruojun.common.utils.TradingUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TradeCommandMessage
 */
public class TradeCommandMessage implements Serializable {
  private static final long serialVersionUID = 1L;


  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  Instrument instrument = null; //new Instrument(Currency.EUR, Currency.USD);
  Direction direction;
  /**
   * Amount in K unit
   */
  Double amount;
  Double openPrice;
  Double closePrice;
  Double stopLossInPips;
  Double takeProfitInPips;
  String positionId;
  private TradeCommandType tradeCommandType;

  /**
   * What time does this TradeCommandMessage work for.
   *
   * Usually, it is used for verify and monitoring
   */
  private Long analyzeTime;

  public TradeCommandMessage(Long analyzeTime) {
    this.analyzeTime = analyzeTime;
  }

  @Override
  public String toString() {
    ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("analyzeTime", this.analyzeTime)
            .append("analyzeTime_H", sdf.format(new Date(this.analyzeTime)))
            .append("positionId", positionId)
            .append("instrument", this.instrument)
            .append("amount", this.amount)
            .append("openPrice", this.openPrice)
            .append("closePrice", closePrice)
            .append("stopLossInPips", stopLossInPips)
            .append("tradeCommandType", tradeCommandType);
    return sb.toString();
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getOpenPrice() {
    return openPrice;
  }

  public void setOpenPrice(Double openPrice) {
    this.openPrice = openPrice;
  }

  public Double getClosePrice() {
    return closePrice;
  }

  public void setClosePrice(Double closePrice) {
    this.closePrice = closePrice;
  }

  public Double getStopLossInPips() {
    return stopLossInPips;
  }

  public void setStopLossInPips(Double stopLossInPips) {
    this.stopLossInPips = stopLossInPips;
  }

  public Double getTakeProfitInPips() {
    return takeProfitInPips;
  }

  public void setTakeProfitInPips(Double takeProfitInPips) {
    this.takeProfitInPips = takeProfitInPips;
  }

  public String getPositionId() {
    return positionId;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
  }

  public TradeCommandType getTradeCommandType() {
    return tradeCommandType;
  }

  public void setTradeCommandType(TradeCommandType tradeCommandType) {
    this.tradeCommandType = tradeCommandType;
  }

  public Long getAnalyzeTime() {
    return analyzeTime;
  }

  public void setAnalyzeTime(Long analyzeTime) {
    this.analyzeTime = analyzeTime;
  }
}
