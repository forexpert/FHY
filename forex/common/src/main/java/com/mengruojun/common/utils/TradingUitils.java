package com.mengruojun.common.utils;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 2/22/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingUitils {

  public static List<Instrument> getInterestInstrumentList(){
    List<Instrument> list = new ArrayList<Instrument>();
    list.add(new Instrument(Currency.EUR, Currency.USD));
    list.add(new Instrument(Currency.USD, Currency.JPY));
    list.add(new Instrument(Currency.AUD, Currency.USD));
    list.add(new Instrument(Currency.XAG, Currency.USD));
    list.add(new Instrument(Currency.XAU, Currency.USD));
    list.add(new Instrument(Currency.GBP, Currency.USD));
    return list;
  }
}
