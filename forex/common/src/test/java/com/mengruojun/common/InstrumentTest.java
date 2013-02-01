package com.mengruojun.common;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Currency;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * InstrumentTest
 */
public class InstrumentTest extends EqualsAndHashCodeTestBase {


  public static final Currency CURRENCY_1 = Currency.EUR;
  public static final Currency CURRENCY_2 = Currency.USD;
  public static final Currency CURRENCY_3 = Currency.JPY;

  public static Instrument buildTestInstrument1() {
    return new Instrument(CURRENCY_1, CURRENCY_2);
  }

  public static Instrument buildTestInstrument2() {
    return new Instrument(CURRENCY_1, CURRENCY_3);
  }

  /**
   * @return a collection of equal (even if unlike) {@link Instrument} objects.
   */
  @Override
  protected Collection<Object> getEqualObjects() {
    Collection<Object> equal = new ArrayList<Object>(2);
    equal.add(new Instrument(CURRENCY_1, CURRENCY_2));
    equal.add(new Instrument(CURRENCY_1, CURRENCY_2));
    return equal;
  }

  /**
   * @return a collection of unequal (even if alike) {@link Instrument} objects.
   */
  @Override
  protected Collection<Object> getNotEqualObjects() {
    Collection<Object> notEqual = new ArrayList<Object>(2);
    notEqual.add(new Instrument(CURRENCY_1, CURRENCY_3));
    notEqual.add(new Instrument(CURRENCY_2, CURRENCY_3));
    return notEqual;
  }

  @Test
  public void testPipValue(){
    Instrument EURUSD = buildTestInstrument1();
    Instrument USDJPY = buildTestInstrument2();
    assertEquals(0.0001d, EURUSD.getPipsValue());
    assertEquals(0.01d, USDJPY.getPipsValue());
  }

  /**
   * Tests the useful (public) constructor
   */
  @Test
  public void testConstruct() {
    Instrument aas = new Instrument(CURRENCY_1, CURRENCY_2);
    assertNotNull(aas);
    assertEquals(CURRENCY_1, aas.getCurrency1());
    assertEquals(CURRENCY_2, aas.getCurrency2());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructEqualCurrencies() {
    new Instrument(CURRENCY_1, CURRENCY_1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringConstructNull() {
    new Instrument(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringConstructNoSlash() {
    new Instrument("EURUSD");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringConstructTwoSlashes() {
    new Instrument("EUR/USD/EUR");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringConstructBadCurrency1() {
    new Instrument("FOO/USD");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStringConstructBadCurrency2() {
    new Instrument("USD/FOO");
  }

  @Test
  public void testStringConstruct() {
    Instrument instrument = new Instrument("USD/EUR");
    assertNotNull(instrument);
    assertEquals(Currency.USD, instrument.getCurrency1());
    assertEquals(Currency.EUR, instrument.getCurrency2());
  }

  @Test
  public void testCurrencyPrecedenceEuro() {
    Instrument instrument = Instrument.MakeInstrument(Currency.USD, Currency.EUR);
    assertNotNull(instrument);
    assertEquals(Currency.EUR, instrument.getCurrency1());
    assertEquals(Currency.USD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.GBP, Currency.EUR);
    assertNotNull(instrument);
    assertEquals(Currency.EUR, instrument.getCurrency1());
    assertEquals(Currency.GBP, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.JPY, Currency.EUR);
    assertNotNull(instrument);
    assertEquals(Currency.EUR, instrument.getCurrency1());
    assertEquals(Currency.JPY, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.AFN, Currency.EUR);
    assertNotNull(instrument);
    assertEquals(Currency.EUR, instrument.getCurrency1());
    assertEquals(Currency.AFN, instrument.getCurrency2());
  }

  @Test
  public void testCurrencyPrecedenceAussie() {
    Instrument instrument = Instrument.MakeInstrument(Currency.USD, Currency.AUD);
    assertNotNull(instrument);
    assertEquals(Currency.AUD, instrument.getCurrency1());
    assertEquals(Currency.USD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.GBP, Currency.AUD);
    assertNotNull(instrument);
    assertEquals(Currency.GBP, instrument.getCurrency1());
    assertEquals(Currency.AUD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.JPY, Currency.AUD);
    assertNotNull(instrument);
    assertEquals(Currency.AUD, instrument.getCurrency1());
    assertEquals(Currency.JPY, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.NZD, Currency.AUD);
    assertNotNull(instrument);
    assertEquals(Currency.AUD, instrument.getCurrency1());
    assertEquals(Currency.NZD, instrument.getCurrency2());
  }

  @Test
  public void testCurrencyPrecedenceKiwi() {
    Instrument instrument = Instrument.MakeInstrument(Currency.USD, Currency.NZD);
    assertNotNull(instrument);
    assertEquals(Currency.NZD, instrument.getCurrency1());
    assertEquals(Currency.USD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.GBP, Currency.NZD);
    assertNotNull(instrument);
    assertEquals(Currency.GBP, instrument.getCurrency1());
    assertEquals(Currency.NZD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.JPY, Currency.NZD);
    assertNotNull(instrument);
    assertEquals(Currency.NZD, instrument.getCurrency1());
    assertEquals(Currency.JPY, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.NZD, Currency.CAD);
    assertNotNull(instrument);
    assertEquals(Currency.NZD, instrument.getCurrency1());
    assertEquals(Currency.CAD, instrument.getCurrency2());
  }

  @Test
  public void testCurrencyPrecedenceUSD() {
    Instrument instrument = Instrument.MakeInstrument(Currency.USD, Currency.EUR);
    assertNotNull(instrument);
    assertEquals(Currency.EUR, instrument.getCurrency1());
    assertEquals(Currency.USD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.GBP, Currency.USD);
    assertNotNull(instrument);
    assertEquals(Currency.GBP, instrument.getCurrency1());
    assertEquals(Currency.USD, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.JPY, Currency.USD);
    assertNotNull(instrument);
    assertEquals(Currency.USD, instrument.getCurrency1());
    assertEquals(Currency.JPY, instrument.getCurrency2());

    instrument = Instrument.MakeInstrument(Currency.USD, Currency.CAD);
    assertNotNull(instrument);
    assertEquals(Currency.USD, instrument.getCurrency1());
    assertEquals(Currency.CAD, instrument.getCurrency2());
  }


}
