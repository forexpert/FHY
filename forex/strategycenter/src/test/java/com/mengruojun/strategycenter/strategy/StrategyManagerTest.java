package com.mengruojun.strategycenter.strategy;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.historyBackTesting.BackTestingStrategyManager;
import com.mengruojun.strategycenter.component.historyBackTesting.HistoryBackTestingProcessor;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.component.strategy.StrategyManager;
import com.mengruojun.strategycenter.component.strategy.simple.SampleStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import junit.extensions.TestSetup;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.Assert.assertEquals;

/**
 * Test Some method in StrategyManager
 */
@ContextConfiguration(
        locations = {"classpath:/com/mengruojun/strategycenter/app-test.xml"})
public class StrategyManagerTest extends AbstractTransactionalJUnit4SpringContextTests {

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  String startTime = "2011.01.01 00:00:00 +0000";
  String endTime = "2011.03.01 00:00:10 +0000";


  @Autowired
  HistoryBackTestingProcessor historyBackTestingProcessor;
  @Autowired
  HistoryDataKBarDao historyDataKBarDao;

  @Before
  public void setUp() {

    historyBackTestingProcessor.setBackTestingStrategyManager(new UnitTestStrategyManager());
    historyBackTestingProcessor.addClient(
            new BrokerClient(BrokerType.MockBroker, "unitTest", "sample",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>()));
  }

  @Test
  public void testStrategyAndStrategyManager() throws ParseException {
    historyBackTestingProcessor.oneTurnTest(sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
  }
}

class UnitTestStrategyManager extends BackTestingStrategyManager {
  Logger logger = Logger.getLogger(this.getClass());

  private Map<String, BaseStrategy> strategyMap = new ConcurrentHashMap<String, BaseStrategy>();

  public UnitTestStrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  private void init() {
    this.strategyMap.put("sample", new SampleStrategy());
  }


  public void handle(BrokerClient bc, Long endTime) {
    BaseStrategy strategy = strategyMap.get(bc.getStrategyName());
    if (strategy != null) {
      //pending orders && SL&TP execution determine
      updateBrokerClientPendingPositions(bc, endTime);
      //analyze tradeCommand and send them to broker server
      List<TradeCommandMessage> tradeCommandMessageList = strategy.analysis(bc, endTime);
      if (tradeCommandMessageList != null) {
        Map<String, Object> tradeCommand = new HashMap<String, Object>();
        tradeCommand.put("clientId", bc.getClientId());
        tradeCommand.put("tradeCommandList", tradeCommandMessageList);

        // Since we use mock trading , no need to send tradeCommand. Comment out the next line:
        // tradeCommandSender.sendObjectMessage(tradeCommand);

        //update local bc status. Normally, we think all the trade command could be executed immediately.
        // But later we open an interface to reconcile the real status from Broker Server.
        updateBrokerClientStatus(bc, tradeCommandMessageList, endTime);
      }
      // add assert test statement.
    }
  }
}
