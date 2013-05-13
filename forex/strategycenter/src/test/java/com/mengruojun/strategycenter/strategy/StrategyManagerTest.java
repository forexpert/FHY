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
import com.mengruojun.strategycenter.domain.BrokerClient;
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
 * Test Some method in StrategyManager
 */
@ContextConfiguration(
        locations = {"classpath:/com/mengruojun/strategycenter/app-test.xml"})
public class StrategyManagerTest extends AbstractTransactionalJUnit4SpringContextTests {
  public static double MONEY_ERROR_RANGE = 0.01;
  public static double QUOTE_ERROR_RANGE = 0.00001;

  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  static {
    sdf.setTimeZone(TradingUtils.GMT);
  }

  String startTime = "2011.03.21 00:00:00 +0000";
  String endTime = "2011.04.01 00:00:10 +0000";


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
  SimpleDateFormat sdf = StrategyManagerTest.sdf;

  public UnitTestStrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  @Override
  protected void init() {
    this.strategyMap.put("sample", new TradeCommandTestStrategy());
  }

  @Override
  protected void verifyCurrentBrokerClientStatus(BrokerClient bc, Long endTime) {

    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(endTime);
    String currentTimeStr = sdf.format(new Date(endTime));
    try {
      verifyResult(bc, currentTimeStr, currentPriceMap);
    } catch (ParseException e) {
      logger.error("", e);
    }
  }


  private void verifyResult(BrokerClient bc, String currentTimeStr, Map<Instrument, HistoryDataKBar> currentPriceMap) throws ParseException {
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

    if (currentTimeStr.equals("2011.03.22 11:01:00 +0000")) {  //[Test Verify] Account Balance,Equity, Position numbers
      assertEquals(10000.0, bc.getEquity(currentPriceMap));  //since all positions are pending, no deals.
      assertEquals(10000.0, bc.getCurrentBalance());
      assertEquals(4, bc.getPendingPositions().size());
      assertEquals(0, bc.getOpenPositions().size());
      assertEquals(0, bc.getClosedPositions().size());
    }

    if (currentTimeStr.equals("2011.03.22 11:11:00 +0000")) {  //[Test Verify] order EURUSD_TO_CANCEL is cancelled.
      assertEquals(10000.0, bc.getEquity(currentPriceMap));  //since all positions are pending, no deals.
      assertEquals(10000.0, bc.getCurrentBalance());
      assertEquals(3, bc.getPendingPositions().size());
      assertEquals(0, bc.getOpenPositions().size());
      assertEquals(0, bc.getClosedPositions().size());


      Long pendingTime = sdf.parse("2011.03.22 11:00:00 +0000").getTime();
      String orderToBeChanged = "TradeCommandTestStrategy" + "_" + pendingTime + "USDJPY";
      boolean found = false;
      for (Position p : bc.getPendingPositions()) {
        if (p.getPositionId().equals(orderToBeChanged)) {
          found = true;
          assertEquals(Instrument.USDJPY, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.USDJPY) * 10, p.getAmount());
          assertNull(p.getStopLossPrice());
          assertNull(p.getTakeProfitPrice());
          break;
        }
      }
      assertTrue(found);

    }

