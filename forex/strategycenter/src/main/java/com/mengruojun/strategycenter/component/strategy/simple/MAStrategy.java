package com.mengruojun.strategycenter.component.strategy.simple;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
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

  public MAStrategy(Instrument targetInstrument, TimeWindowType watchingTimeWindowType, KBarAttributeType shortEMATerm, KBarAttributeType longEMATerm) {
    this.targetInstrument = targetInstrument;
    this.watchingTimeWindowType = watchingTimeWindowType;
    this.shortEMATerm = shortEMATerm;
    this.longEMATerm = longEMATerm;
  }

  public String toString() {
    return this.getClass().getSimpleName() + "_" + targetInstrument + "_" + watchingTimeWindowType + "_" + shortEMATerm + "_" + longEMATerm;
  }

  protected Instrument targetInstrument;
  TimeWindowType watchingTimeWindowType;
  KBarAttributeType shortEMATerm;
  KBarAttributeType longEMATerm;

  public void setTargetInstrument(Instrument targetInstrument) {
    this.targetInstrument = targetInstrument;
  }

  public void setWatchingTimeWindowType(TimeWindowType watchingTimeWindowType) {
    this.watchingTimeWindowType = watchingTimeWindowType;
  }

  public void setShortEMATerm(KBarAttributeType shortEMATerm) {
    this.shortEMATerm = shortEMATerm;
  }

  public void setLongEMATerm(KBarAttributeType longEMATerm) {
    this.longEMATerm = longEMATerm;
  }

  @Override
  public List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    return factor1(bc, currentTime);
  }

  public List<TradeCommandMessage> factor1(BrokerClient bc, long currentTime) {
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);

    Instrument instrument = targetInstrument;

    if (watchingTimeWindowType.canEndWithTime(currentTime)) {
      tcmList.addAll(closePositionAnalysis(bc, currentTime));
      tcmList.addAll(openPositionAnalysis(bc, currentTime));
    }


    return tcmList;
  }

  protected List<TradeCommandMessage> closePositionAnalysis(BrokerClient bc, long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();


    Double currentEMAShortAskClose = MarketDataManager.getKBarAttributes(currentTime, instrument, watchingTimeWindowType, shortEMATerm);
    Double currentEMALongAskPrice = MarketDataManager.getKBarAttributes(currentTime, instrument, watchingTimeWindowType, longEMATerm);

    for (Position p : bc.getOpenPositions()) {
      boolean close = false;

      if (p.getDirection() == Direction.Long) {
        if (currentEMAShortAskClose < currentEMALongAskPrice) {
          close = true;
        }
      } else {
        if (currentEMAShortAskClose > currentEMALongAskPrice) {
          close = true;
        }
      }

      if (close) {// construct close command
        TradeCommandMessage tcm = this.closePositionAtMarketPriceTCM(currentTime, p.getPositionId(), p.getAmount());
        tcmList.add(tcm);
      }


    }
    return tcmList;
  }


  protected List<TradeCommandMessage> openPositionAnalysis(BrokerClient bc, long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();

    Double currentEMAShortAskClose = MarketDataManager.getKBarAttributes(currentTime, instrument, watchingTimeWindowType, shortEMATerm);
    Double currentEMALongAskPrice = MarketDataManager.getKBarAttributes(currentTime, instrument, watchingTimeWindowType, longEMATerm);

    long lastTime = currentTime - this.watchingTimeWindowType.getTimeInMillis();
    Double lastEMAShortAskClose = MarketDataManager.getKBarAttributes(lastTime, instrument, watchingTimeWindowType, shortEMATerm);
    Double lastEMALongAskPrice = MarketDataManager.getKBarAttributes(lastTime, instrument, watchingTimeWindowType, longEMATerm);

    Direction perferDirection = null;

    if (currentEMAShortAskClose!=null && currentEMALongAskPrice!=null &&
            lastEMAShortAskClose!=null && lastEMALongAskPrice!=null &&
            currentEMAShortAskClose < currentEMALongAskPrice && lastEMAShortAskClose > lastEMALongAskPrice) {   //sell short
      perferDirection = Direction.Short;
    }

    if (currentEMAShortAskClose!=null && currentEMALongAskPrice!=null &&
            lastEMAShortAskClose!=null && lastEMALongAskPrice!=null &&
            currentEMAShortAskClose > currentEMALongAskPrice && lastEMAShortAskClose < lastEMALongAskPrice) {   //buy long
      perferDirection = Direction.Long;
    }

    if (perferDirection != null) {
      // verify if money is enough
      if (bc.getOpenPositions().size() < 5 && bc.getLeftMargin(currentPriceMap) > 0) {
        String positionId = this.getClass().getSimpleName() + "_" + bc.getClientId() + "_" + instrument.getCurrency1() + instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime));
        /*TradeCommandMessage tcm1 = this.openPositionAtMarketPriceTCM(currentTime, "HasTP_" + positionId, instrument,
                TradingUtils.getMinAmount(instrument) * 5, perferDirection, slPips, slPips);*/
        TradeCommandMessage tcm2 = this.openPositionAtMarketPriceTCM(currentTime, "HasNo_SL_TP_" + positionId, instrument,
                TradingUtils.getMinAmount(instrument) * 5, perferDirection, 0d, 0d);
        //tcmList.add(tcm1);
        tcmList.add(tcm2);
      }
    }

    return tcmList;

  }
}
