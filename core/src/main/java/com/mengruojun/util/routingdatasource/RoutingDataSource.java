package com.mengruojun.util.routingdatasource;

/**
 * Created by IntelliJ IDEA.
 * User: clyde
 * Date: 12/30/11
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

   @Override
   protected Object determineCurrentLookupKey() {
      return RoutingDataSourceContextHolder.getCustomerType();
   }
}