    if (currentTimeStr.equals("2011.03.22 11:21:00 +0000")) {  //[Test Verify] verify order change
      assertEquals(10000.0, bc.getEquity(currentPriceMap));  //since all positions are pending, no deals.
      assertEquals(10000.0, bc.getCurrentBalance());
      assertEquals(3, bc.getPendingPositions().size());
      assertEquals(0, bc.getOpenPositions().size());
      assertEquals(0, bc.getClosedPositions().size());


      Long pendingTime = sdf.parse("2011.03.22 11:00:00 +0000").getTime();
      String orderToBeChanged = "TradeCommandTestStrategy" + "_" + pendingTime + "USDJPY";
      boolean found = false;
      for (Position p : bc.getPendingPositions()) {
        if (p.getPositionId().equals(orderToBeChanged)) {
          found = true;
          assertEquals(Instrument.USDJPY, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.USDJPY) * 15, p.getAmount());
          assertEquals(81.50 + 50 * 0.01, p.getStopLossPrice());
          assertEquals(81.50 - 50 * 0.01, p.getTakeProfitPrice());
          assertEquals(81.50, p.getOpenPrice());
        }
      }
      assertTrue(found);
    }


    if (currentTimeStr.equals("2011.03.22 12:10:00 +0000")) {  //[Test Verify] open position at market price
      /*
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_B1",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 0d, 0d));  // this will be expected to make deal immediately at openPrice 1.42420
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_B2",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 0d, 0d));  // this will be expected to be added st/tp and make deal immediately at openPrice 1.42420
      */

      //10000.0-TradingUtils.getMinAmount(Instrument.EURUSD)*TradingUtils.getGolbalAmountUnit()*10/1000000.0*TradingUtils.commissionPerM/2 =  9999.835
      assertEquals(9999.53, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      //9999.53 + currentPL = 9999.53 + (bidPrice(1.42334) - openPrice(1.42420))*amount(10*1000)=
      assertEquals(9982.33, bc.getEquity(currentPriceMap), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(3, bc.getPendingPositions().size());
      assertEquals(2, bc.getOpenPositions().size());
      assertEquals(0, bc.getClosedPositions().size());

      // verify the open position
      Long openTime = sdf.parse("2011.03.22 12:00:00 +0000").getTime();
      String openPositionId = "TradeCommandTestStrategy" + "_" + openTime + "EURUSD_B1";
      boolean found = false;
      for (Position p : bc.getOpenPositions()) {
        if (p.getPositionId().equals(openPositionId)) {
          found = true;
          assertEquals(Instrument.EURUSD, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.EURUSD) * 10, p.getAmount());
          assertNull(p.getStopLossPrice());
          assertNull(p.getTakeProfitPrice());
          assertEquals(1.42420, p.getOpenPrice());
        }
      }
      assertTrue(found);
    }

    if (currentTimeStr.equals("2011.03.22 12:11:00 +0000")) {  //[Test Verify] change SL/TP for open position
      /*if (currentTimeStr.equals("2011.03.22 12:10:00 +0000")) {  //[action] change open position
        Long pendingTime = sdf1.parse("2011.03.22 12:00:00 +0000").getTime();
        tcmList.add(this.changeOpenPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + pendingTime + "EURUSD_B1",
                50, 50));
      }*/
      // verify the open position
      Long openTime = sdf.parse("2011.03.22 12:00:00 +0000").getTime();
      String openPositionId1 = "TradeCommandTestStrategy" + "_" + openTime + "EURUSD_B1";
      String openPositionId2 = "TradeCommandTestStrategy" + "_" + openTime + "EURUSD_B2";
      boolean found = false;
      for (Position p : bc.getOpenPositions()) {
        if (p.getPositionId().equals(openPositionId2)) {
          found = true;
          assertEquals(Instrument.EURUSD, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.EURUSD) * 10, p.getAmount());
          assertNull(p.getStopLossPrice());
          assertNull(p.getTakeProfitPrice());
          assertEquals(1.42420, p.getOpenPrice());
        }
        if (p.getPositionId().equals(openPositionId1)) {
          found = true;
          assertEquals(Instrument.EURUSD, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.EURUSD) * 10, p.getAmount());
          assertEquals(1.42420 - 0.005, p.getStopLossPrice());
          assertEquals(1.42420 + 0.005, p.getTakeProfitPrice());
          assertEquals(1.42420, p.getOpenPrice());
        }
      }
      assertTrue(found);
    }


    if (currentTimeStr.equals("2011.03.22 13:10:00 +0000")) {  //[Test Verify] open position at market price
      /**
       if (currentTimeStr.equals("2011.03.22 13:00:00 +0000")) {  //[action]open market USDJPY C1, C2
       tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "USDJPY_C1",
       Instrument.USDJPY,
       TradingUtils.getMinAmount(Instrument.USDJPY) * 10, Direction.Short, 0d, 0d)); // this will be expected to make deal immediately at openPrice 81.030
       tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "USDJPY_C2",
       Instrument.USDJPY,
       TradingUtils.getMinAmount(Instrument.USDJPY) * 10, Direction.Short, 0d, 0d)); // this will be expected to make deal immediately at openPrice 81.030

       }
       */
      //9999.53-TradingUtils.getMinAmount(Instrument.USDJPY)*TradingUtils.getGolbalAmountUnit()*10/1000000.0*TradingUtils.commissionPerM/2*2 =
      //9999.53-1000*10/1000000.0*33/2.0*2=9999.2

      assertEquals(9999.2, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(3, bc.getPendingPositions().size());
      assertEquals(4, bc.getOpenPositions().size());
      assertEquals(0, bc.getClosedPositions().size());
    }


    if (currentTimeStr.equals("2011.03.22 13:50:00 +0000")) {  //[Test Verify] SL reach  B1
      /*
      the following open position will be closed due to reaching SL
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_B1",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 0d, 0d));  // this will be expected to make deal immediately at openPrice 1.42420
       */
      /**
       * B1 will be closed by reaching SL
       */
      /**
       * a pending position will be opened now:
       * tcmList.add(this.openPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD",
       Instrument.EURUSD,
       TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 1.41927, 0d, 0d));  // this will be expected to make deal from 03.22 13:00-14:00
       */

      //9999.2 + realizedPL - commission =  9999.2 + (1.4192(close price) - 1.42420) * 10*1000 - 10000*1.4192/1000000.0*33/2.0 = 9948.9658
      //9948.9658 - pendingToOpenCommission = 9948.9658 - 10000*1.41927/1000000.0*33/2.0=9948.73
      assertEquals(9948.7316, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      //bc.getCurrentBalance() + currentPL = 9948.73 + (bidPrice(1.41886) - openPrice(1.42420))*amount(10*1000)
      // + (bidPrice(1.41886) - openPrice(1.41927))*amount(10*1000)
      // + (81.035-80.987)*amount(20*1000)/(80.987)
      assertEquals(9907.7825, bc.getEquity(currentPriceMap), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(2, bc.getPendingPositions().size());
      assertEquals(4, bc.getOpenPositions().size());
      assertEquals(1, bc.getClosedPositions().size());

      // verify the open position
      Long openTime = sdf.parse("2011.03.22 12:00:00 +0000").getTime();
      String openPositionId = "TradeCommandTestStrategy" + "_" + openTime + "EURUSD_B1";
      boolean found = false;
      for (Position p : bc.getOpenPositions()) {
        if (p.getPositionId().equals(openPositionId)) {
          found = true;
        }
      }
      assertFalse(found);

      for (Position p : bc.getClosedPositions()) {
        if (p.getPositionId().equals(openPositionId)) {
          found = true;
          assertEquals(Instrument.EURUSD, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.EURUSD) * 10, p.getAmount());
          assertEquals(1.42420 - 0.005, p.getStopLossPrice());
          assertEquals(1.42420 + 0.005, p.getTakeProfitPrice());
          assertEquals(1.42420, p.getOpenPrice());
          assertEquals(1.42420 - 0.005, p.getClosePrice());
          assertEquals(Position.CloseReason.Reach_ST, p.getCloseReason());
          assertEquals(sdf.parse(currentTimeStr).getTime(), p.getCloseTime() + 1L, 60 * 1000L);
        }
      }
      assertTrue(found);
    }

    if (currentTimeStr.equals("2011.03.22 14:10:00 +0000")) {  //[Test Verify] open position at market price
      /**

       if (currentTimeStr.equals("2011.03.22 14:00:00 +0000")) {  //[action]open market XAGUSD D1,D2,D3

       tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "XAGUSD_D1",
       Instrument.XAGUSD,
       TradingUtils.getMinAmount(Instrument.XAGUSD) * 1 , Direction.Short, 0d, 0d));  // this will be expected to make deal immediately at openPrice 36.063
       tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "XAGUSD_D2",
       Instrument.XAGUSD,
       TradingUtils.getMinAmount(Instrument.XAGUSD) * 10, Direction.Short, 0d, 0d));  // this will be expected to make deal immediately at openPrice 36.063
       }
       */
      //9948.7316-TradingUtils.getMinAmount(Instrument.XAGUSD)*36.063(openprice) *TradingUtils.getGolbalAmountUnit()*10/1000000.0*TradingUtils.commissionPerM/2*2
      assertEquals(9948.4039, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);

      assertEquals(2, bc.getPendingPositions().size());
      assertEquals(6, bc.getOpenPositions().size());
      assertEquals(1, bc.getClosedPositions().size());
    }


    if (currentTimeStr.equals("2011.03.23 15:01:00 +0000")) {  //[Test Verify] full close
      /**
       * the following open position will be closed by manually
       * tcmList.add(this.openPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD",
       Instrument.EURUSD,
       TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 1.41927, 0d, 0d));  // this will be expected to make deal from 03.22 13:00-14:00
       */

      /**
       String c1 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 13:00:00 +0000").getTime() + "USDJPY_C1";
       double c1_amount = TradingUtils.getMinAmount(Instrument.USDJPY) * 10;
       tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, c1, c1_amount));

       String d1 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 14:00:00 +0000").getTime() + "XAGUSD_D1";
       double d1_amount = TradingUtils.getMinAmount(Instrument.XAGUSD);
       tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, d1, d1_amount));
       */

      // 9948.40405 - closeCommission + P&L = 9948.40405-(0.4667058+0.33+0.0608091)/2.0+(-50.1+18.0505-39.55)  =9876.375
      //commission for EURUSD: 10* 1000 * currentPrice(1.41426)*33/1000000.0/2.0=0.4667058/2.0
      //PL for EURUSD : 10 * 1000 * (1.41426-1.41927)= -50.1
      //

      //commission for USDJPY_C1: 10* 1000 *33/1000000.0/2.0=0.33/2.0
      //PL for USDJPY_C1 : 10 * 1000 * (81.030-80.884) /80.884 = 18.0505

      //commission for XAGUSD_D1: 1* 1000 * 0.05* currentPrice(36.854)*33/1000000.0/2.0=0.0608091/2.0
      //PL for XAGUSD_D1 : 1 * 1000* 0.05 * (36.063-36.854)= -39.55
      //


      assertEquals(9876.375, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(2, bc.getPendingPositions().size());
      assertEquals(3, bc.getOpenPositions().size());
      assertEquals(4, bc.getClosedPositions().size());
    }


    // one short order XAGUSD will be executed
    // currentBalance = 9876.375-commision = 9876.375-1*0.05*1000*37.03/1000000.0/2.0*33
    if (currentTimeStr.equals("2011.03.23 16:44:00 +0000")) {
      assertEquals(9876.345, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(1, bc.getPendingPositions().size());
      assertEquals(4, bc.getOpenPositions().size());
      assertEquals(4, bc.getClosedPositions().size());
    }

    //verify partial close
    /*
    if (currentTimeStr.equals("2011.03.23 22:00:00 +0000")) {  //[action] partial close B2, C2, D2
      String b2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 12:00:00 +0000").getTime() + "EURUSD_B2";
      double b2_amount = TradingUtils.getMinAmount(Instrument.EURUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, b2, b2_amount / 5));

      String c2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 13:00:00 +0000").getTime() + "USDJPY_C2";
      double c2_amount = TradingUtils.getMinAmount(Instrument.USDJPY) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, c2, c2_amount / 5));

      String d2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 14:00:00 +0000").getTime() + "XAGUSD_D2";
      double d2_amount = TradingUtils.getMinAmount(Instrument.XAGUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, d2, d2_amount / 5));

    }

    if (currentTimeStr.equals("2011.03.23 23:00:00 +0000")) {  // full close the remain  B2,C2,D2
      String b2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 12:00:00 +0000").getTime() + "EURUSD_B2";
      double b2_amount = TradingUtils.getMinAmount(Instrument.EURUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, b2, b2_amount * 4 / 5));

      String c2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 13:00:00 +0000").getTime() + "USDJPY_C2";
      double c2_amount = TradingUtils.getMinAmount(Instrument.USDJPY) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, c2, c2_amount * 4 / 5));

      String d2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 14:00:00 +0000").getTime() + "XAGUSD_D2";
      double d2_amount = TradingUtils.getMinAmount(Instrument.XAGUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, d2, d2_amount * 4 / 5));

    }
     */


    if (currentTimeStr.equals("2011.03.23 22:01:00 +0000")) {  //[Test Verify] partial close
      /*
    CurrentBalance = 9876.345 - closeCommission + closePL   = 9876.345-0.0465--29.22-0.033+1.6798-0.0616572-130.5

    //commission for EURUSD_B2 partially: 2* 1000 * currentPrice(1.40959)*33/1000000.0/2.0=0.0465
      //PL for EURUSD : 2 * 1000 * (1.40959-1.42420)= -29.22
      //

      //commission for USDJPY_C2 partially: 2* 1000 *33/1000000.0/2.0=0.033
      //PL for USDJPY_C2 : 2 * 1000 * (81.030-80.962) /80.962 = 1.6798

      //commission for XAGUSD_D2 partially: 2* 1000 * 0.05* currentPrice(37.368)*33/1000000.0/2.0=0.0616572
      //PL for XAGUSD_D2 : 2 * 1000* 0.05 * (36.063-37.389)= -132.6
      //

    Equity = CurrentBalance - unrealizedPL =  9716.06 -116.88+6.7192-530.40 -17.95= 9018.6492

    //unrealizedPL residual EURUSD_B2
             8 * 1000 * (1.40959-1.42420)  =  -116.88
    //unrealizedPL residual USDJPY_C2
             8 * 1000 * (81.030-80.962) /80.962  =  6.719201600750756
    //unrealizedPL residual XAGUSD_D2
             8 * 1000* 0.05 * (36.063-37.389) = -530.40

              //unrealizedPL residual XAGUSD
             1 * 1000* 0.05 * (37.03-37.389) = -17.95

    */
      assertEquals(9716.06, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(9057.55, bc.getEquity(MarketDataManager.getAllInterestInstrumentS10Bars(sdf.parse("2011.03.23 22:00:00 +0000").getTime())), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(1, bc.getPendingPositions().size());
      assertEquals(4, bc.getOpenPositions().size());
      assertEquals(7, bc.getClosedPositions().size());
    }

    if (currentTimeStr.equals("2011.03.23 23:01:00 +0000")) {  //[Test Verify] partial close -- close residual
      /**
       CurrentBalance = 9716.06 - closeCommission + closePL   = 9716.06-0.18612528-113.28-0.132+7.906114885731765-0.24676079999999997-530

       //commission for EURUSD_B2 partially: 8* 1000 * currentPrice(1.41004)*33/1000000.0/2.0=0.18612528
       //PL for EURUSD : 8 * 1000 * (1.41004-1.42420)= -113.28
       //

       //commission for USDJPY_C2 partially: 8* 1000 *33/1000000.0/2.0=0.132
       //PL for USDJPY_C2 : 8 * 1000 * (81.030-80.950) /80.950 = 7.906114885731765

       //commission for XAGUSD_D2 partially: 8* 1000 * 0.05* currentPrice(37.388)*33/1000000.0/2.0=0.24676079999999997
       //PL for XAGUSD_D2 : 8 * 1000* 0.05 * (36.063-37.388)= -530
       //
       */



      assertEquals(9080.12, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(1, bc.getPendingPositions().size());
      assertEquals(1, bc.getOpenPositions().size());
      assertEquals(10, bc.getClosedPositions().size());
    }

    if (currentTimeStr.equals("2011.03.24 19:30:00 +0000")) {  ////[Test Verify] TP reach  B1
      assertEquals(1, bc.getPendingPositions().size());
      assertEquals(0, bc.getOpenPositions().size());
      assertEquals(11, bc.getClosedPositions().size());

      Long openTime = sdf.parse("2011.03.22 11:00:00 +0000").getTime();
      String openPositionId = "TradeCommandTestStrategy" + "_" + openTime + "XAGUSD";
      boolean found = false;
      for (Position p : bc.getOpenPositions()) {
        if (p.getPositionId().equals(openPositionId)) {
          found = true;
        }
      }
      assertFalse(found);
      for (Position p : bc.getClosedPositions()) {
        if (p.getPositionId().equals(openPositionId)) {
          found = true;
          assertEquals(Instrument.XAGUSD, p.getInstrument());
          assertEquals(TradingUtils.getMinAmount(Instrument.XAGUSD) * 1, p.getAmount());
          assertEquals(null, p.getStopLossPrice());
          assertEquals(36.894, p.getTakeProfitPrice());
          assertEquals(37.03, p.getOpenPrice());
          assertEquals(36.894, p.getClosePrice());
          assertEquals(Position.CloseReason.Reach_TP, p.getCloseReason());
        }
      }
      assertTrue(found);
    }



    if (currentTimeStr.equals("2011.03.27 23:01:00 +0000")) {   //[Test Verify] Margin cut reach E3
      /**
       // open price should be 1.40237
       tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_margin_cut",
       Instrument.EURUSD,
       TradingUtils.getMinAmount(Instrument.EURUSD) * 300, Direction.Short, 0d, 0d));
       */
      assertEquals(9077.63, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(0, bc.getPendingPositions().size());
      assertEquals(1, bc.getOpenPositions().size());
      assertEquals(11, bc.getClosedPositions().size());
      assertEquals(bc.getOpenPositions().get(0).getAmount(), TradingUtils.getMinAmount(Instrument.EURUSD)*400);
    }

    if (currentTimeStr.equals("2011.03.31 07:18:00 +0000")) {   //[Test Verify] Margin cut reach E3 -- margin cut will happen next minute
      assertEquals(0, bc.getPendingPositions().size());
      assertEquals(1, bc.getOpenPositions().size());
      assertEquals(11, bc.getClosedPositions().size());
    }

    if (currentTimeStr.equals("2011.03.31 07:19:00 +0000")) {   //[Test Verify] Margin cut reach E3 -- margin cut will happen next minute

      assertEquals(2696.28, bc.getCurrentBalance(), StrategyManagerTest.MONEY_ERROR_RANGE);
      assertEquals(0, bc.getPendingPositions().size());
      assertEquals(0, bc.getOpenPositions().size());
      assertEquals(12, bc.getClosedPositions().size());
      assertEquals(Position.CloseReason.ReachMarginCut, bc.getClosedPositions().get(11).getCloseReason());

    }


  }
}


class TradeCommandTestStrategy extends BaseStrategy {
  Logger logger = Logger.getLogger(this.getClass());
  static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  static {
    sdf1.setTimeZone(TradingUtils.GMT);
  }

  public TradeCommandTestStrategy() {

  }

  @Override
  public List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    //return factor1(bc,currentTime);
    try {
      return tradeRuleFundamentalTest(bc, currentTime);

    } catch (ParseException e) {
      logger.error("", e);
    }
    return null;
  }

  /**
   * Only test for open orders, positions, close positions(full and partial), cancel orders, change orders, change ST/TP orders,
   * Margin cut, Reach ST/TP and so on about Trade Rule Fundamental.
   *
   * @param bc          BrokerClient
   * @param currentTime long
   * @return
   */


  private List<TradeCommandMessage> tradeRuleFundamentalTest(BrokerClient bc, long currentTime) throws ParseException {
    String currentTimeStr = sdf1.format(new Date(currentTime));
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);

    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    /**
     String startTime = "2011.01.01 00:00:00 +0000";
     String endTime = "2012.01.01 00:00:10 +0000";

     */
    /*Verify all Instrument AUD/USD, EUR/USD, GBP/USD, XAG/USD, USD/JPY*/
    if (currentTimeStr.equals("2011.03.22 11:00:00 +0000")) {  //[action]open order A1,A2,A3..

      tcmList.add(this.openPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 1.41927, 0d, 0d));  // this will be expected to make deal from 03.22 13:00-14:00
      tcmList.add(this.openPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_TO_CANCEL",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 1.41927, 0d, 0d));  // this will be expected to cancel later

      tcmList.add(this.openPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "USDJPY",
              Instrument.USDJPY,
              TradingUtils.getMinAmount(Instrument.USDJPY) * 10, Direction.Short, 81.42, 0d, 0d));  // this will be expected to be changed(change amount, ST/TP) later and make deal from 03.25 16:00-17:00


      tcmList.add(this.openPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "XAGUSD",
              Instrument.XAGUSD,
              TradingUtils.getMinAmount(Instrument.XAGUSD), Direction.Short, 37.03, 0d, 0d));  // this will be expected to make deal from 03.23 16:00-17:00

    }
    if (currentTimeStr.equals("2011.03.22 11:10:00 +0000")) {  //[action] cancel order
      Long pendingTime = sdf1.parse("2011.03.22 11:00:00 +0000").getTime();
      tcmList.add(this.cancelPendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + pendingTime + "EURUSD_TO_CANCEL"));
    }

    if (currentTimeStr.equals("2011.03.22 11:20:00 +0000")) {  //[action] change order
      Long pendingTime = sdf1.parse("2011.03.22 11:00:00 +0000").getTime();
      tcmList.add(this.changePendingPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + pendingTime + "USDJPY", 81.50,
              TradingUtils.getMinAmount(Instrument.USDJPY) * 15, 50d, 50d));
    }

    if (currentTimeStr.equals("2011.03.22 12:00:00 +0000")) {  //[action]open market EURUSD B1, B2
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_B1",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 0d, 0d));  // this will be expected to make deal immediately at openPrice 1.42420
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_B2",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 10, Direction.Long, 0d, 0d));  // this will be expected to be added st/tp and make deal immediately at openPrice 1.42420


    }

    if (currentTimeStr.equals("2011.03.22 12:10:00 +0000")) {  //[action] change open position
      Long pendingTime = sdf1.parse("2011.03.22 12:00:00 +0000").getTime();
      tcmList.add(this.changeOpenPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + pendingTime + "EURUSD_B1",
              50d, 50d));
    }

    if (currentTimeStr.equals("2011.03.22 13:00:00 +0000")) {  //[action]open market USDJPY C1, C2
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "USDJPY_C1",
              Instrument.USDJPY,
              TradingUtils.getMinAmount(Instrument.USDJPY) * 10, Direction.Short, 0d, 0d)); // this will be expected to make deal immediately at openPrice 81.030
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "USDJPY_C2",
              Instrument.USDJPY,
              TradingUtils.getMinAmount(Instrument.USDJPY) * 10, Direction.Short, 0d, 0d)); // this will be expected to make deal immediately at openPrice 81.030

    }
    if (currentTimeStr.equals("2011.03.22 14:00:00 +0000")) {  //[action]open market XAGUSD D1,D2,D3

      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "XAGUSD_D1",
              Instrument.XAGUSD,
              TradingUtils.getMinAmount(Instrument.XAGUSD) * 1, Direction.Short, 0d, 0d));  // this will be expected to make deal immediately at openPrice 36.063
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "XAGUSD_D2",
              Instrument.XAGUSD,
              TradingUtils.getMinAmount(Instrument.XAGUSD) * 10, Direction.Short, 0d, 0d));  // this will be expected to make deal immediately at openPrice 36.063
    }


    if (currentTimeStr.equals("2011.03.23 15:00:00 +0000")) {  //[action] full close   B1, C1, D1
      String b1 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 11:00:00 +0000").getTime() + "EURUSD";
      double b1_amount = TradingUtils.getMinAmount(Instrument.EURUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, b1, b1_amount));

      String c1 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 13:00:00 +0000").getTime() + "USDJPY_C1";
      double c1_amount = TradingUtils.getMinAmount(Instrument.USDJPY) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, c1, c1_amount));

      String d1 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 14:00:00 +0000").getTime() + "XAGUSD_D1";
      double d1_amount = TradingUtils.getMinAmount(Instrument.XAGUSD);
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, d1, d1_amount));
    }

    if (currentTimeStr.equals("2011.03.23 22:00:00 +0000")) {  //[action] partial close B2, C2, D2
      String b2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 12:00:00 +0000").getTime() + "EURUSD_B2";
      double b2_amount = TradingUtils.getMinAmount(Instrument.EURUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, b2, b2_amount / 5));

      String c2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 13:00:00 +0000").getTime() + "USDJPY_C2";
      double c2_amount = TradingUtils.getMinAmount(Instrument.USDJPY) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, c2, c2_amount / 5));

      String d2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 14:00:00 +0000").getTime() + "XAGUSD_D2";
      double d2_amount = TradingUtils.getMinAmount(Instrument.XAGUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, d2, d2_amount / 5));

    }

    if (currentTimeStr.equals("2011.03.23 23:00:00 +0000")) {  // full close the remain  B2,C2,D2
      String b2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 12:00:00 +0000").getTime() + "EURUSD_B2";
      double b2_amount = TradingUtils.getMinAmount(Instrument.EURUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, b2, b2_amount * 4 / 5));

      String c2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 13:00:00 +0000").getTime() + "USDJPY_C2";
      double c2_amount = TradingUtils.getMinAmount(Instrument.USDJPY) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, c2, c2_amount * 4 / 5));

      String d2 = this.getClass().getSimpleName() + "_" + sdf1.parse("2011.03.22 14:00:00 +0000").getTime() + "XAGUSD_D2";
      double d2_amount = TradingUtils.getMinAmount(Instrument.XAGUSD) * 10;
      tcmList.add(this.closePositionAtMarketPriceTCM(currentTime, d2, d2_amount * 4 / 5));
    }

    if (currentTimeStr.equals("2011.03.23 23:10:00 +0000")) {     // set SP for XAGUSD
      Long pendingTime = sdf1.parse("2011.03.22 11:00:00 +0000").getTime();
      tcmList.add(this.changeOpenPositionTCM(currentTime, this.getClass().getSimpleName() + "_" + pendingTime + "XAGUSD",
              0d, 13.60d));
    }

    if (currentTimeStr.equals("2011.03.24 19:31:00 +0000")) {     // cancel all pending position
      Long pendingTime = sdf1.parse("2011.03.22 11:00:00 +0000").getTime();
      tcmList.add(this.cancelPendingPositionTCM(currentTime, bc.getPendingPositions().get(0).getPositionId()));
    }



    if (currentTimeStr.equals("2011.03.27 23:00:00 +0000")) {     // open a large amount EURUSD position to test margin cut
      // open price should be 1.40237
      tcmList.add(this.openPositionAtMarketPriceTCM(currentTime, this.getClass().getSimpleName() + "_" + currentTime + "EURUSD_margin_cut",
              Instrument.EURUSD,
              TradingUtils.getMinAmount(Instrument.EURUSD) * 400, Direction.Short, 0d, 0d));
    }






    return tcmList;

  }

  public List<TradeCommandMessage> factor1(BrokerClient bc, long currentTime) {
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);

    for (Instrument instrument : MarketDataManager.interestInstrumentList) {
      HistoryDataKBar m1 = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M1);
      HistoryDataKBar m5 = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5);
      HistoryDataKBar m10 = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M10);
      Direction direction = null;
      if (m1 == null || m5 == null || m10 == null) return tcmList;

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
      if (direction != null) {
        // verify if money is enough
        if (bc.getOpenPositions().size() < 10 && bc.getLeftMargin(currentPriceMap) > 0) {
          String positionId = "Test" + bc.getClientId() + "_" + instrument.getCurrency1() + instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime));
          TradeCommandMessage tcm = this.openPositionAtMarketPriceTCM(currentTime, positionId, instrument,
                  TradingUtils.getMinAmount(instrument), direction, TradingUtils.getGlobalSLInPips(), TradingUtils.getGlobalTPInPips());
          tcmList.add(tcm);
        }
      }
    }
    return tcmList;
  }
}
