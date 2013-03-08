package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.*;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.TradeCommandMessage;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.Callable;

/**
 * To receive trade command from client manager, then send them to dukascopy broker server
 */

public class TradeCommandReceiver{
    Logger logger = Logger.getLogger(this.getClass());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
    {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public TradeCommandReceiver(IContext context, JmsTemplate template, Destination destination) {
        this.context = context;
        this.template = template;
        this.destination = destination;
    }

    IContext context;
    private JmsTemplate template;
    private Destination destination;
    public void receive(){
        while (true) {
          try{
            Message message = template.receive(destination);
            if (message != null)
              onMessage(message);
            else
              break;
          } catch (Exception e){
            logger.error("", e);
          }

        }
    }
    public void onMessage(Message message){

        if (message instanceof ObjectMessage) {
            try {
                Object msgObj = ((ObjectMessage) message).getObject();
                if (msgObj instanceof List) {
                    for(TradeCommandMessage tcm : (List<TradeCommandMessage>) msgObj){
                        logger.debug("TradeCommandMessage: " + tcm.toString());
                      context.executeTask(new TradeTask(tcm));
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
                throw new RuntimeException(ex);
            }
        } else {
            logger.error("Message should be a ObjectMessage in MarketDataTopic, but the message actuall is " + message);
        }
    }




  private class TradeTask implements Callable<Object> {
    private TradeCommandMessage tcm;

    public TradeTask(TradeCommandMessage tcm){
      this.tcm = tcm;
    }

    @Override
    public Object call() throws Exception {
      handleCommand(tcm);
      return null;
    }

    private void handleCommand(TradeCommandMessage tcm) throws JFException {


      String positionLabel = tcm.getPositionId();
      Instrument instrument = DukascopyUtils.toDukascopyInstrument(tcm.getInstrument());
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
      logger.info("sending TradeCommandMessage to Dukascopy server" + tcm);

    }
  }
}
