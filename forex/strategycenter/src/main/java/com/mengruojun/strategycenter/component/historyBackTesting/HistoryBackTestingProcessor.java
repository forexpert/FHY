package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.log4j.Logger;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HistoryBackTestingProcessor
 * Allows a number of Mock Clients to do historyBackTesting and output their performance
 */

@Service
public class HistoryBackTestingProcessor implements Runnable{

  Logger logger = Logger.getLogger(this.getClass());
  @Autowired
  private MarketDataManager backTestingMarketDataManager;
  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  List<BrokerClient> clientList = Collections.synchronizedList(new ArrayList<BrokerClient>());

  /**
   * The processor is always running, but it may be ideal, may be running back testing. If the case is latter, isInTesting equals true;
   */
  private boolean isInTesting = false;

  /**
   * add client to client list
   * @param client BrokerClient
   */
  private void addClient(BrokerClient client){
    this.clientList.add(client);
  }


  /**
   * one Turn Test.
   * In one turn test, it will load all history data(10s), pass one by one to each client, then let them do mock trade testing.
   * After that, this turn is over.
   */
  private void oneTurnTest(){
    isInTesting = true;
    try{
      final String sql = "SELECT * FROM HistoryDataKBar ORDER BY openTime ASC";
      historyDataKBarDao.readAll(new Work() {
          //To change body of implemented methods use File | Settings | File Templates.new Work() {
          @Override
          public void execute(Connection conn) throws SQLException {
              PreparedStatement ps = null;
              ps = conn.prepareStatement(sql);
              ResultSet rs = ps.executeQuery();
              while (rs.next()) {
                  logger.info(rs.getLong(0));
              }
              rs.close();
              ps.close();
          }
      });

    }catch (Exception e){
      logger.error("", e);
    }


    isInTesting = false;


  }




  @Override
  public void run() {

  }
}
