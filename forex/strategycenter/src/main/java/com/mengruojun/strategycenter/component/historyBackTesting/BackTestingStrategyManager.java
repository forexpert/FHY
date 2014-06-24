package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.component.strategy.StrategyManager;
import com.mengruojun.strategycenter.component.strategy.simple.SampleStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class BackTestingStrategyManager extends StrategyManager {
  Logger logger = Logger.getLogger(this.getClass());

  public BackTestingStrategyManager() {
    init();
  }

  /**
   * init strategyMap by registering strategies, which are all instances of Base Strategy
   */
  @Override
  protected void init() {
  }

  public void addStrategy(String strategyName, BaseStrategy baseStrategy){
    strategyMap.put(strategyName, baseStrategy);
  }


  public void handle(BrokerClient bc, Long endTime) {
    BaseStrategy strategy = strategyMap.get(bc.getStrategyName());
    if (strategy != null) {
      //pending orders && SL&TP execution determine
      updateBrokerClientPendingPositions(bc, endTime);
      recordPerformanceInfoForBrokerClient(bc, endTime);

      verifyCurrentBrokerClientStatus(bc, endTime);

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

  /**
   *  do nothing. but child class(unit test) can implement this method to do any verification.
   */
  protected void verifyCurrentBrokerClientStatus(BrokerClient bc, Long endTime) {
    //do nothing
  }
  DecimalFormat df1 = new DecimalFormat("####.00");
  private void recordPerformanceInfoForBrokerClient(BrokerClient bc, Long endTime) {
    Calendar cal = Calendar.getInstance(TradingUtils.GMT);
    cal.setTimeInMillis(endTime);
    if(cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0 && cal.get(Calendar.SECOND) == 0 ){ //one day's start
      Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(endTime);
      bc.recordEquity(endTime, currentPriceMap);
      logger.info("Equity of " + bc.getClientId() + " is " +
              df1.format(bc.getEquity(currentPriceMap)) + " [Time " + TradingUtils.DATE_FORMAT.format(new Date(endTime))  + "]");
    }
  }

}