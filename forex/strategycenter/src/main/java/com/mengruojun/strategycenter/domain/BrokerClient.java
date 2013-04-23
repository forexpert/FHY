package com.mengruojun.strategycenter.domain;


import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.*;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;

import java.util.*;
import java.util.Currency;

/**
 * This class presents broker clients but only used for client manager.
 * Which has some properties like:
 * client type
 * client name
 * client strategy
 * and etc.
 * <p/>
 * but it doesn't do anything about broker, like connection to broker server or so.
 */
public class BrokerClient {

  /**
   * It means, if trade 1 million USD/JPY, 33 dollars commission will be charged.
   * E.g. buy EUR/USD 0.001M at 1.3000; it will take 0.001*1,000,000 *1.3 /1000000 * 33=0.0429 dollars
   */
  private double commissionPerM = 33;

  public BrokerClient(BrokerType brokerType, String clientId, String strategyName, Double accountLeverage,
                      Currency baseCurrency, Double startBalance, Double currentBalance, List<Position> openPositions,
                      List<Position> pendingPositions, List<Position> closedPositions) {
    this.brokerType = brokerType;
    this.clientId = clientId;
    this.strategyName = strategyName;
    this.accountLeverage = accountLeverage;
    this.baseCurrency = baseCurrency;
    this.startBalance = startBalance;
    this.currentBalance = currentBalance;
    this.openPositions = openPositions;
    this.pendingPositions = pendingPositions;
    this.closedPositions = closedPositions;
  }

  private BrokerType brokerType;
  private String clientId;
  private String strategyName;
  private Double accountLeverage;
  private Currency baseCurrency;

  private Double startBalance;
  private Double currentBalance;


  /**
   * Portfolio
   */
  private List<Position> openPositions = new ArrayList<Position>(); //todo init position list
  private List<Position> pendingPositions = new ArrayList<Position>(); //todo init position list
  private List<Position> closedPositions = new ArrayList<Position>(); //todo init position list

  /**
   * After strategy instance did the analysis on this client at a certain time point, it should add the analysis result into this map.
   * The analysis result should be a list of TradeCommandMessage. Even if the strategy doesn't want the client do any trade,
   * it should pass in list of TradeCommandMessage, which size is 0.
   */
  private Map<Long, List<TradeCommandMessage>> analyzedTradeCommandMap = Collections.synchronizedSortedMap(new TreeMap<Long, List<TradeCommandMessage>>());

  public static final long ANALYZED_TRADE_MAP_MAX_NUM=20;
  /**
   * Util method
   */

  /**
   * @param currentBars is a map for all subscribed instruments' ask close price
   * @return current left margin
   */
  public Double getLeftMargin(Map<Instrument, HistoryDataKBar> currentBars) {
    Double totalMargin = currentBalance;
    return currentBalance - getUsedMargin(currentBars);

  }

  public Double getUsedMargin(Map<Instrument, HistoryDataKBar> currentBars) {
    Double usedMargin = 0d;
    for (Position p : openPositions) {
      Double closePrice = currentBars.get(p.getInstrument()).getOhlc().getAskClose();
      usedMargin += TradingUtils.calculateMargin(accountLeverage, closePrice, p, com.mengruojun.common.domain.enumerate.Currency.fromJDKCurrency(baseCurrency));
    }
    return usedMargin;
  }

  //============ Utility ==========

  public Double getEquity(Map<Instrument, HistoryDataKBar> currentBars){
    double equity = currentBalance;
    return equity + getTotalUnRealizedPAndL(currentBars);
  }


  public Double getOpenCommission(Position p, Map<Instrument, HistoryDataKBar> currentBars){
    Double tradingVolume = 0d;
    if (p.getInstrument().getCurrency1() != com.mengruojun.common.domain.enumerate.Currency.fromJDKCurrency(baseCurrency)) {
      tradingVolume = currentBars.get(p.getInstrument()).getOhlc().getAskClose() * p.getAmount() * TradingUtils.getGolbalAmountUnit() ;
    } else {
      tradingVolume =  p.getAmount() * TradingUtils.getGolbalAmountUnit() ;
    }
    return tradingVolume  /1000000.0 * 33.0 /2; // /2 means split commission for open and close
  }

  public Double getClosedCommission(Position p, Map<Instrument, HistoryDataKBar> currentBars){
    return getOpenCommission(p, currentBars);
  }
  private double getTotalUnRealizedPAndL(Map<Instrument, HistoryDataKBar> currentBars) {
    double unrealizedPL = 0;
    for(Position openPosition : openPositions){
      TradingUtils.assertStat(openPosition.getStatus() == PositionStatus.OPEN);
      if(openPosition.getDirection() == Direction.Long){
        unrealizedPL += TradingUtils.calculateOpenPnL(openPosition, currentBars, com.mengruojun.common.domain.enumerate.Currency.fromJDKCurrency(baseCurrency));
      }
    }
    return unrealizedPL;
  }

  public Position getOpenPositionById(String id){
    for(Position openPosition : openPositions){
      if(openPosition.getPositionId().equals(id)){
        return openPosition;
      }
    }
    return null;
  }

  public Position getPendingPositionById(String id){
    for(Position openPosition : pendingPositions){
      if(openPosition.getPositionId().equals(id)){
        return openPosition;
      }
    }
    return null;
  }

  //=========getter and setter==============

  public BrokerType getBrokerType() {
    return brokerType;
  }

  public void setBrokerType(BrokerType brokerType) {
    this.brokerType = brokerType;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
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

  public Double getStartBalance() {
    return startBalance;
  }

  public void setStartBalance(Double startBalance) {
    this.startBalance = startBalance;
  }

  public Double getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(Double currentBalance) {
    this.currentBalance = currentBalance;
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

  public Map<Long, List<TradeCommandMessage>> getAnalyzedTradeCommandMap() {
    return analyzedTradeCommandMap;
  }

  public void setAnalyzedTradeCommandMap(Map<Long, List<TradeCommandMessage>> analyzedTradeCommandMap) {
    this.analyzedTradeCommandMap = analyzedTradeCommandMap;
  }

  public double getCommissionPerM() {
    return commissionPerM;
  }

  public void setCommissionPerM(double commissionPerM) {
    this.commissionPerM = commissionPerM;
  }
}
