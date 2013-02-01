package com.mengruojun.common.utils;

import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

/**
 We have already had a class named 'Instrument', but we can take this one as a reference
 so that the one pips value can be easily determined.
 */
public enum DukascopyInstrumentEnum {

  /**
   * Defines all currency pairs traded by Dukascopy
   */
  AUDJPY(java.util.Currency.getInstance("AUD"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  AUDCAD(java.util.Currency.getInstance("AUD"), java.util.Currency.getInstance("CAD"), 0.0001, 4),
  AUDCHF(java.util.Currency.getInstance("AUD"), java.util.Currency.getInstance("CHF"), 0.0001, 4),
  AUDNZD(java.util.Currency.getInstance("AUD"), java.util.Currency.getInstance("NZD"), 0.0001, 4),
  AUDSGD(java.util.Currency.getInstance("AUD"), java.util.Currency.getInstance("SGD"), 0.0001, 4),
  AUDUSD(java.util.Currency.getInstance("AUD"), java.util.Currency.getInstance("USD"), 0.0001, 4),
  CADCHF(java.util.Currency.getInstance("CAD"), java.util.Currency.getInstance("CHF"), 0.0001, 4),
  CADHKD(java.util.Currency.getInstance("CAD"), java.util.Currency.getInstance("HKD"), 0.0001, 4),
  CADJPY(java.util.Currency.getInstance("CAD"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  CHFJPY(java.util.Currency.getInstance("CHF"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  CHFPLN(java.util.Currency.getInstance("CHF"), java.util.Currency.getInstance("PLN"), 0.0001, 4),
  CHFSGD(java.util.Currency.getInstance("CHF"), java.util.Currency.getInstance("SGD"), 0.0001, 4),
  EURAUD(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("AUD"), 0.0001, 4),
  EURBRL(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("BRL"), 0.0001, 4),
  EURCAD(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("CAD"), 0.0001, 4),
  EURCHF(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("CHF"), 0.0001, 4),
  EURDKK(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("DKK"), 0.0001, 4),
  EURGBP(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("GBP"), 0.0001, 4),
  EURHKD(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("HKD"), 0.0001, 4),
  EURHUF(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("HUF"), 0.01, 2),
  EURJPY(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  EURMXN(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("MXN"), 0.0001, 4),
  EURNOK(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("NOK"), 0.0001, 4),
  EURNZD(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("NZD"), 0.0001, 4),
  EURPLN(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("PLN"), 0.0001, 4),
  EURRUB(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("RUB"), 0.0001, 4),
  EURSEK(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("SEK"), 0.0001, 4),
  EURSGD(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("SGD"), 0.0001, 4),
  EURTRY(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("TRY"), 0.0001, 4),
  EURUSD(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("USD"), 0.0001, 4),
  EURZAR(java.util.Currency.getInstance("EUR"), java.util.Currency.getInstance("ZAR"), 0.0001, 4),
  GBPAUD(java.util.Currency.getInstance("GBP"), java.util.Currency.getInstance("AUD"), 0.0001, 4),
  GBPCAD(java.util.Currency.getInstance("GBP"), java.util.Currency.getInstance("CAD"), 0.0001, 4),
  GBPCHF(java.util.Currency.getInstance("GBP"), java.util.Currency.getInstance("CHF"), 0.0001, 4),
  GBPJPY(java.util.Currency.getInstance("GBP"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  GBPNZD(java.util.Currency.getInstance("GBP"), java.util.Currency.getInstance("NZD"), 0.0001, 4),
  GBPUSD(java.util.Currency.getInstance("GBP"), java.util.Currency.getInstance("USD"), 0.0001, 4),
  HKDJPY(java.util.Currency.getInstance("HKD"), java.util.Currency.getInstance("JPY"), 0.0001, 4),
  HUFJPY(java.util.Currency.getInstance("HUF"), java.util.Currency.getInstance("JPY"), 0.0001, 4),
  MXNJPY(java.util.Currency.getInstance("MXN"), java.util.Currency.getInstance("JPY"), 0.0001, 4),
  NZDCAD(java.util.Currency.getInstance("NZD"), java.util.Currency.getInstance("CAD"), 0.0001, 4),
  NZDCHF(java.util.Currency.getInstance("NZD"), java.util.Currency.getInstance("CHF"), 0.0001, 4),
  NZDJPY(java.util.Currency.getInstance("NZD"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  NZDSGD(java.util.Currency.getInstance("NZD"), java.util.Currency.getInstance("SGD"), 0.0001, 4),
  NZDUSD(java.util.Currency.getInstance("NZD"), java.util.Currency.getInstance("USD"), 0.0001, 4),
  SGDJPY(java.util.Currency.getInstance("SGD"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  USDBRL(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("BRL"), 0.0001, 4),
  USDCAD(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("CAD"), 0.0001, 4),
  USDCHF(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("CHF"), 0.0001, 4),
  USDCZK(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("CZK"), 0.01, 2),
  USDDKK(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("DKK"), 0.0001, 4),
  USDHKD(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("HKD"), 0.0001, 4),
  USDHUF(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("HUF"), 0.01, 2),
  USDJPY(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("JPY"), 0.01, 2),
  USDMXN(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("MXN"), 0.0001, 4),
  USDNOK(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("NOK"), 0.0001, 4),
  USDPLN(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("PLN"), 0.0001, 4),
  USDRON(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("RON"), 0.0001, 4),
  USDRUB(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("RUB"), 0.0001, 4),
  USDSEK(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("SEK"), 0.0001, 4),
  USDSGD(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("SGD"), 0.0001, 4),
  USDTRY(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("TRY"), 0.0001, 4),
  USDZAR(java.util.Currency.getInstance("USD"), java.util.Currency.getInstance("ZAR"), 0.0001, 4),
  XAGUSD(java.util.Currency.getInstance("XAG"), java.util.Currency.getInstance("USD"), 0.01, 2),
  XAUUSD(java.util.Currency.getInstance("XAU"), java.util.Currency.getInstance("USD"), 0.01, 2),
  ZARJPY(java.util.Currency.getInstance("ZAR"), java.util.Currency.getInstance("JPY"), 0.0001, 4);

  private java.util.Currency primaryCurrency;
  private java.util.Currency secondaryCurrency;
  private double pipValue;
  private int pipScale;

  private DukascopyInstrumentEnum(java.util.Currency primaryCurrency, java.util.Currency secondaryCurrency, double pipValue, int pipScale) {
    this.primaryCurrency = primaryCurrency;
    this.secondaryCurrency = secondaryCurrency;
    this.pipValue = pipValue;
    this.pipScale = pipScale;
  }

  @Override
  public String toString() {
    return name().substring(0, 3) + getPairsSeparator() + name().substring(3, 6);
  }

  /**
   * Returns currency separator
   *
   * @return currency separator
   */
  public static String getPairsSeparator() {
    return "/";
  }

  /**
   * Returns corresponding instrument for string in "CUR1/CUR2" format
   *
   * @param instrumentAsString string in "CUR1/CUR2" format
   * @return corresponding instrument or null if no instrument was found for specified string
   */
  public static DukascopyInstrumentEnum fromString(String instrumentAsString) {
    for (DukascopyInstrumentEnum instrument : values()) {
      if (instrumentAsString.equals(instrument.getPrimaryCurrency().getCurrencyCode() + "/" + instrument.getSecondaryCurrency().getCurrencyCode())) {
        return instrument;
      }
    }
    return null;
  }

  /**
   * Returns corresponding inverted instrument for string in "CUR2/CUR1" format, e.g., string USD/EUR returns instrument EUR/USD, but string EUR/USD returns null
   *
   * @param instrumentAsString string in "CUR2/CUR1" format
   * @return corresponding instrument or null if no instrument was found for specified string
   */
  public static DukascopyInstrumentEnum fromInvertedString(String instrumentAsString) {
    for (DukascopyInstrumentEnum instrument : values()) {
      if (instrumentAsString.equals(instrument.getSecondaryCurrency().getCurrencyCode() + "/" + instrument.getPrimaryCurrency().getCurrencyCode())) {
        return instrument;
      }
    }
    return null;
  }

  /**
   * Returns true if instrument is inverted (such as USD/EUR or JPY/USD)
   *
   * @param instrumentStr instrument string representation
   * @return true if inverted, false if not inverted or not instrument
   */
  public static boolean isInverted(String instrumentStr) {
    return fromString(instrumentStr) == null && instrumentStr.length() == 7 && fromString(instrumentStr.substring(4, 7) + "/" + instrumentStr.substring(0, 3)) != null;
  }

  /**
   * Returns set of strings, which are instruments in "CUR1/CUR2" format
   *
   * @param instruments collection of instruments
   * @return set of strings in "CUR1/CUR2" format
   */
  public static Set<String> toStringSet(Collection<DukascopyInstrumentEnum> instruments) {
    Set<String> set = new HashSet<String>();
    if (instruments != null && !instruments.isEmpty()) {
      for (DukascopyInstrumentEnum instrument : instruments) {
        set.add(instrument.toString());
      }
    }
    return set;
  }

  public static Set<DukascopyInstrumentEnum> fromStringSet(Set<String> instrumentsAsString) {
    Set<DukascopyInstrumentEnum> instruments = new HashSet<DukascopyInstrumentEnum>();
    if (instrumentsAsString != null && !instrumentsAsString.isEmpty()) {
      for (String instrumentAsString : instrumentsAsString) {
        try {
          DukascopyInstrumentEnum instrument = fromString(instrumentAsString);
          if (instrument == null) {
            instrument = fromInvertedString(instrumentAsString);
          }
          if (instrument == null) {
            instrument = valueOf(instrumentAsString);
          }
          if (instrument != null) {
            instruments.add(instrument);
          }
        } catch (Throwable t) {
          // unsupported instrument arrived
        }
      }
    }
    return instruments;
  }

  /**
   * Returns true if specified instrument is one of the traded instruments
   *
   * @param instrumentString instrument to check
   * @return true if corresponding instrument was found, false otherwise
   */
  public static boolean contains(String instrumentString) {
    for (DukascopyInstrumentEnum instr : DukascopyInstrumentEnum.values()) {
      if (instr.toString().equals(instrumentString)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns primary currency of this currency pair
   *
   * @return primary currency
   */
  public java.util.Currency getPrimaryCurrency() {
    return primaryCurrency;
  }

  /**
   * Returns secondary currency of this currency pair
   *
   * @return secondary currency
   */
  public java.util.Currency getSecondaryCurrency() {
    return secondaryCurrency;
  }

  /**
   * Returns value of one pip for this currency pair
   *
   * @return pip
   */
  public double getPipValue() {
    return pipValue;
  }

  /**
   * Returns decimal place count of one pip for the currency pair
   *
   * @return pip
   */
  public int getPipScale() {
    return pipScale;
  }

  /**
   * Returns true if the string value represents the instrument
   *
   * @param symbol
   * @return true if the string value represents the instrument
   */
  public boolean equals(String symbol) {
    if (symbol == null) {
      return false;
    } else {
      return toString().equals(symbol);
    }
  }
}
