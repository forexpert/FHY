package com.mengruojun.strategycenter.strategy;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.historyBackTesting.BackTestingStrategyManager;
import com.mengruojun.strategycenter.component.historyBackTesting.HistoryBackTestingProcessor;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
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
import java.util.Date;
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
  String endTime = "2011.01.02 00:00:10 +0000";


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
    this.strategyMap.put("sample", new TradeCommandTestStrategy());
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

      //todo     In each brackets pairs, verify the open/pending/close position size, positionId
      //  And verify the account equity/balance
      if(endTime == 1L) { //expected open a pending order    A

      }
      if(endTime == 1L) { //expected open a market order     B

      }
      if(endTime == 1L) { //expected open a market order     C

      }

      if(endTime == 1L) { //expected A to be opened since the market price has reached the pending condition

      }

      if(endTime == 1L) { //expected close A by manually

      }

      if(endTime == 1L) { //expected B closed by reaching SL

      }

      if(endTime == 1L) { //expected B closed by reaching TP

      }

      if(endTime == 1L) { //open a market position, pending position, then cancel them

      }

      if(endTime == 1L) { //open a market position, pending position, then change them

      }


    }
  }
}


class TradeCommandTestStrategy extends BaseStrategy {
  /**
   * @See Dukascipy Sumbit Order's label javaDoc:
   * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
   * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
   */
  static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

  static{
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  public TradeCommandTestStrategy() {

  }

  @Override
  public List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    return factor1(bc, currentTime);
  }

  public List<TradeCommandMessage> factor1(BrokerClient bc, long currentTime) {
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);

    for (Instrument instrument : MarketDataManager.interestInstrumentList) {
      HistoryDataKBar m1 = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M1);
      HistoryDataKBar m5 = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5);
      HistoryDataKBar m10 = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M10);
      Direction direction = null;

      if(m1 ==null || m5 == null || m10 ==null)return tcmList;

      if (m1.getOhlc().getAskClose() >= m1.getOhlc().getAskOpen()
              && m5.getOhlc().getAskClose() >= m5.getOhlc().getAskOpen()
              && m10.getOhlc().getAskClose() >= m10.getOhlc().getAskOpen()
              ) {
        direction = Direction.Long;
      }

      if (m1.getOhlc().getAskClose() < m1.getOhlc().getAskOpen()
              && m5.getOhlc().getAskClose() < m5.getOhlc().getAskOpen()
              && m10.getOhlc().getAskClose() < m10.getOhlc().getAskOpen()
              ) {
        direction = Direction.Short;
      }

      if(direction != null){
        // verify if money is enough
        if (bc.getOpenPositions().size() < 10 && bc.getLeftMargin(currentPriceMap) > 0) {
          TradeCommandMessage tcm = new TradeCommandMessage(currentTime);

          tcm.setPositionId("Test"+bc.getClientId() + "_" + instrument.getCurrency1()+instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime)));
          tcm.setAmount(TradingUtils.getMinAmount(instrument));
          tcm.setInstrument(instrument);
          tcm.setTradeCommandType(TradeCommandType.openAtMarketPrice);
          tcm.setDirection(direction);

          Double intendOpenPrice = null;
          if(direction == Direction.Long){
            intendOpenPrice = currentPriceMap.get(instrument).getOhlc().getAskClose();
          } else {
            intendOpenPrice = currentPriceMap.get(instrument).getOhlc().getBidClose();
          }
          tcm.setOpenPrice(intendOpenPrice);
          tcm.setTakeProfitPrice(TradingUtils.getTPPrice(TradingUtils.getGlobalTPInPips(), intendOpenPrice, tcm.getDirection(), instrument));
          tcm.setTakeProfitPriceInPips(TradingUtils.getGlobalTPInPips());
          tcm.setStopLossPrice(TradingUtils.getSLPrice(TradingUtils.getGlobalSLInPips(), intendOpenPrice, tcm.getDirection(), instrument));
          tcm.setStopLossPriceInPips(TradingUtils.getGlobalTPInPips());
          tcmList.add(tcm);
        }
      }



    }



    return tcmList;
  }
}
