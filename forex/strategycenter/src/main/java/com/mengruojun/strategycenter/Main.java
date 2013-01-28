package com.mengruojun.strategycenter;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The module application entrance.
 */
public class Main {

  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"com/mengruojun/strategycenter/app.xml"};

  public void start() throws InterruptedException {
    context = new ClassPathXmlApplicationContext(contextFiles);
    while (true) Thread.sleep(10000);
  }

  public static void main(String[] args) throws InterruptedException {
    Main main = new Main();
    main.start();
  }
}
