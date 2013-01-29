package com.mengruojun.brokerclient.dukascopy;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.Serializable;

public class JMSSender {
  Logger logger = Logger.getLogger(this.getClass());

  private JmsTemplate template;
  private Destination destination;

  /**
   * 发送消息
   *
   * @param message
   */
  public void sendObjectMessage(final Object message) {
    template.send(destination, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createObjectMessage((Serializable) message);
      }
    });
    logger.info("send a message:" + message);
  }

  /**
   * 发送消息
   *
   * @param message
   */
  public void sendTextMessage(final String message) {
    template.send(destination, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(message);
      }
    });
    logger.info("send a message:" + message);
  }

  public JmsTemplate getTemplate() {
    return template;
  }

  public void setTemplate(JmsTemplate template) {
    this.template = template;
  }

  public Destination getDestination() {
    return destination;
  }

  public void setDestination(Destination destination) {
    this.destination = destination;
  }
}
