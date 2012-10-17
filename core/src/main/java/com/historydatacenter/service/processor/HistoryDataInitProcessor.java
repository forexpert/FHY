package com.historydatacenter.service.processor;

import com.historydatacenter.dao.HistoryDataKBarDao;
import com.historydatacenter.model.HistoryDataKBar;
import com.mengruojun.util.routingdatasource.DataSourceType;
import com.mengruojun.util.routingdatasource.RoutingDataSourceContextHolder;
import org.apache.log4j.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.util.PropertiesHelper;
import org.hibernate.ejb.Ejb3Configuration;

import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/15/12
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HistoryDataInitProcessor implements Processor, ApplicationContextAware{
  ApplicationContext context;

  Logger log = Logger.getLogger(HistoryDataInitProcessor.class);
  @Autowired
  HistoryDataKBarDao dao;


  public void run() {
    SchemaToolService schemaToolService = (SchemaToolService) context.getBean("schemaToolService");
    /*if(args.length > 0 && args[0].equalsIgnoreCase("test") ){
      RoutingDataSourceContextHolder.setCustomerType(DataSourceType.TEST);
      schemaToolService.initialTestDB();
    } else{
      RoutingDataSourceContextHolder.setCustomerType(DataSourceType.PRODUCTION);
      schemaToolService.initialProductionDB();
    }*/
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }
}
