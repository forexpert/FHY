package com.mengruojun.common.utils;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.domain.enumerate.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 2/22/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingUtils {

  public static Double getGolbalAmountUnit() {
    return 1000d;
  }

  public static List<Instrument> getInterestInstrumentList() {
    List<Instrument> list = new ArrayList<Instrument>();
    list.add(new Instrument(Currency.EUR, Currency.USD));
    list.add(new Instrument(Currency.USD, Currency.JPY));
    list.add(new Instrument(Currency.AUD, Currency.USD));
    list.add(new Instrument(Currency.XAG, Currency.USD));
    list.add(new Instrument(Currency.XAU, Currency.USD));
    list.add(new Instrument(Currency.GBP, Currency.USD));
    // Note, if you want to add more interesting instrument, to calculate margin, you should also add currency1/yourAccountBaseCurrency
    return list;
  }


  /**
   * Assumption is the account base currency is USD
   *
   * @param accountLeverage accountLeverage
   * @param closePrice      closePrice
   * @param p               p
   * @param baseCurrency    Now Assumption is the account base currency is USD
   * @return position margin
   */
  public static Double calculateMargin(Double accountLeverage, Double closePrice, Position p, Currency baseCurrency) {
    if (baseCurrency != Currency.USD) {
      throw new RuntimeException("The baseCurrency is not USD!");
    }
    if (p.getInstrument().getCurrency1() != baseCurrency) {
      if (!getInterestInstrumentList().contains(new Instrument(p.getInstrument().getCurrency1(), baseCurrency))) {
        throw new RuntimeException("No related instrument data found! (" + new Instrument(p.getInstrument().getCurrency1(), baseCurrency) + ")");
      }

      return closePrice * p.getAmount() * TradingUtils.getGolbalAmountUnit() / accountLeverage;
    } else {
      return p.getAmount() * TradingUtils.getGolbalAmountUnit() / accountLeverage;
    }


  }

  public static Double getMinAmount(Instrument instrument) {
    if (instrument == new Instrument("XAU/USD")) return 0.001;
    if (instrument == new Instrument("XAG/USD")) return 0.05;
    return 1d;
  }

  /**
   * input what pips do you want to take profit
   * @param pips pips
   * @param lastBar HistoryDataKBar
   * @param direction direction
   * @return TakeProfit price
   */
  public static Double getTPPrice(Double pips, HistoryDataKBar lastBar, Direction direction) {
    if(direction == Direction.Long){
      return lastBar.getOhlc().getBidClose() + pips * lastBar.getInstrument().getPipsValue();
    } else if(direction == Direction.Short){
      return lastBar.getOhlc().getAskClose() - pips * lastBar.getInstrument().getPipsValue();
    }
    return null;
  }

  /**
   * input what pips do you want to stop lost,
   * @param pips pips
   * @param lastBar HistoryDataKBar
   * @param direction direction
   * @return StopLost price
   */
  public static Double getSLPrice(Double pips, HistoryDataKBar lastBar, Direction direction) {
    if(direction == Direction.Long){
      return lastBar.getOhlc().getBidClose() - pips * lastBar.getInstrument().getPipsValue();
    } else if(direction == Direction.Short){
      return lastBar.getOhlc().getAskClose() + pips * lastBar.getInstrument().getPipsValue();
    }
    return null;
  }

  public static Double getGlobalTPInPips() {
    return 100.0d;
  }
  public static Double getGlobalSLInPips() {
    return 100.0d;
  }
}
