package com.mengruojun.common.dao.db.routingdatasource;

/**
 * Created by IntelliJ IDEA.
 * User: clyde
 * Date: 12/30/11
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

   @Override
   protected Object determineCurrentLookupKey() {
      return RoutingDataSourceContextHolder.getCustomerType();
   }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

