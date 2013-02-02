package com.mengruojun.forex.activemq;

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
  private String[] contextFiles = new String[]{"applicationContext.xml"};

  public void start() throws Exception {
    context = new ClassPathXmlApplicationContext(contextFiles);
    context.getBean("broker");
    while (true) Thread.sleep(10000);
  }

  public static void main(String[] args) throws InterruptedException {
    ActivemqServer as = new ActivemqServer();
    try {
      as.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
