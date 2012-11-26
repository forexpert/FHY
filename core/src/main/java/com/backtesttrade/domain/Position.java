package com.backtesttrade.domain;

import com.historydatacenter.model.Instrument;
import com.historydatacenter.model.enumerate.Currency;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/26/12
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Position {
  Long openTime;
  Long closeTime;
  Instrument instrument = new Instrument(Currency.EUR, Currency.USD) ;
  PositionStatus status;
  Direction direction;
  Double openPrice;
  Double closePrice;
  Double stopLossInPips;
  Double takeProfitInPips;

  public static Position createOpenPositionInstance(){
    Position position = null;
    return position;
  }

  // close action should be executed by trading processor


  public enum PositionStatus {
    Open, closed, pending
  }
}
