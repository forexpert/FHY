package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.*;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.jms.utils.JMSSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

  private Queue<TradeCommandMessage> tcmQueue = new ArrayDeque<TradeCommandMessage>();

    public void onStart(final IContext context) throws JFException {
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
    }

    /**
     * here we start the trade command listener to listen any trade command sent by client manager, then send these
     * command to dukascopy server by Dukascopy JForex API
     */
    private void startTradeCommandListener() {
        tradeCommandReceiver = new TradeCommandReceiver(tcmQueue, jsmTemplate, jmsTopicTradeCommand);
        new Thread(){
            public void run(){
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
        ClientInfoMessage cim = DukascopyUtils.generateClientInfoMessage(BrokerType.DukascopyDemo, this.context, "sample");
        clientInfoSender.sendObjectMessage(cim);
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
    }
  private void handleCommand(Instrument instrumentExpected, TradeCommandMessage tcm) throws JFException {
      logger.info("handleCommand TradeCommandMessage :" + tcm);

      Instrument instrument = DukascopyUtils.toDukascopyInstrument(tcm.getInstrument());
    if(instrumentExpected.equals(instrument)){

        String positionLabel = tcm.getPositionId();
        Double amount = DukascopyUtils.toDukascopyAmountFromK(tcm.getAmount());
        Double openPrice = tcm.getOpenPrice();
        Double stopLossPrice = tcm.getStopLossPrice();
        Double takeProfitPrice = tcm.getTakeProfitPrice();

        switch (tcm.getTradeCommandType()){
            case openAtMarketPrice: {
                IEngine.OrderCommand longOrShort = tcm.getDirection() == Direction.Long ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL;
                context.getEngine().submitOrder(positionLabel, instrument, longOrShort, amount, openPrice, 5, stopLossPrice, takeProfitPrice);
                break;
            }
            case openAtSetPrice: {
                IEngine.OrderCommand longOrShort = tcm.getDirection() == Direction.Long ? IEngine.OrderCommand.BUYLIMIT : IEngine.OrderCommand.SELLLIMIT;
                context.getEngine().submitOrder(positionLabel, instrument, longOrShort, amount, openPrice, 5, stopLossPrice, takeProfitPrice);
                break;
            }
            case cancel:{
                IOrder order = context.getEngine().getOrder(positionLabel);
                if(order != null){
                    order.close();
                }
                break;
            }
            case change:{
                IOrder order = context.getEngine().getOrder(positionLabel);
                if(order.getState() == IOrder.State.OPENED || order.getState() == IOrder.State.CREATED){
                    order.setOpenPrice(openPrice);
                }
                if(order.getState() == IOrder.State.OPENED || order.getState() == IOrder.State.CREATED
                        || order.getState() == IOrder.State.FILLED){
                    order.setRequestedAmount(amount);
                    order.setStopLossPrice(stopLossPrice);
                    order.setTakeProfitPrice(takeProfitPrice);
                }
                break;
            }
            case close:{
                IOrder order = context.getEngine().getOrder(positionLabel);
                if(order != null){
                    order.close(amount);
                }
                break;
            }
        }
        logger.info("sending TradeCommandMessage to Dukascopy server " + tcm);
    }

  }
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        synchronized (tcmQueue){
            logger.info("Instrument is " + instrument + " Tick time is" + tick.getTime());
            if(!tcmQueue.isEmpty()) handleCommand(instrument, tcmQueue.poll());
        }
    }




    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {

    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
        registerClient();
      startTradeCommandListener();
    }
}
