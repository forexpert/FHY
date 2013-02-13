package com.mengruojun.brokerclient;

import com.mengruojun.brokerclient.dukascopy.DukascopyTradeClient;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The module application entrance.
 */
public class Main {

  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"com/mengruojun/brokerclient/app.xml"};

  public void start(String clientId) throws InterruptedException {
    context = new ClassPathXmlApplicationContext(contextFiles);
      ((DukascopyTradeClient)context.getBean(clientId)).start();
    while (true) Thread.sleep(10000);
  }

  public static void main(String[] args) throws InterruptedException {
    Main bcs = new Main();
    String clientId = args[0];
    bcs.start(clientId);
  }
}
