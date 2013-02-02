package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.*;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.PositionStatus;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.ArrayList;
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


    private TradeCommandReceiver tradeCommandReceiver;

    @Autowired
    private JMSSender clientInfoSender;


    @Autowired
    private JmsTemplate jsmTemplate;
    @Autowired
    private Destination jmsTopicTradeCommand;

    public void onStart(final IContext context) throws JFException {
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        startTradeCommandListener();
    }

    /**
     * here we start the trade command listener to listen any trade command sent by client manager, then send these
     * command to dukascopy server by Dukascopy JForex API
     */
    private void startTradeCommandListener() {
        tradeCommandReceiver = new TradeCommandReceiver(context, jsmTemplate, jmsTopicTradeCommand);
        new Thread(){
            public void run(){
                try {
                    tradeCommandReceiver.receive();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * register client by JMS to the Client Manager
     */
    private void registerClient() throws JFException {
        ClientInfoMessage cim = DukascopyUtils.generateClientInfoMessage(BrokerType.DukascopyDemo, this.context);
        clientInfoSender.sendObjectMessage(cim);
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

    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
        registerClient();
    }
}
