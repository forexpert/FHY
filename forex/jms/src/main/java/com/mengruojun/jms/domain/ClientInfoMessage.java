package com.mengruojun.jms.domain;

import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.BrokerType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Client Info Message includes:
 * 1. Client Id/name/type
 * 2. Account leverage
 * 3. Account money
 * 4. Account Strategy name
 * 5. Orders and Positions
 */
public class ClientInfoMessage implements Serializable {
  private static final long serialVersionUID = 1L;
  private String ClientId;
  private BrokerType brokerType;
  private double leverage;
  private double currentBalance;
  private double currentEquity;
  private String StrategyId;
  private Currency baseCurrency;

  private List<Position> pendingPositionList = new ArrayList<Position>();
  private List<Position> openPositionList = new ArrayList<Position>();
  private List<Position> closedPositionList = new ArrayList<Position>();

  public String getClientId() {
    return ClientId;
  }

  public void setClientId(String clientId) {
    ClientId = clientId;
  }

  public Currency getBaseCurrency() {
    return baseCurrency;
  }

  public void setBaseCurrency(Currency baseCurrency) {
    this.baseCurrency = baseCurrency;
  }

  public BrokerType getBrokerType() {
    return brokerType;
  }

  public void setBrokerType(BrokerType brokerType) {
    this.brokerType = brokerType;
  }

  public double getLeverage() {
    return leverage;
  }

  public void setLeverage(double leverage) {
    this.leverage = leverage;
  }

  public double getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(double currentBalance) {
    this.currentBalance = currentBalance;
  }

  public double getCurrentEquity() {
    return currentEquity;
  }

  public void setCurrentEquity(double currentEquity) {
    this.currentEquity = currentEquity;
  }

  public String getStrategyId() {
    return StrategyId;
  }

  public void setStrategyId(String strategyId) {
    StrategyId = strategyId;
  }

  public List<Position> getPendingPositionList() {
    return pendingPositionList;
  }

  public void setPendingPositionList(List<Position> pendingPositionList) {
    this.pendingPositionList = pendingPositionList;
  }

  public List<Position> getOpenPositionList() {
    return openPositionList;
  }

  public void setOpenPositionList(List<Position> openPositionList) {
    this.openPositionList = openPositionList;
  }

  public List<Position> getClosedPositionList() {
    return closedPositionList;
  }

  public void setClosedPositionList(List<Position> closedPositionList) {
    this.closedPositionList = closedPositionList;
  }
}
