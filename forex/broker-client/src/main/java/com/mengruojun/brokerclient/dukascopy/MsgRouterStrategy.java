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
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.utils.JMSSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.Arrays;
import java.util.List;

/**
 * This is a Dukascopy Strategy, which plays a msg sender and receiver roles.
 * It doesn't do anything about strategy, but delegate the analysis work to JMS topic listener.
 */
public class MsgRouterStrategy implements IStrategy {

  private IEngine engine = null;
  private IContext context = null;
  private IIndicators indicators = null;
  private int tagCounter = 0;
  private double[] ma1 = new double[Instrument.values().length];
  private IConsole console;
  Logger logger = Logger.getLogger(this.getClass());
  private List<Instrument> dukascopyInstrumentList = Arrays.asList(Instrument.values());
  private String clientId = null;

  private TradeCommandReceiver tradeCommandReceiver;

  @Autowired
  private JMSSender clientInfoSender;


  @Autowired
  private JmsTemplate jsmTemplate;
  @Autowired
  private Destination jmsTopicTradeCommand;
  private String strategyName;

  public void onStart(final IContext context) throws JFException {
    this.context = context;
    engine = context.getEngine();
    indicators = context.getIndicators();
    this.console = context.getConsole();
    console.getOut().println("Started");

    registerClient();
    startTradeCommandListener();
  }

  /**
   * here we start the trade command listener to listen any trade command sent by client manager, then send these
   * command to dukascopy server by Dukascopy JForex API
   */
  private void startTradeCommandListener() {
    tradeCommandReceiver = new TradeCommandReceiver(this.clientId, context, jsmTemplate, jmsTopicTradeCommand);
    new Thread() {
      public void run() {
        try {
          tradeCommandReceiver.receive();
        } catch (Exception e) {
          logger.error("", e);
          //todo cmeng --jiji 一旦这里出异常， tradeCommandReceiver就会退出运行，这里需要有重启和通知的功能
        }
      }
    }.start();
    logger.info("tradeCommandReceiver started!");

  }

  /**
   * register client by JMS to the Client Manager
   */
  private void registerClient() throws JFException {
    ClientInfoMessage cim = DukascopyUtils.generateClientInfoMessage(BrokerType.DukascopyDemo, this.context, strategyName);
    this.clientId = cim.getClientId();
    clientInfoSender.sendObjectMessage(cim);
  }

  public void onStop() throws JFException {
    for (IOrder order : engine.getOrders()) {
      order.close();
    }
    console.getOut().println("Stopped");
  }

  public void onTick(Instrument instrument, ITick tick) throws JFException {
  }

  public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {

  }

  public void onMessage(IMessage message) throws JFException {
  }

  public void onAccount(IAccount account) throws JFException {
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  public String getStrategyName() {
    return strategyName;
  }
}
