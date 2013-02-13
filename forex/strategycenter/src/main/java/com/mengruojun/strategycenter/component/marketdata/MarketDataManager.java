package com.mengruojun.strategycenter.component.marketdata;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.strategycenter.component.marketdata.dao.HistoryDataKBarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this MarketData is used for
 */
@Service
public class MarketDataManager {

    @Autowired
    HistoryDataKBarDao historyDataKBarDao;

    long kbarListMemorySize = 50000;

    private List<HistoryDataKBar> kbarList = new ArrayList<HistoryDataKBar>();

    private Map<Instrument, Map<Long, Map<TimeWindowType, HistoryDataKBar>>> kbarMap = new ConcurrentHashMap<Instrument, Map<Long, Map<TimeWindowType, HistoryDataKBar>>>();

    /**
     * init kbarMap from DB
     */
    public void init(){

    }

    /**
     * we may need to supplement KBar from file from Dukascopy
     */
    public void insertOrUpdateFromFile(){

    }

    /**
     * push a marketData in this manager.  Then the manager can maintain marketData by:
     * 1) push into memory
     * 2) push into DB
     * 3) computing indicators and attributes
     */
    public void push(MarketDataMessage marketDataMessage) {
        Instrument instrument = new Instrument(marketDataMessage.getCurrency1() + "/" + marketDataMessage.getCurrency2());
        Long openTime = marketDataMessage.getStartTime();
        TimeWindowType timeWindowType = marketDataMessage.getTimeWindowType();

        double askOpen = marketDataMessage.getAskOpen();
        double askHigh = marketDataMessage.getAskHigh();
        double askLow = marketDataMessage.getAskLow();
        double askClose = marketDataMessage.getAskClose();
        double askVolume = marketDataMessage.getAskVolume();

        double bidOpen = marketDataMessage.getBidOpen();
        double bidHigh = marketDataMessage.getBidHigh();
        double bidLow = marketDataMessage.getBidLow();
        double bidClose = marketDataMessage.getBidClose();
        double bidVolume = marketDataMessage.getBidVolume();

        if (containKbar(instrument, openTime, timeWindowType)) {
            OHLC ohlc = new OHLC(askOpen, askHigh, askLow, askClose, askVolume,
                    bidOpen, bidHigh, bidLow, bidClose, bidVolume);

            HistoryDataKBar kbar = new HistoryDataKBar(instrument, timeWindowType, openTime, openTime + timeWindowType.getTimeInMillis(), ohlc);

            putKBarIntoMap(kbar, instrument, openTime, timeWindowType);

            saveOrUpdate(kbar);

        } else {
            // update into kbarMap
        }


    }

    /**
     * save kbar into db
     * @param kbar
     */
    private void saveOrUpdate(HistoryDataKBar kbar) {
    }

    private void putKBarIntoMap(HistoryDataKBar kbar, Instrument instrument, Long openTime, TimeWindowType timeWindowType) {
        synchronized (kbarMap){
            if(kbarMap.get(instrument) == null) {
                kbarMap.put(instrument, new HashMap<Long, Map<TimeWindowType, HistoryDataKBar>>());
            }
            if(kbarMap.get(instrument).get(openTime) == null){
                kbarMap.get(instrument).put(openTime, new HashMap<TimeWindowType, HistoryDataKBar>());
            }
            kbarMap.get(instrument).get(openTime).put(timeWindowType, kbar);
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
     * return KBar from memory or DB by giving startTime, instrument and timeWidowType
     *
     * @param openTime
     * @param timeWindowType
     * @return
     */
    public HistoryDataKBar getKBar(Long openTime, Instrument instrument, TimeWindowType timeWindowType) {
        HistoryDataKBar kbar = kbarMap.get(instrument).get(openTime).get(timeWindowType);
        if (kbar != null) {
            return kbar;
        } else {
            return getKBarFromDB(openTime, instrument, timeWindowType);
        }
    }

    private HistoryDataKBar getKBarFromDB(Long openTime, Instrument instrument, TimeWindowType timeWindowType) {

        // todo cmeng
        return null;
    }
}
