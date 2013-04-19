package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.strategycenter.component.client.ClientManager;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HistoryBackTestingProcessor
 * Major purpose :
 * 1. Allows a number of Mock Clients to do historyBackTesting and output their performance
 * 2. To Support Unit Test for any trading position status changes
 */

@Service
public class HistoryBackTestingProcessor implements Runnable{

  Logger logger = Logger.getLogger(this.getClass());
  Long startTime = TradingUtils.getGlobalTradingStartTime();

  @Autowired
  private MarketDataManager marketDataManager;
  @Autowired
  private BackTestingStrategyManager backTestingStrategyManager;
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
  public void addClient(BrokerClient client){
    this.clientList.add(client);
  }


  /**
   * one Turn Test.
   * In one turn test, it will load all history data(10s), pass one by one to each client, then let them do mock trade testing.
   * After that, this turn is over.
   */
  public void oneTurnTest(Long startTime, Long endTime){
    BackTestingWork btw = new BackTestingWork();
    btw.setDataHandler(new DataHandler() {
      @Override
      public void handle(Map<Instrument, MarketDataMessage> mdmMap) {
        if (ClientManager.verifyMarketDataPackage(mdmMap)) {
          Long endTime = ClientManager.getEndTime(mdmMap);
          marketDataManager.push(mdmMap);
          for (BrokerClient bc : clientList) {
            backTestingStrategyManager.handle(bc, endTime);
          }
        } else {   // it shouldn't go to here
          logger.error("verifyMarketData failed. The market data is :");
          for (MarketDataMessage marketDataMessage : mdmMap.values()) {
            logger.error(marketDataMessage.toString());
          }
        }
      }
    });

    isInTesting = true;
    try{
      historyDataKBarDao.readS10BarsByTimeRangeOrderByOpenTime(startTime, endTime, btw);
    }catch (Exception e){
      logger.error("", e);
    }


    isInTesting = false;


  }




  @Override
  public void run() {

  }


  //============= setter and getter ==========  //

  public BackTestingStrategyManager getBackTestingStrategyManager() {
    return backTestingStrategyManager;
  }

  public void setBackTestingStrategyManager(BackTestingStrategyManager backTestingStrategyManager) {
    this.backTestingStrategyManager = backTestingStrategyManager;
  }
}
