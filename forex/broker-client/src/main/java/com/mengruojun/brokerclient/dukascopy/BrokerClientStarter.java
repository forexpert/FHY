package com.mengruojun.brokerclient.dukascopy;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The module application entrance.
 */
public class BrokerClientStarter {

  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"applicationContext.xml"};

  public void start() throws InterruptedException {
    context = new ClassPathXmlApplicationContext(contextFiles);
    while (true) Thread.sleep(10000);
  }

  public static void main(String[] args) throws InterruptedException {
    BrokerClientStarter bcs = new BrokerClientStarter();
    bcs.start();
  }
}
