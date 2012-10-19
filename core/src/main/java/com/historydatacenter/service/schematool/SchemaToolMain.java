package com.historydatacenter.service.schematool;

import com.mengruojun.util.routingdatasource.DataSourceType;
import com.mengruojun.util.routingdatasource.RoutingDataSourceContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: clyde
 * Date: 12/30/11
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaToolMain {
  private static final Log log = LogFactory.getLog(SchemaToolMain.class);
  ApplicationContext context;

  private SchemaToolMain() {
    context = new ClassPathXmlApplicationContext(new String[]{"classpath:/applicationContext-dao.xml", "classpath*:/applicationContext.xml", "classpath:**/applicationContext*.xml"});
  }

  public void run(String[] args) {
    SchemaToolService schemaToolService = (SchemaToolService) context.getBean("schemaToolService");
    if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
      RoutingDataSourceContextHolder.setCustomerType(DataSourceType.TEST);
      schemaToolService.initialTestDB();
      log.info("Initial Test DB successfully!");
    } else {
      RoutingDataSourceContextHolder.setCustomerType(DataSourceType.PRODUCTION);
      schemaToolService.initialProductionDB();
      log.info("Initial Test Production successfully!");
    }


  }


  public static void main(String[] args) {
    try {
      (new SchemaToolMain()).run(args);
    } catch (Throwable e) {
      log.error("error starting Main class", e);
      e.printStackTrace();
    }
  }
}
