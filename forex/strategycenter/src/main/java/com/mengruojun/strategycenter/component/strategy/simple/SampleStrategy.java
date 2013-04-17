package com.mengruojun.strategycenter.component.strategy.simple;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simpleStrategy just for test purpose
 */
public class SampleStrategy extends BaseStrategy {
  /**
   * @See Dukascipy Sumbit Order's label javaDoc:
   * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
   * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
   */
  static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

  static{
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  public SampleStrategy() {

  }

  @Override
  public List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    return factor1(bc, currentTime);
  }


  /**
   * just send Long Market Price order, without any strategy
   */
  public List<TradeCommandMessage> factor0(BrokerClient bc, long currentTime) {
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    TradeCommandMessage tcm = new TradeCommandMessage(currentTime);
    tcm.setPositionId("test" + sdf.format(new Date(currentTime)));
    tcm.setAmount(1d);
    tcm.setInstrument(new Instrument(Currency.EUR, Currency.USD));
    tcm.setTradeCommandType(TradeCommandType.openAtMarketPrice);
    tcm.setDirection(Direction.Long);

    tcm.setOpenPrice(0d);
    tcm.setTakeProfitPrice(0d);
    tcm.setStopLossPrice(0d);
    tcmList.add(tcm);
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
        if (bc.getOpenPositions().size() < 1 && bc.getLeftMargin(currentPriceMap) > 0) {
          TradeCommandMessage tcm = new TradeCommandMessage(currentTime);

          tcm.setPositionId("Test"+bc.getClientId() + "_" + instrument.getCurrency1()+instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime)));
          tcm.setAmount(TradingUtils.getMinAmount(instrument));
          tcm.setInstrument(instrument);
          tcm.setTradeCommandType(TradeCommandType.openAtMarketPrice);
          tcm.setDirection(direction);

          tcm.setOpenPrice(0d);
          tcm.setTakeProfitPrice(TradingUtils.getTPPrice(TradingUtils.getGlobalTPInPips(), currentPriceMap.get(instrument), tcm.getDirection()));
          tcm.setStopLossPrice(TradingUtils.getSLPrice(TradingUtils.getGlobalSLInPips(), currentPriceMap.get(instrument), tcm.getDirection()));
          tcmList.add(tcm);
        }
      }



    }



    return tcmList;
  }
}
