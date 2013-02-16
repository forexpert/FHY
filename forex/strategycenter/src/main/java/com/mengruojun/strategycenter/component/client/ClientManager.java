package com.mengruojun.strategycenter.component.client;

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

import java.util.HashMap;
import java.util.Map;

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
    Map<String, BrokerClient> brokerClientMap = new HashMap<String, BrokerClient>();

    @Autowired
    StrategyManager strategyManager;
    @Autowired
    MarketDataManager marketDataManager;
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof MarketDataReceivedEvent){
            synchronized (brokerClientMap) {
                marketDataManager.push((MarketDataMessage) event.getSource());
                for(BrokerClient bc : brokerClientMap.values()){
                    strategyManager.handle(bc, (MarketDataMessage)event.getSource());
                }
            }
        }

        if (event instanceof ClientRegisterEvent) {
            synchronized (brokerClientMap) {
                ClientInfoMessage cim = (ClientInfoMessage) event.getSource();
                if (brokerClientMap.containsKey(cim.getClientId())) {
                    //todo update broker client info
                } else { // register a new broker client
                    BrokerClient bc = new BrokerClient(cim.getBrokerType(), cim.getClientId(), cim.getStrategyId(),
                            cim.getLeverage(), cim.getBaseCurrency(), cim.getCurrentBalance(), cim.getCurrentBalance(),
                            cim.getOpenPositionList(),cim.getPendingPositionList(),cim.getClosedPositionList());
                    brokerClientMap.put(bc.getClientId(), bc);
                    logger.info("Current brokerclient size is " + brokerClientMap.size());
                }
            }
        }

    }
}
