package com.mengruojun.common.domain;

import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.PositionStatus;

import java.io.Serializable;
import java.util.UUID;

/**
 * Position object. including pending position(orders), open position and closed positions
 */
public class Position extends GeneratedIdBaseEntity {
  public enum CloseReason{
    Reach_ST,
    Reach_TP,
    Manually,
    ReachMarginCall,
    Other
  }
  private static final long serialVersionUID = 1L;

  String positionId;// = UUID.randomUUID().toString();
  Long openTime;
  Long closeTime;
  Instrument instrument = null; //new Instrument(Currency.EUR, Currency.USD);
  PositionStatus status;
  CloseReason closeReason = null;
  Direction direction;

  /* K as unit  */
  Double amount;
  Double openPrice;
  Double closePrice;
  Double stopLossInPips;
  Double takeProfitInPips;


  // close action should be executed by trading processor


  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Long getOpenTime() {
    return openTime;
  }

  public void setOpenTime(Long openTime) {
    this.openTime = openTime;
  }

  public Long getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(Long closeTime) {
    this.closeTime = closeTime;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public PositionStatus getStatus() {
    return status;
  }

  public void setStatus(PositionStatus status) {
    this.status = status;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
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

  public CloseReason getCloseReason() {
    return closeReason;
  }

  public void setCloseReason(CloseReason closeReason) {
    this.closeReason = closeReason;
  }
}
