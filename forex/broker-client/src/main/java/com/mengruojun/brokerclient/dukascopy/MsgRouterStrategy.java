package com.mengruojun.brokerclient.dukascopy;

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
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.jms.domain.enumerate.TimeWindowType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * This is a Dukascopy Strategy, which plays a msg sender and receiver roles.
 * It doesn't do anything about strategy, but delegate the analysis work to JMS topic listener.
 */
@Service("msgRouterStrategy")
public class MsgRouterStrategy implements IStrategy {
  private IEngine engine = null;
  private IContext context = null;
  private IIndicators indicators = null;
  private int tagCounter = 0;
  private double[] ma1 = new double[Instrument.values().length];
  private IConsole console;
  Logger logger = Logger.getLogger(this.getClass());
  private List<Instrument> dukascopyInstrumentList = Arrays.asList(Instrument.values());

  @Autowired
  private JMSSender marketDataSender;
  @Autowired
  private JMSSender accountInfoSender;


  public void onStart(final IContext context) throws JFException {
    this.context = context;
    engine = context.getEngine();
    indicators = context.getIndicators();
    this.console = context.getConsole();
    console.getOut().println("Started");
    new Thread(){
      public void run(){

        try {
          context.getAccount();
          //todo cmeng send account info
          engine.getOrders();  //open and pending order
          //context.getHistory().getOrdersHistory();     // closed orders
        } catch (JFException e) {
          logger.error("", e);
        }
        //todo cmeng send account information
        try {
          Thread.sleep(1000*60L);       // sync account info every minutes
        } catch (InterruptedException e) {
          logger.error("", e);
        }
      }
    }.start();
  }

  public void onStop() throws JFException {
    for (IOrder order : engine.getOrders()) {
      order.close();
    }
    console.getOut().println("Stopped");
  }

  public void onTick(Instrument instrument, ITick tick) throws JFException {
    /*if (ma1[instrument.ordinal()] == -1) {
      ma1[instrument.ordinal()] = indicators.ema(instrument, Period.TEN_SECS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, 14, 1);
    }
    double ma0 = indicators.ema(instrument, Period.TEN_SECS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, 14, 0);
    if (ma0 == 0 || ma1[instrument.ordinal()] == 0) {
      ma1[instrument.ordinal()] = ma0;
      return;
    }

    double diff = (ma1[instrument.ordinal()] - ma0) / (instrument.getPipValue());

    if (positionsTotal(instrument) == 0) {
      if (diff > 1) {
        engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.SELL, 0.001, 0, 0, tick.getAsk()
                + instrument.getPipValue() * 10, tick.getAsk() - instrument.getPipValue() * 15);
      }
      if (diff < -1) {
        engine.submitOrder(getLabel(instrument), instrument, IEngine.OrderCommand.BUY, 0.001, 0, 0, tick.getBid()
                - instrument.getPipValue() * 10, tick.getBid() + instrument.getPipValue() * 15);
      }
    }
    ma1[instrument.ordinal()] = ma0;*/
  }

  public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {
    if(period.equals(Period.TEN_SECS)){
      TimeWindowType twt = TimeWindowType.S10;
      MarketDataMessage mdm = new MarketDataMessage(askBar.getTime(),
              askBar.getOpen(),askBar.getHigh(),askBar.getLow(),askBar.getClose(),
              bidBar.getOpen(),bidBar.getHigh(),bidBar.getLow(),bidBar.getClose(),
              askBar.getVolume(),bidBar.getVolume(),instrument.getPrimaryCurrency(),
              instrument.getSecondaryCurrency(),twt);

      marketDataSender.sendObjectMessage(mdm);
    }
  }

  //count open positions
  protected int positionsTotal(Instrument instrument) throws JFException {
    int counter = 0;
    for (IOrder order : engine.getOrders(instrument)) {
      if (order.getState() == IOrder.State.FILLED) {
        counter++;
      }
    }
    return counter;
  }

  protected String getLabel(Instrument instrument) {
    String label = instrument.name();
    label = label.substring(0, 2) + label.substring(3, 5);
    label = label + (tagCounter++);
    label = label.toLowerCase();
    return label;
  }

  public void onMessage(IMessage message) throws JFException {
  }

  public void onAccount(IAccount account) throws JFException {
    accountInfoSender.sendTextMessage("AccountState is " + account.getAccountState());
  }
}