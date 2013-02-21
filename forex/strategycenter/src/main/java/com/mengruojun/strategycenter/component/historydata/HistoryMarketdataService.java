package com.mengruojun.strategycenter.component.historydata;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.IndicatorType;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Service
public class HistoryMarketdataService {
  Logger logger = Logger.getLogger(this.getClass());

  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  /**
   * This method will be called by marketDate JMS Receiver .
   * This method will do the following on the fly.
   * <p/>
   * 1. save bar into DB
   * 2. generate bars into DB
   * 3. computing indicators and save into DB
   *
   * @param marketDataMessage marketDataMessage is only based on 10S bars
   */
  public void handle(MarketDataMessage marketDataMessage) {
    if (marketDataMessage.getTimeWindowType() == TimeWindowType.S10) {
      List<TimeWindowType> timeWindowTypeList = Arrays.asList(TimeWindowType.values());
      List<IndicatorType> indicatorTypeList = Arrays.asList(IndicatorType.values());
      long barStartTime = marketDataMessage.getStartTime();
      long barEndTime = marketDataMessage.getStartTime() + marketDataMessage.getTimeWindowType().getTimeInMillis();

      // 1 save 10s bar into DB
      save10SBarIntoDB(marketDataMessage);
      // 2 generate bars in all timewindowTypes.
      generateAndSaveBarsInAllTimewindowTypes(timeWindowTypeList, barEndTime);
      // 3. computing indicators and save into DB
      generateIndicatorsForAllBars(indicatorTypeList, timeWindowTypeList, barEndTime);
    }


  }

  /**
   * generate indicators in indicatorTypeList for bars starting from barStartTime in timeWindowTypeList
   *
   * @param indicatorTypeList  indicator type list
   * @param timeWindowTypeList bars in time window type list
   * @param barEndTime       the indicators which are to be generated are should be associated with the bars end with the barEndTime
   */
  private void generateIndicatorsForAllBars(List<IndicatorType> indicatorTypeList, List<TimeWindowType> timeWindowTypeList, long barEndTime) {

  }

  /**
   * generate bars starting from barStartTime in timeWindowTypeList
   * @param timeWindowTypeList  timeWindowTypeList
   * @param barEndTime        end with the barEndTime
   */
  private void generateAndSaveBarsInAllTimewindowTypes(List<TimeWindowType> timeWindowTypeList, long barEndTime) {


  }

  /**
   * convert MarketDataMessage to HistoryDataKBar and save into db
   * @param marketDataMessage  marketDataMessage
   */
  private void save10SBarIntoDB(MarketDataMessage marketDataMessage) {
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


    OHLC ohlc = new OHLC(askOpen, askHigh, askLow, askClose, askVolume,
            bidOpen, bidHigh, bidLow, bidClose, bidVolume);
    HistoryDataKBar kbar = new HistoryDataKBar(instrument, timeWindowType, openTime, openTime + timeWindowType.getTimeInMillis(), ohlc);
    //todo cmeng -- consider if there is really a update case
    save(kbar);

  }

  /**
   * save kbar into db
   * @param kbar kbar
   */
  private void save(HistoryDataKBar kbar) {
    historyDataKBarDao.save(kbar);
    logger.info("Save a Kbar: " + kbar.toString());
  }

  /**
   * Now we have only limited bars and indicators. In the future, we can add more indicators and bars.
   * So we may need to rebuild all Bars and indicators
   */
  public void rebuildAllBarsAndIndicators() {
    //to do cmeng
  }

}
