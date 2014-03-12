package com.mengruojun.pure.model.strategy.tools;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by clyde on 2/19/14.
 */
public class StartMain {
  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"com/mengruojun/pure/model/strategy/app.xml"};

  public void start() throws InterruptedException {
    context = new ClassPathXmlApplicationContext(contextFiles);
    while (true) Thread.sleep(10000);
  }




  public static void main(String[] args) throws InterruptedException {
    StartMain startMain = new StartMain();
    startMain.start();
  }
}
