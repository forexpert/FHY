package com.mengruojun.strategycenter;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *  MarketDataReceiver
 */
@Service("marketDataReceiver")
public class MarketDataReceiver implements MessageListener {
  Logger logger = Logger.getLogger(this.getClass());
  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        logger.info(((TextMessage) message).getText());
      }
      catch (JMSException ex) {
        throw new RuntimeException(ex);
      }
    }
    else {
      throw new IllegalArgumentException("Message must be of type TextMessage");
    }
  }
}
