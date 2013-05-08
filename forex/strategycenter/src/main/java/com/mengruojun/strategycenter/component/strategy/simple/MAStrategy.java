package com.mengruojun.strategycenter.component.strategy.simple;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simpleStrategy just for test purpose
 */
public class MAStrategy extends BaseStrategy {

  public MAStrategy() {

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
