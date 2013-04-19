package com.mengruojun.strategycenter.component.client;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.StrategyManager;
import com.mengruojun.strategycenter.domain.BrokerClient;
import com.mengruojun.strategycenter.springevent.ClientRegisterEvent;
import com.mengruojun.strategycenter.springevent.MarketDataReceivedEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Manager multiple broker clients
 * 1. registering clients
 * 2. sync portfolio
 * 3. send trading action to real/demo but not mock clients
 */
@Service
public class ClientManager implements ApplicationListener {
  Logger logger = Logger.getLogger(ClientManager.class);

  {
    logger.info("ClientManager started");
  }

  final Map<String, BrokerClient> brokerClientMap = new HashMap<String, BrokerClient>();

  @Autowired
  StrategyManager strategyManager;
  @Autowired
  MarketDataManager marketDataManager;

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof MarketDataReceivedEvent) {
      synchronized (brokerClientMap) {
        Map<Instrument, MarketDataMessage> mdmMap = (HashMap<Instrument, MarketDataMessage>) event.getSource();
        if (verifyMarketDataPackage(mdmMap)) {
          Long endTime = getEndTime(mdmMap);
          marketDataManager.push(mdmMap);
          for (BrokerClient bc : brokerClientMap.values()) {
            strategyManager.handle(bc, endTime);
          }
        } else {   // it shouldn't go to here
          logger.error("verifyMarketData failed. The market data is :");
          for (MarketDataMessage marketDataMessage : mdmMap.values()) {
            logger.error(marketDataMessage.toString());
          }
        }
      }
    }

    if (event instanceof ClientRegisterEvent) {
      synchronized (brokerClientMap) {
        ClientInfoMessage cim = (ClientInfoMessage) event.getSource();
        if (brokerClientMap.containsKey(cim.getClientId())) {
          //todo update broker client info  --cmeng
        } else { // register a new broker client
          BrokerClient bc = new BrokerClient(cim.getBrokerType(), cim.getClientId(), cim.getStrategyId(),
                  cim.getLeverage(), cim.getBaseCurrency(), cim.getCurrentEquity(), cim.getCurrentEquity(),
                  cim.getOpenPositionList(), cim.getPendingPositionList(), cim.getClosedPositionList());
          brokerClientMap.put(bc.getClientId(), bc);
          logger.info("Current brokerclient size is " + brokerClientMap.size());
        }
      }
    }

  }

  public static Long getEndTime(Map<Instrument, MarketDataMessage> mdmMap) {
    for (MarketDataMessage mdm : mdmMap.values()) {
      return mdm.getStartTime() + mdm.getTimeWindowType().getTimeInMillis();
    }
    return null;
  }

  /**
   * All marketDataMessage should have the same start time and timeWindowType
   *
   * @return return true if data is available;
   */
  public static boolean verifyMarketDataPackage(Map<Instrument, MarketDataMessage> mdmMap) {
    Long openTime = null;
    TimeWindowType twt = null;

    for (MarketDataMessage mdm : mdmMap.values()) {
      if (openTime == null) {
        openTime = mdm.getStartTime();
      } else {
        if (openTime != mdm.getStartTime()) {
          return false;
        }
      }

      if (twt == null) {
        twt = mdm.getTimeWindowType();
      } else {
        if (twt != mdm.getTimeWindowType()) {
          return false;
        }
      }
    }

    return true;


  }
}
