package com.mengruojun.strategycenter.component.marketdata;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * this MarketData is used for live data management
 *
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
     *      For Each computing the max last level data size is only 6;
     * 2. But we also need to compute indicators. So let's keep 500 here;
     */
    long eachKBarMapMaxSize = 500;

    private Map<Instrument, Map<TimeWindowType, Map<Long, HistoryDataKBar>>> kbarMap =
            new ConcurrentHashMap<Instrument, Map<TimeWindowType, Map<Long, HistoryDataKBar>>>();



    /**
     * push a marketData in this manager.  Then the manager can maintain marketData by:
     * 1) push into memory
     * 3) computing indicators and attributes and push them into memory
     */
    public void push(Map<Instrument, MarketDataMessage> s10MdmMap) {

        //todo next cmeng



    }


    private void putKBarIntoMap(HistoryDataKBar kbar, Instrument instrument, Long openTime, TimeWindowType timeWindowType) {
        synchronized (kbarMap){
            if(kbarMap.get(instrument) == null) {
                kbarMap.put(instrument, new HashMap<TimeWindowType, Map<Long, HistoryDataKBar>>());
            }
            if(kbarMap.get(instrument).get(timeWindowType) == null){
                kbarMap.get(instrument).put(timeWindowType, new HashMap<Long, HistoryDataKBar>());
            }
            kbarMap.get(instrument).get(timeWindowType).put(openTime, kbar);
        }
    }

    private boolean containKbar(Instrument instrument, Long openTime, TimeWindowType timeWindowType){
        if(kbarMap.get(instrument) == null || kbarMap.get(instrument).get(openTime) == null ||
                kbarMap.get(instrument).get(openTime).get(timeWindowType) == null){
            return false;
        } else {
            return true;
        }
    }

    /**
     * return KBar from memory  by giving startTime, instrument and timeWidowType
     *
     * @param openTime
     * @param timeWindowType
     * @return
     */
    public HistoryDataKBar getKBar(Long openTime, Instrument instrument, TimeWindowType timeWindowType) {
        HistoryDataKBar kbar = kbarMap.get(instrument).get(openTime).get(timeWindowType);
        return kbar;
    }
}
