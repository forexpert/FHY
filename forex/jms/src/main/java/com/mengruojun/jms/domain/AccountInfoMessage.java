package com.mengruojun.jms.domain;

/**
 * Account Info Message includes:
 * 1. Account Id/name
 * 2. Account leverage
 * 3. Account money
 * 4. Account Strategy name
 * 5. Orders and Positions
 */
public class AccountInfoMessage {
  private String accountName;
  private double leverage;
  private double initialMoney;
  private double currenyMoney;

}
