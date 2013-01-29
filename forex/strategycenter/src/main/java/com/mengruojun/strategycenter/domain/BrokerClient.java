package com.mengruojun.strategycenter.domain;

import com.mengruojun.strategycenter.domain.enumerate.Currency;

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
  private String clientType;
  private String clientName;
  private String StrategyName;
  private Double accountLeverage = 100.0;
  private Currency baseCurrency = Currency.USD;

  private Double startMoney = 10000.0;
  private Double currentMoney = startMoney;


  /**
   * Portfolio
   */
  private List<Position> openPositions = new ArrayList<Position>(); //todo init position list
  //private List<Order> orders = new ArrayList<Order>(); //todo init position list
  private List<Position> closedPositions = new ArrayList<Position>(); //todo init position list

}
