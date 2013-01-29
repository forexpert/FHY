package com.mengruojun.strategycenter.component.jmsreceiver;

import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * MarketDataReceiver
 */
@Service("marketDataReceiver")
public class MarketDataReceiver implements MessageListener {
  Logger logger = Logger.getLogger(this.getClass());

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }


  @Override
  public void onMessage(Message message) {
    if (message instanceof ObjectMessage) {
      try {
        Object msgObj = ((ObjectMessage) message).getObject();
        if(msgObj instanceof MarketDataMessage){
          MarketDataMessage mdm = (MarketDataMessage) msgObj;
          logger.info("Start time is " + sdf.format(new Date(mdm.getStartTime())));
          logger.info("Time Window is " + mdm.getTimeWindowType());
          logger.info("ask OHLC is " + mdm.getAskOpen() + " " + mdm.getAskHigh() + " "
                  + mdm.getAskLow() + " " + mdm.getAskClose());
          logger.info("");
          logger.info("");
          logger.info("");
          //todo cmeng 1. save into DB and memory
          //todo cmeng 2. computing indicators
          //todo cmeng 3. notify client manager

        }
      } catch (JMSException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      logger.error("Message should be a ObjectMessage in MarketDataTopic, but the message actuall is " + message);
    }
  }
}
