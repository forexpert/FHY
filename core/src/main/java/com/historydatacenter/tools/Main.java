package com.historydatacenter.tools;

import com.historydatacenter.service.processor.HistoryDataInitProcessor;
import com.historydatacenter.service.processor.Processor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring Standalone application starter tool
 */
public class Main {

  private String[] args;

  // the loaded context
  private ClassPathXmlApplicationContext context;
  private String[] contextFiles = new String[]{"applicationContext.xml", "applicationContext-dao.xml", "applicationContext-service.xml"};

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    if (args.length > 0 ) {
      context = new ClassPathXmlApplicationContext(contextFiles);
      Processor processor = getBean(args[0], Processor.class);
      processor.run();
    }


  }

  private <T>T getBean(String name, Class<T> clz)
  {
    T bean = (T)context.getBean(name, clz);
    if (bean == null)
      throw new RuntimeException("error locating bean '" + name + "'");
    return bean;
  }

  public static void main(String[] args) {
    new Main(args).run();
  }
}
