package com.mengruojun.common.utils;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.domain.enumerate.Direction;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 2/22/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingUtils {
  static Logger logger = Logger.getLogger(TradingUtils.class);
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
  public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
  public static final Double commissionPerM = 33.0d; //USD
  static {
    DATE_FORMAT.setTimeZone(TradingUtils.GMT);
  }
  public static Long getGlobalTradingStartTime(){
    try {
      return DATE_FORMAT.parse("2010.03.01 00:00:00 +0000").getTime();
    } catch (ParseException e) {
      logger.error("",e);
    }
    return null;
  }
  public static Double getGolbalAmountUnit() {
    return 1000d;
  }

  public static List<Instrument> getInterestInstrumentList() {
    List<Instrument> list = new ArrayList<Instrument>();
    //list.add(new Instrument(Currency.XAU, Currency.USD));  // exclude XAU/USD
    list.add(new Instrument(Currency.EUR, Currency.USD));
    //list.add(new Instrument(Currency.USD, Currency.JPY));
    //list.add(new Instrument(Currency.AUD, Currency.USD));
    //list.add(new Instrument(Currency.XAG, Currency.USD));
    //list.add(new Instrument(Currency.GBP, Currency.USD));
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
   TradingUtils.assertStat(baseCurrency==Currency.USD, "\"The baseCurrency is not USD!\"");
    if (p.getInstrument().getCurrency1() != baseCurrency) {
      if (!getInterestInstrumentList().contains(new Instrument(p.getInstrument().getCurrency1(), baseCurrency))) {
        throw new RuntimeException("No related instrument data found! (" + new Instrument(p.getInstrument().getCurrency1(), baseCurrency) + ")");
      }

      return closePrice * p.getAmount() * TradingUtils.getGolbalAmountUnit() / accountLeverage;
    } else {
      return p.getAmount() * TradingUtils.getGolbalAmountUnit() / accountLeverage;
    }
  }

  /**
   * @param position - the ForexPosition that Open PnL should be calculated for
   * @param currentPrices - a map of current PriceQuotes by Instrument.  Required for cases in which the
   *              base currency of the account is not part of the instrument.
   * @param baseCurrency - a Currency object representing the base currency of the account the
   *              trade is occurring under - required for when the base currency of the account
   *              is not involved in the position.
   * @return a double representing the unrealized gain/loss (aka Open Profit/Loss).
   */
  public static double calculateOpenPnL(Position position, Map<Instrument, HistoryDataKBar> currentPrices, Currency baseCurrency)
  {
    double openPnL = 0.0d;
    // Check to see that the instrument of the ForexPosition
    // contains the base currency of the brokerage account...  If not - locate the intermediary instrument.
    // For example:  the position instrument is AUD/NZD and the account base currency is in USD.
    // This means in order to calculate the PnL on the position, an intermediate position
    // of USD/AUD will have to be calculated.
    if(position.getInstrument() == null)
    {
      logger.warn("Unable to calculate Open PnL for position: " + position.getId() + " Instrument is null!");
      return openPnL;
    }

    if(position.getAmount() == null)
    {
      logger.warn("Unable to calculate Open PnL for position: " + position.getId() + " Size is null!");
      return openPnL;
    }

    if(position.getDirection() == null)
    {
      logger.warn("Unable to calculate Open PnL for position: " + position.getId() + " Direction is null!");
      return openPnL;
    }

    if(position.getOpenPrice() == null)
    {
      logger.warn("Unable to calculate Open PnL for position: " + position.getId() + " Open Price is null!");
      return openPnL;
    }

    HistoryDataKBar instrumentPriceBar = currentPrices.get(position.getInstrument());

    HistoryDataKBar conversionPriceBar = null;
    if(baseCurrency != position.getInstrument().getCurrency1() && baseCurrency != position.getInstrument().getCurrency2()){
      Instrument conversionInstrument = new Instrument(baseCurrency, position.getInstrument().getCurrency2());
      conversionPriceBar = currentPrices.get(conversionInstrument);
    }

    return calculateOpenPnL(position.getAmount(), position.getDirection(), position.getOpenPrice(), instrumentPriceBar,
            conversionPriceBar, baseCurrency);
  }

  private static double calculateOpenPnL(Double amount, Direction direction, Double openPrice,
                                  HistoryDataKBar instrumentPriceBar, HistoryDataKBar conversionPriceBar,Currency baseCurrency) {

    Double openPnL;

    //  Standard PnL calculation.  This is in the second currency of the instrument
    if(direction == Direction.Long)
    {
      openPnL = amount * TradingUtils.getGolbalAmountUnit() * (instrumentPriceBar.getOhlc().getBidClose() - openPrice);
    }
    else
    {
      openPnL = amount * TradingUtils.getGolbalAmountUnit() * (openPrice - instrumentPriceBar.getOhlc().getAskClose());
    }

    // simple case -- pips is already in base currency
    if (baseCurrency == instrumentPriceBar.getInstrument().getCurrency2())
      return openPnL;

    // if the base currency is the first currency of the instrument, we need to
    // divide pips by ask price to get pips in base currency
    if (instrumentPriceBar.getInstrument().getCurrency1() == baseCurrency)
    {
      openPnL = openPnL / instrumentPriceBar.getOhlc().getAskClose();
      return openPnL;
    }

    if (conversionPriceBar == null)
      throw new IllegalArgumentException("need a conversion price when computing instrument " + conversionPriceBar.getInstrument() +
              " into P/L of base currency " + baseCurrency);

    if(baseCurrency == conversionPriceBar.getInstrument().getCurrency1())
    {
      // use the 'ask'
      openPnL = openPnL / conversionPriceBar.getOhlc().getAskClose();
    } else
    {
      // use the 'bid'
      openPnL = openPnL * conversionPriceBar.getOhlc().getBidClose();
    }
    return openPnL;
  }

  public static double calculateRealizedPnL(Position position, Map<Instrument, HistoryDataKBar> currentPrices, Currency baseCurrency){


    Double amount = position.getAmount();
    Direction direction = position.getDirection();
    Double openPrice = position.getOpenPrice();
    Double closePrice = position.getClosePrice();
    HistoryDataKBar instrumentPriceBar = currentPrices.get(position.getInstrument());
    HistoryDataKBar conversionPriceBar = null;
    if(baseCurrency != position.getInstrument().getCurrency1() && baseCurrency != position.getInstrument().getCurrency2()){
      Instrument conversionInstrument = new Instrument(baseCurrency, position.getInstrument().getCurrency2());
      conversionPriceBar = currentPrices.get(conversionInstrument);
    }


    Double realizedPnL;

    //  Standard PnL calculation.  This is in the second currency of the instrument
    if(direction == Direction.Long)
    {
      realizedPnL = amount * TradingUtils.getGolbalAmountUnit() * (closePrice - openPrice);
    }
    else
    {
      realizedPnL = amount * TradingUtils.getGolbalAmountUnit() * (openPrice - closePrice);
    }

    // simple case -- pips is already in base currency
    if (baseCurrency == instrumentPriceBar.getInstrument().getCurrency2())
      return realizedPnL;

    // if the base currency is the first currency of the instrument, we need to
    // divide pips by ask price to get pips in base currency
    if (instrumentPriceBar.getInstrument().getCurrency1() == baseCurrency)
    {
      realizedPnL = realizedPnL / instrumentPriceBar.getOhlc().getAskClose();
      return realizedPnL;
    }

    if (conversionPriceBar == null)
      throw new IllegalArgumentException("need a conversion price when computing instrument " + conversionPriceBar.getInstrument() +
              " into P/L of base currency " + baseCurrency);

    if(baseCurrency == conversionPriceBar.getInstrument().getCurrency1())
    {
      // use the 'ask'
      realizedPnL = realizedPnL / conversionPriceBar.getOhlc().getAskClose();
    } else
    {
      // use the 'bid'
      realizedPnL = realizedPnL * conversionPriceBar.getOhlc().getBidClose();
    }
    return realizedPnL;

  }

  /**
   *
   * @param instrument
   * @return unit is K
   */
  public static Double getMinAmount(Instrument instrument) {
    if (instrument.equals(new Instrument("XAU/USD"))) return 0.001;
    if (instrument.equals(new Instrument("XAG/USD"))) return 0.05;
    return 1d;
  }

  /**
   * input what pips do you want to take profit
   * @param pips pips
   * @param openPrice openPrice
   * @param direction direction
   * @return TakeProfit price
   */
  public static Double getTPPrice(Double pips, Double openPrice, Direction direction, Instrument instrument) {
    if(direction == Direction.Long){
      return openPrice + pips * instrument.getPipsValue();
    } else if(direction == Direction.Short){
      return openPrice - pips * instrument.getPipsValue();
    }
    return null;
  }

  /**
   * input what pips do you want to stop lost,
   * @param pips pips
   * @param openPrice openPrice
   * @param direction direction
   * @return StopLost price
   */
  public static Double getSLPrice(Double pips, Double openPrice, Direction direction, Instrument instrument) {
    if(direction == Direction.Long){
      return openPrice - pips * instrument.getPipsValue();
    } else if(direction == Direction.Short){
      return openPrice + pips * instrument.getPipsValue();
    }
    return null;
  }

  public static Double getGlobalTPInPips() {
    return 100.0d;
  }
  public static Double getGlobalSLInPips() {
    return 100.0d;
  }

  public static void assertStat(boolean condition, String errorMsg){
    if(!condition){
      RuntimeException re = new RuntimeException(errorMsg);
      logger.error("", re);
      throw re;
    }
  }
  public static void assertStat(boolean condition){
    assertStat(condition, "assertStat error");
  }
}
