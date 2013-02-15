package com.mengruojun.common.dao.initdb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: clyde
 * Date: 12/30/11
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SchemaToolService {
  private static final Log log = LogFactory.getLog(SchemaToolService.class);
  //private HibernateTemplate hibernateTemplate;
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  private org.springframework.orm.hibernate3.LocalSessionFactoryBean sessionFactory;

  /**
   * @param dataSource the jdbc data source
   */
  @Autowired
  public final void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
    if (jdbcTemplate == null || dataSource != ((JdbcAccessor) jdbcTemplate.getJdbcOperations()).getDataSource()) {
      jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }
  }

  /**
   * initial work for test db.
   * Include:
   * 1. update Schema
   * 2. insert initial test data
   */
  public void initialTestDB() {
    updateSchema();
    testDataInit();
  }

  /**
   * initial work for production db.
   * Include:
   * 1. update Schema
   * 2. insert initial data like roles, base users for production db.
   */
  public void initialProductionDB() {
    updateSchema();
    productionDataInit();

  }

  private void updateSchema() {
    try {
      sessionFactory.validateDatabaseSchema();
    } catch (HibernateSystemException e) {
      sessionFactory.updateDatabaseSchema();
    }
  }

  /**
   * init data for test purpose
   */
  private void testDataInit() {
    baseDataInit();
    // add more test data here

  }

  /**
   * init data for test purpose
   */
  private void productionDataInit() {
    baseDataInit();
    // add more production data here
  }


  /**
   * Setup basic test data
   */
  private void baseDataInit() {
    try {

      Map<String, Object> params = new HashMap<String, Object>();
      this.jdbcTemplate.update("INSERT INTO app_user (id,account_expired,account_locked,address,city,country,postal_code,province,credentials_expired,email,account_enabled,first_name,last_name,password,password_hint,phone_number,username,version,website) VALUES(-1,0,0,'','Denver','US','80210','CO',0,'matt_raible@yahoo.com',1,'Tomcat','User','12dea96fec20593566ab75692c9949596833adc9','<![CDATA[A male kitty.]]>',NULL,'user',2,'http://tomcat.apache.org')", params);
      this.jdbcTemplate.update("INSERT INTO app_user (id,account_expired,account_locked,address,city,country,postal_code,province,credentials_expired,email,account_enabled,first_name,last_name,password,password_hint,phone_number,username,version,website) VALUES(-2,0,0,'','Denver','US','80210','CO',0,'matt@raibledesigns.com',1,'Matt','Raible','d033e22ae348aeb5660fc2140aec35850c4da997','<![CDATA[Not a female kitty.]]>',NULL,'admin',2,'http://raibledesigns.com')", params);
      this.jdbcTemplate.update("INSERT INTO role (id,description,name) VALUES(1,'<![CDATA[Administrator role (can edit Users)]]>','ROLE_ADMIN') ;", params);
      this.jdbcTemplate.update("INSERT INTO role (id,description,name) VALUES(2,'<![CDATA[Default role for all Users]]>','ROLE_USER')", params);

      this.jdbcTemplate.update("INSERT INTO user_role (user_id,role_id) VALUES(-1,2);", params);
      this.jdbcTemplate.update("INSERT INTO user_role (user_id,role_id) VALUES(-2,1);", params);
    } catch (Exception e) {
      //log.error("", e);
      log.warn("The base Data had already been initialized into database");
    }
  }

}
