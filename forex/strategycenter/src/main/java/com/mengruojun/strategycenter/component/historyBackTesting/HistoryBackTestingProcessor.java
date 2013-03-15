package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    BackTestingWork btw = new BackTestingWork();
    isInTesting = true;
    try{
      historyDataKBarDao.readAll(btw);
    }catch (Exception e){
      logger.error("", e);
    }


    isInTesting = false;


  }




  @Override
  public void run() {

  }
}
