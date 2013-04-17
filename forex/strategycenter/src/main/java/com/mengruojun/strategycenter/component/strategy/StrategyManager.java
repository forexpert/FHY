package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.jms.utils.JMSSender;
import com.mengruojun.strategycenter.component.strategy.simple.SampleStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Service
public class StrategyManager {
  @Autowired
  JMSSender tradeCommandSender;

  private Map<String, BaseStrategy> strategyMap = new ConcurrentHashMap<String, BaseStrategy>();

  public StrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  public void init() {
    strategyMap.put("sample", new SampleStrategy());
  }


  public void handle(BrokerClient bc, Long endTime) {
      BaseStrategy strategy = strategyMap.get(bc.getStrategyName());
      if (strategy != null) {
        List<TradeCommandMessage> tradeCommandMessageList = strategy.analysis(bc, endTime);
        if (tradeCommandMessageList != null) {
          Map<String, Object> tradeCommand = new HashMap<String, Object>();
          tradeCommand.put("clientId", bc.getClientId());
          tradeCommand.put("tradeCommandList", tradeCommandMessageList);
          tradeCommandSender.sendObjectMessage(tradeCommand);
        }
      }
  }


}
