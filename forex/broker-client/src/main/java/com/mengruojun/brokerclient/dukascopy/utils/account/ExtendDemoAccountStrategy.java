package com.mengruojun.brokerclient.dukascopy.utils.account;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.utils.TradingUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This is a Dukascopy extendDemoAccountStrategy. It will open a position and close it when it starts, and will do it again once a week(at Monday 00:00:00);
 * So that we can use the demo account without expiration.
 */

public class ExtendDemoAccountStrategy implements IStrategy {
  static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
  boolean doneFirstTrade = false;
  boolean openFirstTrade = false;

  static{
    sdf.setTimeZone(TradingUtils.GMT);
  }
  private boolean isTest = false;
  private IEngine engine = null;
  private IContext context = null;
  private IIndicators indicators = null;
  private int tagCounter = 0;
  private double[] ma1 = new double[Instrument.values().length];
  private IConsole console;
  Logger logger = Logger.getLogger(this.getClass());

  private String orderLabel ;

  private List<Instrument> dukascopyInstrumentList = DukascopyUtils.getInterestInstrumentList();


  public void onStart(final IContext context) throws JFException {
    this.context = context;
    engine = context.getEngine();
    indicators = context.getIndicators();
    this.console = context.getConsole();
    console.getOut().println("Started");

  }


  public void onStop() throws JFException {
    for (IOrder order : engine.getOrders()) {
      order.close();
    }
    console.getOut().println("Stopped");
  }

  public void onTick(Instrument instrument, ITick tick) throws JFException {
  }

  public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    if (instrument.equals(Instrument.EURUSD)) {
      if (!openFirstTrade || period.equals(Period.WEEKLY)) {
        doMinimumTrade();
        openFirstTrade = true;
      }
    }

    if (instrument.equals(Instrument.EURUSD) && period == Period.TEN_SECS) {
      if(isTest){
        closeOrders();
        doneFirstTrade = true;
      }
    }
  }

  private void closeOrders() throws JFException {
    IOrder order = engine.getOrder(orderLabel);
    if(order !=null && order.getState().equals(IOrder.State.FILLED)){
      order.close();
    }
  }

  private void doMinimumTrade() throws JFException {
    String label = "extend_" + sdf.format(new Date());
    IOrder order = engine.submitOrder(label, Instrument.EURUSD, IEngine.OrderCommand.SELL, 0.001, 0, 0);
    order.waitForUpdate(2000);
    orderLabel = label;
  }

  public void onMessage(IMessage message) throws JFException {
  }

  public void onAccount(IAccount account) throws JFException {
    //registerClient();
  }

  public void setTest(boolean test) {
    isTest = test;
  }

  public boolean isDoneFirstTrade() {
    return doneFirstTrade;
  }
}
