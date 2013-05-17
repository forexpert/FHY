package com.mengruojun.strategycenter.strategy.farmstrategy;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.Direction;
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
import org.junit.Ignore;
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
  BrokerClient testBrokerClient2;

  @Before
  public void setUp() {

    historyBackTestingProcessor.setBackTestingStrategyManager(new UnitTestStrategyManager());
    testBrokerClient = new BrokerClient(BrokerType.MockBroker, "M5MomentumStrategy", "sample",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());

    testBrokerClient2 = new BrokerClient(BrokerType.MockBroker, "AntiM5MomentumStrategy", "anti-sample",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());
    historyBackTestingProcessor.clearClient();
    historyBackTestingProcessor.addClient(testBrokerClient);
    //historyBackTestingProcessor.addClient(testBrokerClient2);
  }
  @Ignore
  @Test
  public void testStrategy() throws ParseException {
    historyBackTestingProcessor.oneTurnTest(sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
  }
}

class AntiM5MomentumStrategy extends M5MomentumStrategy{


  public AntiM5MomentumStrategy(Instrument targetInstrument) {
    super(targetInstrument);
  }

  /**
   * Which should be implemented by subclasses.
   *
   * @param bc
   * @param currentTime
   * @return
   */
  @Override
  protected List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    return super.OnAnalysis(bc,currentTime);
  }


  protected List<TradeCommandMessage>  closePositionAnalysis(BrokerClient bc, long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    for(Position p : bc.getOpenPositions()){
      if(p.getPositionId().startsWith("HasNoTP_")){
        boolean close = false;

        Double currentAskClose = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5).getOhlc().getAskClose();
        Double currentEMA20Price = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
        if(p.getDirection() == Direction.Short){
          if(currentAskClose<currentEMA20Price && currentAskClose-p.getOpenPrice()>(p.getOpenPrice()-p.getTakeProfitPrice())){
            close = true;
          }
        } else {
          if(currentAskClose>currentEMA20Price && p.getOpenPrice()-currentAskClose>(p.getTakeProfitPrice()-p.getOpenPrice())){
            close = true;
          }
        }

        if(close){// construct close command
          TradeCommandMessage tcm = this.closePositionAtMarketPriceTCM(currentTime,p.getPositionId(),p.getAmount());
          tcmList.add(tcm);
        }
      }

    }
    return tcmList;
  }


  protected List<TradeCommandMessage> openPositionAnalysis(BrokerClient bc,long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    //determine if we should open position
    Double currentAskClose = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5).getOhlc().getAskClose();
    Double currentEMA20Price = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
    Double currentMACDHistgram = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.MACD_12_26_9_hist);
    long lastBarTime = currentTime - TimeWindowType.M5.getTimeInMillis();
    HistoryDataKBar lastBar = MarketDataManager.getKBarByEndTime_OnlySearch(lastBarTime, instrument, TimeWindowType.M5);
    if(lastBar == null) {
      return tcmList;
    }
    Double lastAskClose = lastBar.getOhlc().getAskClose();
    Double lastEMA20Price = MarketDataManager.getKBarAttributes(lastBarTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
    Double lastMACDHistgram = MarketDataManager.getKBarAttributes(lastBarTime, instrument, TimeWindowType.M5, KBarAttributeType.MACD_12_26_9_hist);


    if (determineEma20(currentAskClose, currentEMA20Price, lastAskClose, lastEMA20Price, currentTime) &&
            determineMACD(currentMACDHistgram, lastMACDHistgram, currentTime)) {
      //start to open a position
      Direction perferDirection = ema20ConditionPerfer == Direction.Long ? Direction.Short : Direction.Long;

      //set SL , usually as 20 pips. But we can enhance it  later
      double slPips = 20;
      //

      // verify if money is enough
      if (bc.getOpenPositions().size() < 10 && bc.getLeftMargin(currentPriceMap) > 0) {
        String positionId = this.getClass().getSimpleName() + bc.getClientId() + "_" + instrument.getCurrency1() + instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime));
        TradeCommandMessage tcm1 = this.openPositionAtMarketPriceTCM(currentTime, "HasTP_" + positionId, instrument,
                TradingUtils.getMinAmount(instrument) * 5, perferDirection, slPips,slPips );
        TradeCommandMessage tcm2 = this.openPositionAtMarketPriceTCM(currentTime, "HasNoTP_" + positionId, instrument,
                TradingUtils.getMinAmount(instrument) * 5, perferDirection, 0d, slPips);
        tcmList.add(tcm1);
        tcmList.add(tcm2);
      }


      //clear
      ema20ConditionPerfer = null;
      countForNUM_PASS_MACD_BAR_CROSS = 0;

    }

    return tcmList;

  }

  private void doReverse(TradeCommandMessage tcm) {
    if(tcm.getDirection()!=null){
      tcm.setDirection(tcm.getDirection()== Direction.Long?Direction.Short:Direction.Long);
    }

    Double temp = tcm.getStopLossInPips();
    tcm.setStopLossInPips(tcm.getTakeProfitInPips());
    tcm.setTakeProfitInPips(temp);



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
    this.strategyMap.put("anti-sample", new AntiM5MomentumStrategy(Instrument.GBPUSD));
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

