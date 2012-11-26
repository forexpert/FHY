package com.backtesttrade.domain;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 11/23/12
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class StrategyAction {

  Double actionPrice;
  PriceType priceType;
  Double mount;
  OperationType operationType;
  Direction direction;
  Double stopLossInPips;
  Double takeProfitInPips;

  public StrategyAction(Double actionPrice, PriceType priceType, Double mount, OperationType operationType, Direction direction,
                        Double stopLossInPips, Double takeProfitInPips) {
    this.actionPrice = actionPrice;
    this.priceType = priceType;
    this.mount = mount;
    this.operationType = operationType;
    this.direction = direction;
    this.stopLossInPips = stopLossInPips;
    this.takeProfitInPips = takeProfitInPips;
  }

  public enum PriceType{
    setManually,
    atMarket
  }

  public Double getActionPrice() {
    return actionPrice;
  }

  public void setActionPrice(Double actionPrice) {
    this.actionPrice = actionPrice;
  }

  public PriceType getPriceType() {
    return priceType;
  }

  public void setPriceType(PriceType priceType) {
    this.priceType = priceType;
  }

  public OperationType getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationType operationType) {
    this.operationType = operationType;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
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

  public Double getMount() {
    return mount;
  }

  public void setMount(Double mount) {
    this.mount = mount;
  }
}
