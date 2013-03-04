package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.jms.utils.JMSSender;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        long currentTime = endTime;
        List<TradeCommandMessage> tradeCommandMessageList = strategy.analysis(bc, currentTime);
        if (tradeCommandMessageList != null) {
          tradeCommandSender.sendObjectMessage(tradeCommandMessageList);
        }
      }
  }


}
