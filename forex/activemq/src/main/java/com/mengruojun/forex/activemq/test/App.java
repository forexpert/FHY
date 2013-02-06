package com.mengruojun.forex.activemq.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App {

  // the loaded context
  private ClassPathXmlApplicationContext context;

  public App(){
    String[] contextFiles = new String[]{"com/mengruojun/forex/activemq/applicationContext.xml"};
    context = new ClassPathXmlApplicationContext(contextFiles);
  }

  private <T>T getBean(String name, Class<T> clz)
  {
    T bean = (T)context.getBean(name, clz);
    if (bean == null)
      throw new RuntimeException("error locating bean '" + name + "'");
    return bean;
  }

  public static void main(String[] args) throws InterruptedException {
    final App app = new App();
    new Thread(){
      public void run(){

        SpringJMSProductor sender = app.getBean("jmsproductor", SpringJMSProductor.class);
        sender.createMessage("I love you, yingying");

      }
    }.start();

    new Thread(){
      public void run(){
        SpringJMSReceiver receiver = app.getBean("jmsreceiver", SpringJMSReceiver.class);
        try {
          receiver.receive();
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    }.start();


    while (true) {
      Thread.sleep(10000);

    }
  }
}
