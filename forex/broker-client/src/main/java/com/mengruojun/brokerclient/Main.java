package com.mengruojun.brokerclient;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The module application entrance.
 */
public class Main {

  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"com/mengruojun/brokerclient/app.xml"};

  public void start() throws InterruptedException {
    context = new ClassPathXmlApplicationContext(contextFiles);
    while (true) Thread.sleep(10000);
  }

  public static void main(String[] args) throws InterruptedException {
    Main bcs = new Main();
    bcs.start();
  }
}
