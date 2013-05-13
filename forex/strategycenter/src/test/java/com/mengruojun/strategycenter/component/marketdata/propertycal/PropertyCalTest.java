package com.mengruojun.strategycenter.component.marketdata.propertycal;

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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * test for PropertyCal
 */
@ContextConfiguration(
        locations = {"classpath:/com/mengruojun/strategycenter/app-test.xml"})
public class PropertyCalTest extends AbstractTransactionalJUnit4SpringContextTests {
  Log log = LogFactory.getLog(PropertyCalTest.class);

  public static double MONEY_ERROR_RANGE = 0.01;
  public static double QUOTE_ERROR_RANGE = 0.0001;

  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  static {
    sdf.setTimeZone(TradingUtils.GMT);
  }

  String startTime = "2011.03.21 00:00:00 +0000";
  String endTime = "2011.04.21 00:00:10 +0000";


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
    historyBackTestingProcessor.addClient(testBrokerClient);
  }

  @Test
  public void testStrategyAndStrategyManager() throws ParseException {
    historyBackTestingProcessor.oneTurnTest(sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
  }
}

class UnitTestStrategyManager extends BackTestingStrategyManager {
  Logger logger = Logger.getLogger(this.getClass());
  SimpleDateFormat sdf = PropertyCalTest.sdf;

  public UnitTestStrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  @Override
  protected void init() {
    this.strategyMap.put("sample", new PropertyCalTestStrategy());
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
    new BrokerClient(BrokerType.MockBroker, "unitTest", "sample",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());
    /*
    *
   testBrokerClient = new BrokerClient(BrokerType.MockBroker, "unitTest", "sample",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());
    *
    * */

    if (currentTimeStr.equals("2011.03.21 01:00:00 +0000")) {  //[Test Verify] Account Balance,Equity, Position numbers
      assertEquals("unitTest", bc.getClientId());
      assertEquals("sample", bc.getStrategyName());
      assertEquals(Currency.getInstance("USD"), bc.getBaseCurrency());
      assertEquals(10000.0, bc.getEquity(currentPriceMap));
      assertEquals(10000.0, bc.getCurrentBalance());
      assertEquals(0, bc.getPendingPositions().size());
      assertEquals(0, bc.getOpenPositions().size());
      assertEquals(0, bc.getClosedPositions().size());
    }

    if (currentTimeStr.equals("2011.03.22 01:00:00 +0000")) {  //[Test Verify] EMA
      assertEquals(1.42208, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.S10, KBarAttributeType.EMA_60), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.63140, MarketDataManager.getKBarAttributes(endTime,Instrument.GBPUSD, TimeWindowType.M1, KBarAttributeType.EMA_40), PropertyCalTest.QUOTE_ERROR_RANGE);
      // for XAGUSD, the dukascopy server M5 or M1 data is not consistent with our calculation according to S10 bars. So the precise is not very good, but is acceptable.
      assertEquals(36.1420, MarketDataManager.getKBarAttributes(endTime,Instrument.XAGUSD, TimeWindowType.M5, KBarAttributeType.EMA_20), 0.01);

      assertNull(MarketDataManager.getKBarAttributes(endTime,Instrument.USDJPY, TimeWindowType.H1, KBarAttributeType.EMA_30));
      assertNull(MarketDataManager.getKBarAttributes(endTime,Instrument.AUDUSD, TimeWindowType.H4, KBarAttributeType.EMA_10));
      assertNull(MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.D1, KBarAttributeType.EMA_5));
    }
    /*if (currentTimeStr.equals("2011.03.22 01:00:00 +0000")) {  //[Test Verify] MACD
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.S10, KBarAttributeType.MACD_12_26_9_hist), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.GBPUSD, TimeWindowType.M1, KBarAttributeType.MACD_12_26_9_hist), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.XAGUSD, TimeWindowType.M5, KBarAttributeType.MACD_12_26_9_macd), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.USDJPY, TimeWindowType.H1, KBarAttributeType.MACD_12_26_9_macd), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.AUDUSD, TimeWindowType.H4, KBarAttributeType.MACD_12_26_9_signal), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.D1, KBarAttributeType.MACD_12_26_9_signal), PropertyCalTest.QUOTE_ERROR_RANGE);
    }*/


    if (currentTimeStr.equals("2011.04.15 01:00:00 +0000")) {  //[Test Verify] EMA
      assertEquals(1.44899, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.S10, KBarAttributeType.EMA_60), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.63599, MarketDataManager.getKBarAttributes(endTime,Instrument.GBPUSD, TimeWindowType.M1, KBarAttributeType.EMA_40), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(42.316, MarketDataManager.getKBarAttributes(endTime,Instrument.XAGUSD, TimeWindowType.M5, KBarAttributeType.EMA_20), 0.01);
      assertEquals(83.538, MarketDataManager.getKBarAttributes(endTime,Instrument.USDJPY, TimeWindowType.H1, KBarAttributeType.EMA_30),0.001);
      assertEquals(1.05179, MarketDataManager.getKBarAttributes(endTime,Instrument.AUDUSD, TimeWindowType.H4, KBarAttributeType.EMA_10), PropertyCalTest.QUOTE_ERROR_RANGE);
      assertEquals(1.44578, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.D1, KBarAttributeType.EMA_5), PropertyCalTest.QUOTE_ERROR_RANGE);
    }

    /*if (currentTimeStr.equals("2011.04.15 01:00:00 +0000")) {  //[Test Verify] MACD
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.S10, KBarAttributeType.MACD_12_26_9_hist));
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.GBPUSD, TimeWindowType.M1, KBarAttributeType.MACD_12_26_9_hist));
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.XAGUSD, TimeWindowType.M5, KBarAttributeType.MACD_12_26_9_macd));
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.USDJPY, TimeWindowType.H1, KBarAttributeType.MACD_12_26_9_macd));
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.AUDUSD, TimeWindowType.H4, KBarAttributeType.MACD_12_26_9_signal));
      assertEquals(1.00, MarketDataManager.getKBarAttributes(endTime,Instrument.EURUSD, TimeWindowType.D1, KBarAttributeType.MACD_12_26_9_signal));
    }*/


  }
}


class PropertyCalTestStrategy extends BaseStrategy {
  Logger logger = Logger.getLogger(this.getClass());
  static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  static {
    sdf1.setTimeZone(TradingUtils.GMT);
  }

  public PropertyCalTestStrategy() {

  }

  @Override
  public List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    try {
      return tradeRuleFundamentalTest(bc, currentTime);
    } catch (ParseException e) {
      logger.error("", e);
    }
    return null;
  }


  private List<TradeCommandMessage> tradeRuleFundamentalTest(BrokerClient bc, long currentTime) throws ParseException {
    String currentTimeStr = sdf1.format(new Date(currentTime));
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);

    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    return tcmList;

  }
}