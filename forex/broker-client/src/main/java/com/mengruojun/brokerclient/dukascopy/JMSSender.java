package com.mengruojun.brokerclient.dukascopy;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class JMSSender {

  private JmsTemplate template;
  private Destination destination;

  /**
   * 发送消息
   *
   * @param message
   */
  public void createMessage(final String message) {
    template.send(destination, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(message);
      }
    });
    System.out.println(message);
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
