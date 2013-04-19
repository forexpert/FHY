package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.*;
import com.dukascopy.api.Instrument;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.*;
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

public class TradeCommandReceiver {
  Logger logger = Logger.getLogger(this.getClass());
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  public TradeCommandReceiver(String clientId, IContext context, JmsTemplate template, Destination destination) {
    this.clientId = clientId;
    this.context = context;
    this.template = template;
    this.destination = destination;
  }

  IContext context;
  private JmsTemplate template;
  private Destination destination;
  private String clientId;

  public void receive() {
    while (true) {
      try {
        Message message = template.receive(destination);
        if (message != null)
          onMessage(message);
        else
          break;
      } catch (Exception e) {
        logger.error("", e);
      }

    }
  }

  public void onMessage(Message message) {
    if (message instanceof ObjectMessage) {
      try {
        Object msgObj = ((ObjectMessage) message).getObject();
        if (msgObj instanceof Map) {
          Map<String, Object> tradeCommand = (Map<String, Object>) msgObj;
          if (tradeCommand.get("clientId") != null && tradeCommand.get("clientId").equals(this.clientId)){
            if(tradeCommand.get("tradeCommandList") instanceof List){
              logger.info("tcms size is " + ((List<TradeCommandMessage>) (tradeCommand.get("tradeCommandList"))).size());
              context.executeTask(new TradeTask((List<TradeCommandMessage>) tradeCommand.get("tradeCommandList")));
            }
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
    private List<TradeCommandMessage> tcmList;

    public TradeTask(List<TradeCommandMessage> tcmList) {
      this.tcmList = tcmList;
    }

    @Override
    public Object call() throws Exception {
      for (TradeCommandMessage tcm : tcmList) {
        handleCommand(tcm);
      }
      return null;
    }

    private void handleCommand(TradeCommandMessage tcm) throws JFException {
      logger.info("start to handle TradeCommandMessage to Dukascopy server" + tcm);
      String positionLabel = tcm.getPositionId();
      Instrument instrument = DukascopyUtils.toDukascopyInstrument(tcm.getInstrument());
      Double amount = DukascopyUtils.toDukascopyAmountFromK(tcm.getAmount());
      Double openPrice = tcm.getOpenPrice();
      Double stopLossPrice = tcm.getStopLossPrice();
      Double takeProfitPrice = tcm.getTakeProfitPrice();

      switch (tcm.getTradeCommandType()) {
        case openAtMarketPrice: {
          IEngine.OrderCommand longOrShort = tcm.getDirection() == Direction.Long ? IEngine.OrderCommand.BUY : IEngine.OrderCommand.SELL;
          IOrder order = context.getEngine().submitOrder(positionLabel, instrument, longOrShort, amount, openPrice, 5, stopLossPrice, takeProfitPrice);
          break;
        }
        case openAtSetPrice: {
          IEngine.OrderCommand longOrShort = tcm.getDirection() == Direction.Long ? IEngine.OrderCommand.BUYLIMIT : IEngine.OrderCommand.SELLLIMIT;
          IOrder order = context.getEngine().submitOrder(positionLabel, instrument, longOrShort, amount, openPrice, 5, stopLossPrice, takeProfitPrice);
          break;
        }
        case cancel: {
          IOrder order = context.getEngine().getOrder(positionLabel);
          if (order != null && order.getState()== IOrder.State.OPENED) {
            order.close();
          }
          break;
        }
        case change: {
          IOrder order = context.getEngine().getOrder(positionLabel);
          if (order.getState() == IOrder.State.OPENED || order.getState() == IOrder.State.CREATED) {
            order.setOpenPrice(openPrice);
            order.setStopLossPrice(stopLossPrice);
            order.setTakeProfitPrice(takeProfitPrice);
            order.setRequestedAmount(amount);
          }
          if (order.getState() == IOrder.State.FILLED) {
            order.setStopLossPrice(stopLossPrice);
            order.setTakeProfitPrice(takeProfitPrice);
          }
          break;
        }
        case close: {
          IOrder order = context.getEngine().getOrder(positionLabel);
          if (order != null) {
            order.close(amount);
          }
          break;
        }
      }
      logger.info("sending TradeCommandMessage to Dukascopy server" + tcm);
    }


    public void wait5sec() {//just waits 5 seconds - when we place an order
      try {
        Thread.currentThread().sleep(5000); //wait 5 secs
      } catch (InterruptedException e) {
      }

    }
  }
}
