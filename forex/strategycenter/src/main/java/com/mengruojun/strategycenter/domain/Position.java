package com.mengruojun.strategycenter.domain;

import com.historydatacenter.model.Instrument;
import com.historydatacenter.model.enumerate.Currency;
import com.mengruojun.strategycenter.domain.enumerate.Direction;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/26/12
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Position {
  String positionId = UUID.randomUUID().toString();
  Long openTime;
  Long closeTime;
  Instrument instrument = new Instrument(Currency.EUR, Currency.USD);
  PositionStatus status;
  Direction direction;
  Double openPrice;
  Double closePrice;
  Double stopLossInPips;
  Double takeProfitInPips;

  public static Position createOpenPositionInstance() {
    Position position = null;
    return position;
  }

  // close action should be executed by trading processor


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

  public enum PositionStatus {
    Open, closed
  }
}
