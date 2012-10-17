package com.mengruojun.util.routingdatasource;

import org.springframework.util.Assert;

/**
 * Created by IntelliJ IDEA.
 * User: clyde
 * Date: 12/30/11
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutingDataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> contextHolder =
                new ThreadLocal<DataSourceType>();

       public static void setCustomerType(DataSourceType dataSourceType) {
          Assert.notNull(dataSourceType, "dataSourceType cannot be null");
          contextHolder.set(dataSourceType);
       }

       public static DataSourceType getCustomerType() {
          return (DataSourceType) contextHolder.get();
       }

       public static void clearCustomerType() {
          contextHolder.remove();
       }

}
