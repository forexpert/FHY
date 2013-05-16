package com.mengruojun.strategycenter.strategy.farmstrategy;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.historyBackTesting.BackTestingStrategyManager;
import com.mengruojun.strategycenter.component.historyBackTesting.HistoryBackTestingProcessor;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.component.strategy.simple.M5MomentumStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * test for PropertyCal
 */
@ContextConfiguration(
        locations = {"classpath:/com/mengruojun/strategycenter/app-test.xml"})
public class M5MomentumStrategTest extends AbstractTransactionalJUnit4SpringContextTests {
  Log log = LogFactory.getLog(M5MomentumStrategTest.class);


  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  static {
    sdf.setTimeZone(TradingUtils.GMT);
  }

  String startTime = "2011.03.21 00:00:00 +0000";
  String endTime = "2013.03.21 00:00:10 +0000";


  @Autowired
  HistoryBackTestingProcessor historyBackTestingProcessor;
  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  BrokerClient testBrokerClient;

  @Before
  public void setUp() {

    historyBackTestingProcessor.setBackTestingStrategyManager(new UnitTestStrategyManager());
    testBrokerClient = new BrokerClient(BrokerType.MockBroker, "unitTest", "sample",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());
    historyBackTestingProcessor.clearClient();
    historyBackTestingProcessor.addClient(testBrokerClient);
  }

  @Test
  public void testStrategy() throws ParseException {
    historyBackTestingProcessor.oneTurnTest(sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
  }
}

class UnitTestStrategyManager extends BackTestingStrategyManager {
  Logger logger = Logger.getLogger(this.getClass());
  SimpleDateFormat sdf = M5MomentumStrategTest.sdf;

  public UnitTestStrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  @Override
  protected void init() {
    this.strategyMap.put("sample", new M5MomentumStrategy(Instrument.GBPUSD));
  }

  @Override
  protected void verifyCurrentBrokerClientStatus(BrokerClient bc, Long endTime) {

    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(endTime);
    String currentTimeStr = sdf.format(new Date(endTime));
    try {
      verifyResult(bc,endTime, currentTimeStr, currentPriceMap);
    } catch (ParseException e) {
      logger.error("", e);
    }
  }


  private void verifyResult(BrokerClient bc,  Long endTime , String currentTimeStr, Map<Instrument, HistoryDataKBar> currentPriceMap) throws ParseException {

  }
}

