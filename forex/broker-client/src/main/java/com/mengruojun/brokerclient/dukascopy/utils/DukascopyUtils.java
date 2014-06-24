package com.mengruojun.brokerclient.dukascopy.utils;

import com.dukascopy.api.*;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.PositionStatus;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.ClientInfoMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 2/1/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DukascopyUtils {

  /**
   * The global amount unit is K, but in dukascopy is million. So
   *
   * @param k k is amount based on the unit K
   * @return
   */
  public static double toDukascopyAmountFromK(double k) {
    return k / TradingUtils.getGolbalAmountUnit();
  }

  public static TimeWindowType convertPeriodToTimeWindowType(Period period){
      if(period == Period.TEN_SECS)  return TimeWindowType.S10;
      if(period == Period.TWENTY_SECS) return TimeWindowType.S20;
      if(period == Period.THIRTY_SECS) return TimeWindowType.S30;
      if(period == Period.ONE_MIN) return TimeWindowType.M1;
      if(period == Period.FIVE_MINS) return TimeWindowType.M5;
      if(period == Period.TEN_MINS) return TimeWindowType.M10;
      if(period == Period.THIRTY_MINS) return TimeWindowType.M30;
      if(period == Period.ONE_HOUR) return TimeWindowType.H1;
      if(period == Period.FOUR_HOURS) return TimeWindowType.H4;
      if(period == Period.DAILY) return TimeWindowType.D1;

      return null;
  }

  public static List<Instrument> getInterestInstrumentList(){
    List<Instrument> reList = new ArrayList<Instrument>();
    List<com.mengruojun.common.domain.Instrument> list = TradingUtils.getInterestInstrumentList();
    for(com.mengruojun.common.domain.Instrument instrument:list){
      reList.add(DukascopyUtils.toDukascopyInstrument(instrument));
    }
    return reList;
  }

  public static List<Instrument> getAllInstrument(){
    return Arrays.asList(Instrument.values());
  }
  public static com.mengruojun.common.domain.Instrument fromDukascopyInstrument(Instrument instrument) {
    Currency c1 = Currency.fromJDKCurrency(instrument.getPrimaryCurrency());
    Currency c2 = Currency.fromJDKCurrency(instrument.getSecondaryCurrency());
    return new com.mengruojun.common.domain.Instrument(c1, c2);

  }

  public static Instrument toDukascopyInstrument(com.mengruojun.common.domain.Instrument instrument) {
    String c1 = instrument.getCurrency1().toString();
    String c2 = instrument.getCurrency2().toString();
    return Instrument.fromString(c1 + "/" + c2);

  }

  public static ClientInfoMessage generateClientInfoMessage(BrokerType brokerType, IContext context, String strategyName) throws JFException {

    ClientInfoMessage cim = new ClientInfoMessage();
    //set base client account info
    cim.setBrokerType(brokerType);
    cim.setClientId(context.getAccount().getAccountId());
    cim.setStrategyId(strategyName == null ? "noStrategy" : strategyName);// to be determined by client manager
    cim.setBaseCurrency(context.getAccount().getCurrency());
    cim.setCurrentBalance(context.getAccount().getBalance());
    cim.setCurrentEquity(context.getAccount().getEquity());
    cim.setLeverage(context.getAccount().getLeverage());

    // set position info
    List<IOrder> openingAndPendingPositionList = context.getEngine().getOrders();
    List<Position> openPositionList = new ArrayList<Position>();
    List<Position> pendingPositionList = new ArrayList<Position>();
    for (IOrder order : openingAndPendingPositionList) {
      if (order.getState().equals(IOrder.State.FILLED)) {  //opening position
        Position position = new Position();
        position.setStatus(PositionStatus.OPEN);
        position.setPositionId(order.getId());
        position.setOpenPrice(order.getOpenPrice());
        position.setInstrument(DukascopyUtils.fromDukascopyInstrument(order.getInstrument()));
        position.setDirection(order.getOrderCommand().isLong() ? Direction.Long : Direction.Short);
        position.setAmount(order.getAmount());
        position.setOpenTime(order.getFillTime());
        openPositionList.add(position);
      } else if (order.getState().equals(IOrder.State.OPENED)) {  //pending position
        Position position = new Position();
        position.setStatus(PositionStatus.PENDING);
        position.setPositionId(order.getId());
        position.setOpenPrice(order.getOpenPrice());
        position.setInstrument(DukascopyUtils.fromDukascopyInstrument(order.getInstrument()));
        position.setDirection(order.getOrderCommand().isLong() ? Direction.Long : Direction.Short);
        position.setAmount(order.getAmount());
        position.setOpenTime(order.getFillTime());
        pendingPositionList.add(position);
      }
    }
    cim.setOpenPositionList(openPositionList);
    cim.setPendingPositionList(pendingPositionList);

    //clientInfoSender.sendObjectMessage(cim);
    return cim;

  }

}
