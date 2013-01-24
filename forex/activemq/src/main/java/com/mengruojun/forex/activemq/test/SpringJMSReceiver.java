package com.mengruojun.forex.activemq.test;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;

public class SpringJMSReceiver implements InitializingBean {
  private JmsTemplate template;
  private Destination destination;
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
  public void receive() throws JMSException, InterruptedException {
    while (true) {
      TextMessage txtmsg = (TextMessage) template.receive(destination);
      if (null != txtmsg)
        System.out.println("收到消息内容为: " + txtmsg.getText());
      else
        break;
    }
  }

  public void afterPropertiesSet() throws Exception {
  }
}
