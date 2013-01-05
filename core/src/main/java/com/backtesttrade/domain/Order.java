package com.backtesttrade.domain;

import com.historydatacenter.model.Instrument;
import com.historydatacenter.model.enumerate.Currency;

import java.util.UUID;

/**
 * order domain
 */
public class Order {
  Long orderTime;
  Instrument instrument = new Instrument(Currency.EUR, Currency.USD);
  Direction direction;
  Double orderPrice;
  OrderType orderType;
  Double mount;

  Double stopLossInPips;
  Double takeProfitInPips;
  /**
   * when it is a "close" type order,
   */
  Position targetPosition;


  // close action should be executed by trading processor


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

  public Long getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Long orderTime) {
    this.orderTime = orderTime;
  }

  public Double getOrderPrice() {
    return orderPrice;
  }

  public void setOrderPrice(Double orderPrice) {
    this.orderPrice = orderPrice;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderType orderType) {
    this.orderType = orderType;
  }

  public Double getMount() {
    return mount;
  }

  public void setMount(Double mount) {
    this.mount = mount;
  }

  public Position getTargetPosition() {
    return targetPosition;
  }

  public void setTargetPosition(Position targetPosition) {
    this.targetPosition = targetPosition;
  }

  public enum OrderType {
    open, close
  }
}
