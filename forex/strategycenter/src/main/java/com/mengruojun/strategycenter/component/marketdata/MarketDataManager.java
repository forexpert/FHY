package com.mengruojun.strategycenter.component.marketdata;


import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.utils.HistoryDataKBarUtils;
import com.mengruojun.common.utils.TradingUitils;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
   * 2. But we also need to compute indicators. So let's keep 500 here;
   */
  long eachKBarMapMaxSize = 500;

  private Map<Instrument, Map<TimeWindowType, Queue<HistoryDataKBar>>> kbarMap  ;

  {
    initKbarMap();
  }

  /**
   * init KbarMap;
   */
  private void initKbarMap(){
    kbarMap = new ConcurrentHashMap<Instrument, Map<TimeWindowType, Queue<HistoryDataKBar>>>();
    List<Instrument> instruments = TradingUitils.getInterestInstrumentList();
    for(Instrument instrument : instruments){
      Map<TimeWindowType, Queue<HistoryDataKBar>> twtBars = new ConcurrentHashMap<TimeWindowType, Queue<HistoryDataKBar>>();

      for(TimeWindowType twt : TimeWindowType.values()){
        Queue<HistoryDataKBar> openTimeBars = new ConcurrentLinkedQueue<HistoryDataKBar>();
        twtBars.put(twt, openTimeBars);
      }
      kbarMap.put(instrument, twtBars);
    }
  }

  /**
   * push a marketData in this manager.  Then the manager can maintain marketData by:
   * 1) push into memory
   * 2) computing indicators and attributes and push them into memory  ---  This computing process will be executed later when they are first used.
   */
  public void push(Map<Instrument, MarketDataMessage> s10MdmMap) {


    //todo next cmeng
    for (MarketDataMessage marketDataMessage : s10MdmMap.values()) {
      HistoryDataKBar kbar = marketDataMessage.convertToHistorydataKBar();
      Instrument instrument = kbar.getInstrument();
      Long openTime = kbar.getOpenTime();
      TimeWindowType timeWindowType = kbar.getTimeWindowType();

      putKBarIntoMap(kbar, instrument, openTime, timeWindowType);
    }


  }


  private void putKBarIntoMap(HistoryDataKBar kbar, Instrument instrument, Long openTime, TimeWindowType timeWindowType) {
    Queue<HistoryDataKBar> longTimeBars = kbarMap.get(instrument).get(timeWindowType);

    while(longTimeBars.size() >= eachKBarMapMaxSize){
      longTimeBars.poll();
    }
    longTimeBars.offer(kbar);
  }

  /**
   * return KBar from memory  by giving startTime, instrument and timeWidowType
   * @param endTime
   * @param instrument
   * @param timeWindowType
   * @return
   */
  public HistoryDataKBar getKBarByEndTime(Long endTime, Instrument instrument, TimeWindowType timeWindowType) {
    Queue<HistoryDataKBar> longTimeBars = kbarMap.get(instrument).get(timeWindowType);
    for(HistoryDataKBar kBar : longTimeBars){
      if(kBar.getOpenTime() == endTime - timeWindowType.getTimeInMillis()){
        return kBar;
      }
    }
    // if not found, then calculate
    TimeWindowType nextLevelTWT = TimeWindowType.getNextLevel(timeWindowType);
    if(nextLevelTWT !=null){
      Long openTime = endTime - timeWindowType.getTimeInMillis();
      List<HistoryDataKBar> nextLevelBars = getBars(openTime, endTime - nextLevelTWT.getTimeInMillis(), instrument, nextLevelTWT);
      HistoryDataKBar thisLevel = buildKBarFromNextLevelBars(nextLevelBars, timeWindowType);
      return thisLevel;
    } else return null;
  }

  /**
   * Get bars from memory, the openTime of which is from fromOpenTime to toOpenTime
   * @param fromOpenTime fromOpenTime
   * @param toOpenTime  toOpenTime
   * @param instrument  instrument
   * @param twt twt
   * @return  List<HistoryDataKBar>
   */
  private List<HistoryDataKBar> getBars(Long fromOpenTime, Long toOpenTime, Instrument instrument, TimeWindowType twt){
    List<HistoryDataKBar> bars = new ArrayList<HistoryDataKBar>();
    while(true){
      if(fromOpenTime > toOpenTime) break;
      HistoryDataKBar bar = getKBarByEndTime(fromOpenTime+twt.getTimeInMillis(), instrument, twt);
      fromOpenTime +=twt.getTimeInMillis();
      bars.add(bar);

    }
    return bars;

  }

  /**
   * build a Kbar with longer timeWindowType by 2 giving bars with shorter timeWindowsType
   * @param nextLevelList nextLevelFirst
   * @param timeWindowType  timeWindowType
   * @return  HistoryDataKBar
   */
  private HistoryDataKBar buildKBarFromNextLevelBars(List<HistoryDataKBar> nextLevelList, TimeWindowType timeWindowType) {
    HistoryDataKBar nextLevelFirst = nextLevelList.get(0);
    HistoryDataKBar nextLevelEnd = nextLevelList.get(nextLevelList.size()-1);

    //Instrument instrument, TimeWindowType timeWindowType, Long openTime, Long closeTime, OHLC ohlc
    // verify input data
    if( nextLevelFirst.getInstrument().equals(nextLevelEnd.getInstrument()) &&
            (nextLevelEnd.getCloseTime() - nextLevelFirst.getOpenTime() == timeWindowType.getTimeInMillis()) ){
      HistoryDataKBar kbar = new HistoryDataKBar();
      kbar.setInstrument(nextLevelFirst.getInstrument());
      kbar.setOpenTime(nextLevelFirst.getOpenTime());
      kbar.setCloseTime(nextLevelEnd.getCloseTime());
      kbar.setTimeWindowType(timeWindowType);

      OHLC ohlc = new OHLC();
      ohlc.setAskOpen(nextLevelFirst.getOhlc().getAskOpen());
      ohlc.setAskHigh(Collections.max((HistoryDataKBarUtils.getPropertyList(nextLevelList,"getAskHigh"))));
      ohlc.setAskLow(Collections.max((HistoryDataKBarUtils.getPropertyList(nextLevelList,"getAskLow"))));
      ohlc.setAskClose(nextLevelEnd.getOhlc().getAskClose());
      Double askVolume = 0d;
      for(Double d : HistoryDataKBarUtils.getPropertyList(nextLevelList,"getAskVolume")){
        askVolume += d;
      }
      ohlc.setAskVolume(askVolume);

      ohlc.setBidOpen(nextLevelFirst.getOhlc().getBidOpen());
      ohlc.setBidHigh(Collections.max((HistoryDataKBarUtils.getPropertyList(nextLevelList,"getBidHigh"))));
      ohlc.setBidLow(Collections.max((HistoryDataKBarUtils.getPropertyList(nextLevelList,"getBidLow"))));
      ohlc.setBidClose(nextLevelEnd.getOhlc().getBidClose());
      Double bidVolume = 0d;
      for(Double d : HistoryDataKBarUtils.getPropertyList(nextLevelList,"getBidVolume")){
        bidVolume += d;
      }
      ohlc.setBidVolume(bidVolume);

      kbar.setOhlc(ohlc);
      return kbar;
    } else {
      return null;
    }
  }
}
