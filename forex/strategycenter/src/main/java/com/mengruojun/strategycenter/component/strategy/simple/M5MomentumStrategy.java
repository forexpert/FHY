package com.mengruojun.strategycenter.component.strategy.simple;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * reference to 5分钟动量交易系统 作　者： 关天豪　等著
 */
public class M5MomentumStrategy extends BaseStrategy {

  public M5MomentumStrategy(Instrument targetInstrument) {
    this.targetInstrument = targetInstrument;
  }

  private Instrument targetInstrument;

  public void setTargetInstrument(Instrument targetInstrument) {
    this.targetInstrument = targetInstrument;
  }

  Direction ema20ConditionPerfer = null;
  int countForNUM_PASS_MACD_BAR_CROSS = 0;
  private final static int NUM_PASS_MACD_BAR_CROSS = 5;

  /**
   * Which should be implemented by subclasses.
   *
   * @param bc
   * @param currentTime
   * @return
   */
  @Override
  protected List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);

    Instrument instrument = targetInstrument;

    if (TimeWindowType.M5.canEndWithTime(currentTime)) {
      tcmList.addAll(closePositionAnalysis(bc, currentTime));
      tcmList.addAll(openPositionAnalysis(bc, currentTime));
    }


    return tcmList;
  }

  private List<TradeCommandMessage>  closePositionAnalysis(BrokerClient bc, long currentTime) {
    Instrument instrument = targetInstrument;
    Map<Instrument, HistoryDataKBar> currentPriceMap = MarketDataManager.getAllInterestInstrumentS10Bars(currentTime);
    List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
    for(Position p : bc.getOpenPositions()){
      if(p.getPositionId().startsWith("HasNoTP_")){
        boolean close = false;

        Double currentAskClose = MarketDataManager.getKBarByEndTime_OnlySearch(currentTime, instrument, TimeWindowType.M5).getOhlc().getAskClose();
        Double currentEMA20Price = MarketDataManager.getKBarAttributes(currentTime, instrument, TimeWindowType.M5, KBarAttributeType.EMA_20);
        if(p.getDirection() == Direction.Long){
          if(currentAskClose<currentEMA20Price && currentAskClose-p.getOpenPrice()>(p.getOpenPrice()-p.getStopLossPrice())){
            close = true;
          }
        } else {
          if(currentAskClose>currentEMA20Price && p.getOpenPrice()-currentAskClose>(p.getStopLossPrice()-p.getOpenPrice())){
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


  private List<TradeCommandMessage> openPositionAnalysis(BrokerClient bc,long currentTime) {
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


    boolean ema20 = determineEma20(currentAskClose, currentEMA20Price, lastAskClose, lastEMA20Price, currentTime);
    if (determineEma20(currentAskClose, currentEMA20Price, lastAskClose, lastEMA20Price, currentTime) &&
            determineMACD(currentMACDHistgram, lastMACDHistgram, currentTime)) {
      //start to open a position
      Direction perferDirection = ema20ConditionPerfer == Direction.Long ? Direction.Long : Direction.Short;

      //set SL , usually as 20 pips. But we can enhance it  later
      double slPips = 20;
      //

      // verify if money is enough
      if (bc.getOpenPositions().size() < 10 && bc.getLeftMargin(currentPriceMap) > 0) {
        String positionId = "M5MomentumStrategy" + bc.getClientId() + "_" + instrument.getCurrency1() + instrument.getCurrency2() + "_" + sdf.format(new Date(currentTime));
        TradeCommandMessage tcm1 = this.openPositionAtMarketPriceTCM(currentTime, "HasTP_" + positionId, instrument,
                TradingUtils.getMinAmount(instrument) * 5, perferDirection, slPips, slPips);
        TradeCommandMessage tcm2 = this.openPositionAtMarketPriceTCM(currentTime, "HasNoTP_" + positionId, instrument,
                TradingUtils.getMinAmount(instrument) * 5, perferDirection, slPips, 0d);
        tcmList.add(tcm1);
        tcmList.add(tcm2);
      }


      //clear
      ema20ConditionPerfer = null;
      countForNUM_PASS_MACD_BAR_CROSS = 0;

    }

    return tcmList;

  }

  private boolean determineMACD(Double currentMACDHistgram, Double lastMACDHistgram, long currentTime) {
    if(currentMACDHistgram == null || lastMACDHistgram == null){
      return false;
    }


    int lastMACDHistogramVS0 = (lastMACDHistgram >= 0) ? 1 : -1;
    int currentMACDHistogramVS0 = (currentMACDHistgram >= 0) ? 1 : -1;

    if ((lastMACDHistogramVS0 * currentMACDHistogramVS0 == -1)) {
      Direction macdConditionPerfer = currentMACDHistogramVS0 == 1 ? Direction.Long : Direction.Short;
      if (ema20ConditionPerfer == macdConditionPerfer) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }

  }

  private boolean determineEma20(Double currentAskClose, Double currentEMA20Price, Double lastAskClose, Double lastEMA20Price, long currentTime) {
    if(currentEMA20Price==null||lastEMA20Price==null){
      return false;
    }


    if (ema20ConditionPerfer != null) {
      if (countForNUM_PASS_MACD_BAR_CROSS < NUM_PASS_MACD_BAR_CROSS) {
        countForNUM_PASS_MACD_BAR_CROSS++;
        return true;
      } else {
        ema20ConditionPerfer = null;
        countForNUM_PASS_MACD_BAR_CROSS = 0;
      }
    }


    int lastAskCloseVSEMA20 = (lastAskClose >= lastEMA20Price) ? 1 : -1;
    int currentAskCloseVSEMA20 = (currentAskClose >= currentEMA20Price) ? 1 : -1;
    if ((lastAskCloseVSEMA20 * currentAskCloseVSEMA20 == -1)) {
      ema20ConditionPerfer = currentAskCloseVSEMA20 == 1 ? Direction.Long : Direction.Short;
      return true;
    } else {
      return false;
    }
  }
}
