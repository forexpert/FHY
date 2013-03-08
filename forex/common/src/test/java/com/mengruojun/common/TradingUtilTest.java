package com.mengruojun.common;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.utils.TradingUtils;
import org.junit.Test;

import static junit.framework.Assert.*;


public class TradingUtilTest {

  @Test
  public void calculateMarginTest(){
    Position p = new Position();
    p.setAmount(1d);
    p.setInstrument(new Instrument("EUR/USD"));
    Double margin = TradingUtils.calculateMargin(100d, 1.3210, p, Currency.USD);
    assertEquals(13.21, margin, 0.001d);


    p.setInstrument(new Instrument("USD/JPY"));
    margin = TradingUtils.calculateMargin(100d, 95.35, p, Currency.USD);
    assertEquals(10, margin, 0.001d);


  }
}
