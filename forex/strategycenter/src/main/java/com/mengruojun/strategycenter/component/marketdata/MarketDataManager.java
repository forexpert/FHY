package com.mengruojun.strategycenter.component.marketdata;


import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
import com.mengruojun.common.domain.enumerate.KBarAttributeKey;
import com.mengruojun.common.utils.HistoryDataKBarUtils;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this MarketData is used for live data management
 * <p/>
 * kbarMap is a ConcurrentHashMap which contains all live data, include diversified TimeWindowType bars and related indicators.
 * All indicators' value are stored into HistoryDataKBar.historyDataKBarAttribute.
 * All indicators' value should be calculated only when they are really to be used.
 */
@Service
public class MarketDataManager {
  Logger logger = Logger.getLogger(this.getClass());

  @Autowired
  HistoryDataKBarDao historyDataKBarDao;

  /**
   * 1. To compute other Bars from S10 bar, we only need to keep the max map size as 6. (since we can compute S20 by S10, S30 by S20, ..., D1 by H4);
   * For Each computing the max last level data size is only 6;
   * 2. But we also need to compute indicators. So let's keep 100 here;
   */
  private static long eachKBarMapMaxSize = 500;

  private static Map<Instrument, Map<TimeWindowType, TreeMap<Long, HistoryDataKBar>>> kbarMap;

  public static List<Instrument> interestInstrumentList = TradingUtils.getInterestInstrumentList();

  {
    initKbarMap();
  }

  /**
   * init KbarMap;
   */
  private void initKbarMap() {
    kbarMap = new ConcurrentHashMap<Instrument, Map<TimeWindowType, TreeMap<Long, HistoryDataKBar>>>();
    for (Instrument instrument : interestInstrumentList) {
      Map<TimeWindowType, TreeMap<Long, HistoryDataKBar>> twtBars = new ConcurrentHashMap<TimeWindowType, TreeMap<Long, HistoryDataKBar>>();

      for (TimeWindowType twt : TimeWindowType.values()) {
        /*
          The key of Long is HistoryDataKBar's openTime
        */

        TreeMap<Long, HistoryDataKBar> openTimeBars = new TreeMap<Long, HistoryDataKBar>();
        twtBars.put(twt, openTimeBars);
      }
      kbarMap.put(instrument, twtBars);
    }
  }

  /**
   * push a marketData in this manager.  Then the manager can maintain marketData by:
   * 1) push into memory
   * 2) computing indicators and attributes and push them into memory  ---
   * This indicators computing process will be executed later when they are first used.
   * But the bars should be calculated right now. Otherwise, since the S10Map only keeps 100 rows, we can't calculated D1 bar in the future.
   */
  public void push(Map<Instrument, MarketDataMessage> s10MdmMap) {

    for (MarketDataMessage marketDataMessage : s10MdmMap.values()) {
      HistoryDataKBar kbar = marketDataMessage.convertToHistorydataKBar();
      Instrument instrument = kbar.getInstrument();
      Long openTime = kbar.getOpenTime();
      TimeWindowType timeWindowType = kbar.getTimeWindowType();

      putKBarIntoMap(kbar, instrument, timeWindowType);

      //calculate all Bars right now
      calculateAllBar(openTime + timeWindowType.getTimeInMillis(), instrument);
    }


  }

  /**
   * try to calculate D1 bars, so that we can calculate each level bars recursively.
   *
   * @param endTime    endTime
   * @param instrument instrument
   */
  private void calculateAllBar(Long endTime, Instrument instrument) {

    for (TimeWindowType twt : TimeWindowType.values()) {
      if (twt != TimeWindowType.S10) {
        getKBarByEndTime(endTime, instrument, twt);
      }
    }

    //debug  -- tobe remove
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTime(new Date(endTime));
    /*if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
      MarketDataManager.kbarMap.size();
    }*/

    if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
      MarketDataManager.kbarMap.size();
    }

