package com.backtesttrade.domain;

import com.historydatacenter.model.enumerate.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/26/12
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Account {

  /**
   * if accountLeverage is 100 (as default), and the currentMoney is $100, the maximum open position values $10,000.
   */
  private Double accountLeverage = 100.0;
  private Currency baseCurrency = Currency.USD;

  private Double startMoney = 10000.0;
  private Double currentMoney = startMoney;


  /**
   * Portfolio
   */
  private List<Position> openPositions = new ArrayList<Position>(); //todo init position list
  private List<Position> pendingPositions = new ArrayList<Position>(); //todo init position list
  private List<Position> closedPositions = new ArrayList<Position>(); //todo init position list

  /**
   * typical account uses default values for its member variables
   *
   * @return
   */
  public static Account getTypicalAccount() {
    return new Account();
  }

  public Account() {
  }

  public Account(Double accountLeverage, Currency baseCurrency, Double startMoney, Double currentMoney,
                 List<Position> openPositions, List<Position> pendingPositions, List<Position> closedPositions) {
    this.accountLeverage = accountLeverage;
    this.baseCurrency = baseCurrency;
    this.startMoney = startMoney;
    this.currentMoney = currentMoney;
    this.openPositions = openPositions;
    this.pendingPositions = pendingPositions;
    this.closedPositions = closedPositions;
  }

//todo add portfolio operation methods


  public double getStartMoney() {
    return startMoney;
  }

  public void setStartMoney(double startMoney) {
    this.startMoney = startMoney;
  }

  public List<Position> getOpenPositions() {
    return openPositions;
  }

  public void setOpenPositions(List<Position> openPositions) {
    this.openPositions = openPositions;
  }

  public List<Position> getPendingPositions() {
    return pendingPositions;
  }

  public void setPendingPositions(List<Position> pendingPositions) {
    this.pendingPositions = pendingPositions;
  }

  public List<Position> getClosedPositions() {
    return closedPositions;
  }

  public void setClosedPositions(List<Position> closedPositions) {
    this.closedPositions = closedPositions;
  }

  public Double getAccountLeverage() {
    return accountLeverage;
  }

  public void setAccountLeverage(Double accountLeverage) {
    this.accountLeverage = accountLeverage;
  }

  public Currency getBaseCurrency() {
    return baseCurrency;
  }

  public void setBaseCurrency(Currency baseCurrency) {
    this.baseCurrency = baseCurrency;
  }

  public Double getCurrentMoney() {
    return currentMoney;
  }

  public void setCurrentMoney(Double currentMoney) {
    this.currentMoney = currentMoney;
  }
}
