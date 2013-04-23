package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.PositionStatus;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.jms.utils.JMSSender;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.component.strategy.StrategyManager;
import com.mengruojun.strategycenter.component.strategy.simple.SampleStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Service
public class BackTestingStrategyManager extends StrategyManager {
  Logger logger = Logger.getLogger(this.getClass());

  private Map<String, BaseStrategy> strategyMap = new ConcurrentHashMap<String, BaseStrategy>();

  public BackTestingStrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  private void init() {
    strategyMap.put("sample", new SampleStrategy());
  }


  public void handle(BrokerClient bc, Long endTime) {
    BaseStrategy strategy = strategyMap.get(bc.getStrategyName());
    if (strategy != null) {
      //pending orders && SL&TP execution determine
      updateBrokerClientPendingPositions(bc, endTime);
      //analyze tradeCommand and send them to broker server
      List<TradeCommandMessage> tradeCommandMessageList = strategy.analysis(bc, endTime);
      if (tradeCommandMessageList != null) {
        //Map<String, Object> tradeCommand = new HashMap<String, Object>();
        //tradeCommand.put("clientId", bc.getClientId());
        //tradeCommand.put("tradeCommandList", tradeCommandMessageList);

        // Since we use mock trading , no need to send tradeCommand. Comment out the next line:
        // tradeCommandSender.sendObjectMessage(tradeCommand);

        //update local bc status. Normally, we think all the trade command could be executed immediately.
        // But later we open an interface to reconcile the real status from Broker Server.
        updateBrokerClientStatus(bc, tradeCommandMessageList, endTime);
      }
    }
  }

}