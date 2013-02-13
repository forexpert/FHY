package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.jms.utils.JMSSender;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class StrategyManager {
    @Autowired
    JMSSender  tradeCommandSender;

    public void handle(BrokerClient bc, MarketDataMessage source) {


    }
}
