package com.backtesttrade;

import com.backtesttrade.domain.*;
import com.historydatacenter.model.HistoryDataKBar;

import java.util.ArrayList;
import java.util.List;

/**
 * MarketExecutor is responsible for trying to execute action.
 * rules:
 * 1. if the account doesn't have enough margin, this executor will return failure if a open position action is requested.
 * 2. if the actionPriceType is setManually, and it isn't in the price range of the next bar, the action result is failed.
 * ...
 */
public class MarketExecutor {
  /**
   * execute actions computed by strategy
   * which can be replaced in real trading system.
   *
   * @param account
   * @param actions
   * @param currentBar
   */
  public void executeActions(Account account, List<StrategyAction> actions, HistoryDataKBar lastBar, HistoryDataKBar currentBar) {
    for (StrategyAction action : actions) {
      switch (action.getOperationType()) {
        case Open:
          // try to add an open position to account
          switch (action.getPriceType()) {
            case atMarket:
              //todo
            case setManually:
              //todo

          }

          double openPositionMarginRequire = action.getActionPrice();


          break;
        case Close:
          //todo
          break;
      }
    }
  }


  /**
   * execute orders.
   * it will be called before strategy analysis.
   * Consider pending orders, SL, TP
   *
   * @param account
   * @param lastBar
   */
  public synchronized void executeOrders(Account account, HistoryDataKBar lastBar) {
    //1. deal with pending orders
    List<Order> orders = account.getOrders();
    List<Position> openPositions = account.getOpenPositions();
    List<Position> closePositions = account.getClosedPositions();

    List<Position> executedPositions = new ArrayList<Position>();
    for (Order order : orders) {
      double orderPrice = order.getOrderPrice();
      Direction dir = order.getDirection();
      switch (order.getOrderType()) {
        case open:
          if (dir == Direction.Long) { // case Long, we should look at ask price
            //if (orderPrice <= lastBar.getOhlc().getAskHigh() && orderPrice >= lastBar.getOhlc().getAskLow()) {
            if (priceCheck(dir, orderPrice, lastBar) && moneyCheck(account, order, lastBar)) {
              //the pending order should be executed now
              Position newPosition = createNewPosition(order, lastBar);
              account.addNewOpenPosition(newPosition);
            }
            //}
          } else if (dir == Direction.Short) { // case Short, we should look at bid price
            if (orderPrice <= lastBar.getOhlc().getBidHigh() && orderPrice >= lastBar.getOhlc().getBidLow()) {
              //the pending order should be executed
              pendingPosition.setOpenTime(lastBar.getOpenTime() + 1000);
              pendingPosition.setStatus(Position.PositionStatus.Open);
              executedPositions.add(pendingPosition);
            }
          } else {
            //do nothing
          }
          break;
        case close:
          break;
      }
      //move those executed positions from pendingList to openList
      for (Position executedPosition : executedPositions) {
        orders.remove(executedPosition);
        openPositions.add(executedPosition);
      }


    }
    //2. deal with SL, TP
    for (Position position : openPositions) {

    }

  }

  /**
   * check if the account have enough margin to execute the order.
   * @param account
   * @param order
   * @param lastBar
   * @return
   */
  private boolean moneyCheck(Account account, Order order, HistoryDataKBar lastBar) {
    double orderPrice = order.getOrderPrice();
    Direction dir = order.getDirection();
    Double leftMargin = account.getLeftMargin();

    return false;
  }

  /**
   * check if the orderPrice is qualified during lastBar period
   * @param dir
   * @param orderPrice
   * @param lastBar
   * @return
   */
  private boolean priceCheck(Direction dir, double orderPrice, HistoryDataKBar lastBar) {
    if ((orderPrice <= lastBar.getOhlc().getAskHigh() && orderPrice >= lastBar.getOhlc().getAskLow()) ||
            (orderPrice <= lastBar.getOhlc().getBidHigh() && orderPrice >= lastBar.getOhlc().getBidLow())) {
      return true;
    } else {
      return false;
    }

  }

  private Position createNewPosition(Order order, HistoryDataKBar lastBar) {
    double orderPrice = order.getOrderPrice();
    Direction dir = order.getDirection();
    Position newPosition = new Position();
    newPosition.setStatus(Position.PositionStatus.Open);
    newPosition.setOpenTime(lastBar.getOpenTime() + 1000);
    newPosition.setDirection(dir);
    newPosition.setOpenPrice(orderPrice);
    newPosition.setStopLossInPips(order.getStopLossInPips());
    newPosition.setTakeProfitInPips(order.getTakeProfitInPips());
    return newPosition;

  }
}
