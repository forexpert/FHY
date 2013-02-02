package com.mengruojun.strategycenter.component.client;

import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.strategycenter.domain.BrokerClient;
import com.mengruojun.strategycenter.springevent.ClientRegisterEvent;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager multiple broker clients
 * 1. registering clients
 * 2. sync portfolio
 * 3. send trading action to clients
 */
@Service
public class ClientManager implements ApplicationListener {
    Logger logger = Logger.getLogger(ClientManager.class);

    Map<String, BrokerClient> brokerClientMap = new HashMap<String, BrokerClient>();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ClientRegisterEvent) {
            synchronized (brokerClientMap) {
                ClientInfoMessage cim = (ClientInfoMessage) event.getSource();
                if (brokerClientMap.containsKey(cim.getClientId())) {
                    //todo update broker client info
                } else { // register a new broker client
                    BrokerClient bc = new BrokerClient(cim.getBrokerType(), cim.getClientId(), cim.getStrategyId(),
                            cim.getLeverage(), cim.getBaseCurrency(), cim.getCurrentBalance(), cim.getCurrentBalance(),
                            cim.getOpenPositionList(),cim.getPendingPositionList(),cim.getClosedPositionList());
                    brokerClientMap.put(bc.getClientName(), bc);
                    logger.info("Current brokerclient size is " + brokerClientMap.size());
                }
            }
        }

    }
}
