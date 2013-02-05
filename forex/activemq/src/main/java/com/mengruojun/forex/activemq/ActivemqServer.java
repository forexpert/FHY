package com.mengruojun.forex.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.xbean.BrokerFactoryBean;
import org.apache.activemq.xbean.XBeanBrokerService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 1/25/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivemqServer {

  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"com/mengruojun/forex/activemq/applicationContext.xml"};
  BrokerService broker;

  public void start() throws Exception {
    context = new ClassPathXmlApplicationContext(contextFiles);
    broker = (BrokerService) context.getBean("broker");
    broker.start();
  }

  public void stop() throws Exception {
    broker.stop();
  }

  public static void main(String[] args) throws InterruptedException {
    ActivemqServer as = new ActivemqServer();
    try {
      as.start();
      while (true) Thread.sleep(10000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
