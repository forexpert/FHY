package com.mengruojun.strategycenter.domain;


import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.BrokerType;
import java.util.Currency;

import java.util.ArrayList;
import java.util.List;

/*
This class presents broker clients but only used for client manager.
Which has some properties like:
client type
client name
client strategy
and etc.

but it doesn't do anything about broker, like connection to broker server or so.
 */
public class BrokerClient {

    public BrokerClient(BrokerType brokerType, String clientName, String strategyName, Double accountLeverage,
                        Currency baseCurrency, Double startMoney, Double currentMoney, List<Position> openPositions,
                        List<Position> pendingPositions, List<Position> closedPositions) {
        this.brokerType = brokerType;
        this.clientName = clientName;
        StrategyName = strategyName;
        this.accountLeverage = accountLeverage;
        this.baseCurrency = baseCurrency;
        this.startMoney = startMoney;
        this.currentMoney = currentMoney;
        this.openPositions = openPositions;
        this.pendingPositions = pendingPositions;
        this.closedPositions = closedPositions;
    }

    private BrokerType brokerType;
    private String clientName;
    private String StrategyName;
    private Double accountLeverage;
    private Currency baseCurrency;

    private Double startMoney;
    private Double currentMoney;


    /**
     * Portfolio
     */
    private List<Position> openPositions = new ArrayList<Position>(); //todo init position list
    private List<Position> pendingPositions = new ArrayList<Position>(); //todo init position list
    private List<Position> closedPositions = new ArrayList<Position>(); //todo init position list


    public BrokerType getBrokerType() {
        return brokerType;
    }

    public void setBrokerType(BrokerType brokerType) {
        this.brokerType = brokerType;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getStrategyName() {
        return StrategyName;
    }

    public void setStrategyName(String strategyName) {
        StrategyName = strategyName;
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

    public Double getStartMoney() {
        return startMoney;
    }

    public void setStartMoney(Double startMoney) {
        this.startMoney = startMoney;
    }

    public Double getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(Double currentMoney) {
        this.currentMoney = currentMoney;
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
}