    //debug  -- to be remove
  }


  private void putKBarIntoMap(HistoryDataKBar kbar, Instrument instrument, TimeWindowType timeWindowType) {
    if (kbar == null) return;
    TreeMap<Long, HistoryDataKBar> longTimeBars = kbarMap.get(instrument).get(timeWindowType);

    while (longTimeBars.size() >= eachKBarMapMaxSize) {
      longTimeBars.remove(longTimeBars.firstKey());
    }
    longTimeBars.put(kbar.getOpenTime(), kbar);
  }

  /**
   * return KBar from memory  by giving startTime, instrument and timeWidowType
   * if not find, return null. This is ONLY SEARCH.
   *
   * @param endTime        endTime
   * @param instrument     instrument
   * @param timeWindowType timeWindowType
   * @return HistoryDataKBar
   * @see MarketDataManager#getKBarByEndTime
   */
  public static HistoryDataKBar getKBarByEndTime_OnlySearch(Long endTime, Instrument instrument, TimeWindowType timeWindowType) {

    endTime = TimeWindowType.getLastAvailableEndTime(timeWindowType, endTime);

    TreeMap<Long, HistoryDataKBar> longTimeBars = kbarMap.get(instrument).get(timeWindowType);
    return longTimeBars.get(endTime - timeWindowType.getTimeInMillis());
  }


  public static Map<Instrument, HistoryDataKBar> getAllInterestInstrumentS10Bars(Long endTime) {
    Map<Instrument, HistoryDataKBar> bars = new HashMap<Instrument, HistoryDataKBar>();
    for (Instrument instrument : interestInstrumentList) {
      HistoryDataKBar bar = getKBarByEndTime_OnlySearch(endTime, instrument, TimeWindowType.S10);
      if (bar != null) bars.put(instrument, bar);
    }
    return bars;
  }

  /* <key,Value> is <KBarAttributeKey, Double>  */
  private static TreeMap<KBarAttributeKey, Double> kBarAttributeCache = new TreeMap<KBarAttributeKey, Double>();

  /**
   * it will return the property of a certain kar which ends with endTime
   *
   * @param endTime       endTime
   * @param instrument    instrument
   * @param twt           twt
   * @param attributeType attributeType
   * @return
   */
  public static Double getKBarAttributes(Long endTime, Instrument instrument,
                                         TimeWindowType twt, KBarAttributeType attributeType) {
    Long realEndTime = TimeWindowType.getLastAvailableEndTime(twt, endTime);
    KBarAttributeKey key = new KBarAttributeKey(attributeType, instrument, twt, realEndTime);
    if (kBarAttributeCache.get(key) != null) {
      return kBarAttributeCache.get(key);
    } else {
      Double value = computeAttributeValue(key);
      if (kBarAttributeCache.size() >= eachKBarMapMaxSize) {
        kBarAttributeCache.remove(kBarAttributeCache.firstKey());
      }
      kBarAttributeCache.put(key, value);
      return value;
    }
  }

  private static Double computeAttributeValue(KBarAttributeKey key) {
    return PropertyCal.cal(key, kbarMap.get(key.getInstrument()).get(key.getTwt()));
  }


  /**
   * return KBar from memory  by giving startTime, instrument and timeWidowType
   * if not find, insert one if it is able to calculated
   *
   * @param endTime        endTime
   * @param instrument     instrument
   * @param timeWindowType timeWindowType
   * @return HistoryDataKBar
   */
  private HistoryDataKBar getKBarByEndTime(Long endTime, Instrument instrument, TimeWindowType timeWindowType) {

    endTime = TimeWindowType.getLastAvailableEndTime(timeWindowType, endTime);

    TreeMap<Long, HistoryDataKBar> openTimeBars = kbarMap.get(instrument).get(timeWindowType);
    if (openTimeBars.get(endTime - timeWindowType.getTimeInMillis()) != null) {
      return openTimeBars.get(endTime - timeWindowType.getTimeInMillis());
    }
    // if not found, then find in DB
    /*HistoryDataKBar thisLevelInDB = historyDataKBarDao.find(endTime-timeWindowType.getTimeInMillis(), instrument,timeWindowType);
    if(thisLevelInDB !=null){
      putKBarIntoMap(thisLevelInDB, instrument, timeWindowType);
      return thisLevelInDB;
    }*/


    // if not found, then calculate   // the calculation has some problem for D1 level when it is sunday and friday.
    TimeWindowType nextLevelTWT = TimeWindowType.getNextLevel(timeWindowType);
    if (nextLevelTWT != null) {
      Long openTime = endTime - timeWindowType.getTimeInMillis();
      List<HistoryDataKBar> nextLevelBars = getBars(openTime, endTime - nextLevelTWT.getTimeInMillis(), instrument, nextLevelTWT);
      HistoryDataKBar thisLevel = buildKBarFromNextLevelBars(nextLevelBars, timeWindowType, openTime);
      putKBarIntoMap(thisLevel, instrument, timeWindowType);
      return thisLevel;
    } else return null;
  }

  /**
   * Get bars from memory, the openTime of which is from fromOpenTime to toOpenTime
   *
   * @param fromOpenTime fromOpenTime
   * @param toOpenTime   toOpenTime
   * @param instrument   instrument
   * @param twt          twt
   * @return List<HistoryDataKBar>  the return list only contains non-null bars
   */
  private List<HistoryDataKBar> getBars(Long fromOpenTime, Long toOpenTime, Instrument instrument, TimeWindowType twt) {
    List<HistoryDataKBar> bars = new ArrayList<HistoryDataKBar>();
    while (true) {
      if (fromOpenTime > toOpenTime) break;
      HistoryDataKBar bar = getKBarByEndTime_OnlySearch(fromOpenTime + twt.getTimeInMillis(), instrument, twt);
      fromOpenTime += twt.getTimeInMillis();
      if (bar != null) bars.add(bar);

    }
    return bars;

  }

  /**
   * build a Kbar with longer timeWindowType by 2 giving bars with shorter timeWindowsType
   *
   * @param nextLevelList  nextLevelFirst
   * @param timeWindowType timeWindowType
   * @param openTime       openTime
   * @return HistoryDataKBar
   */
  private HistoryDataKBar buildKBarFromNextLevelBars(List<HistoryDataKBar> nextLevelList, TimeWindowType timeWindowType, Long openTime) {
    if (nextLevelList == null || nextLevelList.isEmpty()) return null;

    HistoryDataKBar nextLevelFirst = nextLevelList.get(0);
    HistoryDataKBar nextLevelEnd = nextLevelList.get(nextLevelList.size() - 1);

    //Instrument instrument, TimeWindowType timeWindowType, Long openTime, Long closeTime, OHLC ohlc
    // verify input data
    if (nextLevelFirst.getInstrument().equals(nextLevelEnd.getInstrument())) {
      HistoryDataKBar kbar = new HistoryDataKBar();
      kbar.setInstrument(nextLevelFirst.getInstrument());
      kbar.setOpenTime(openTime);
      kbar.setCloseTime(openTime + timeWindowType.getTimeInMillis());
      kbar.setTimeWindowType(timeWindowType);

      OHLC ohlc = new OHLC();
      ohlc.setAskOpen(nextLevelFirst.getOhlc().getAskOpen());
      ohlc.setAskHigh(Collections.max((HistoryDataKBarUtils.getPropertyList(nextLevelList, "getAskHigh"))));
      ohlc.setAskLow(Collections.min((HistoryDataKBarUtils.getPropertyList(nextLevelList, "getAskLow"))));
      ohlc.setAskClose(nextLevelEnd.getOhlc().getAskClose());
      Double askVolume = 0d;
      for (Double d : HistoryDataKBarUtils.getPropertyList(nextLevelList, "getAskVolume")) {
        askVolume += d;
      }
      ohlc.setAskVolume(askVolume);

      ohlc.setBidOpen(nextLevelFirst.getOhlc().getBidOpen());
      Arrays.asList((HistoryDataKBarUtils.getPropertyList(nextLevelList, "getBidHigh")));
      ohlc.setBidHigh(Collections.max((HistoryDataKBarUtils.getPropertyList(nextLevelList, "getBidHigh"))));
      ohlc.setBidLow(Collections.min((HistoryDataKBarUtils.getPropertyList(nextLevelList, "getBidLow"))));
      ohlc.setBidClose(nextLevelEnd.getOhlc().getBidClose());
      Double bidVolume = 0d;
      for (Double d : HistoryDataKBarUtils.getPropertyList(nextLevelList, "getBidVolume")) {
        bidVolume += d;
      }
      ohlc.setBidVolume(bidVolume);

      kbar.setOhlc(ohlc);
      return kbar;
    } else {
      return null;
    }
  }


  //==========getter and setter =============


  public long getEachKBarMapMaxSize() {
    return eachKBarMapMaxSize;
  }

  public void setEachKBarMapMaxSize(long eachKBarMapMaxSize) {
    this.eachKBarMapMaxSize = eachKBarMapMaxSize;
  }
}
