package com.mengruojun.common.service;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
   * @param kbar kbar is only based on 10S bars
   */
  @Deprecated
  public void handle(HistoryDataKBar kbar) {
    logger.error("this method is deprecated!");
  }


  /**
   * save bars in batch mode first , if error occurs, save one by one
   * @param kbars
   */
  private void saveBars(List<HistoryDataKBar> kbars){
    try {
      historyDataKBarDao.batchSave(kbars);
      logger.info("Saved Kbars size: " + kbars.size());
    } catch (Exception e) {
      logger.error("", e);

      logger.info("there might be already one duplicated record in batch mode, now insert one by one:");
      // when exception occurs, save one by one
      for (HistoryDataKBar bar : kbars) {
        save(bar);
      }
    }
  }


  /**
   *  // 1 save 10s bar into DB
   saveBars(kbar);
   // 2 generate bars in all timewindowTypes.
   generateAndSaveBarsInAllTimewindowTypes(timeWindowTypeList, barEndTime);
   // 3. computing indicators and save into DB
   generateIndicatorsForAllBars(indicatorTypeList, timeWindowTypeList, barEndTime);

   * It seems we don't need to save other bars and indicators into DB. Instead, we can compute them on the fly and maintain them as a fixed size collection
   * @param kbars
   */
  public void handle(List<HistoryDataKBar> kbars) {
    saveBars(kbars);
  }


  /**
   * save kbar into db
   *
   * @param kbar kbar
   */
  private void save(HistoryDataKBar kbar) {
    if (historyDataKBarDao.find(kbar.getOpenTime(), kbar.getInstrument(), kbar.getTimeWindowType()) == null) {
      try {
        historyDataKBarDao.save(kbar);
        logger.debug("Saved a Kbar: " + kbar.toString());
      } catch (Exception e) {
        logger.error("there might be already one duplicated record", e);

      }
    }

  }
/*
  *//**
   * save giving kbar first, if "duplicated row exception", catch and log it;
   * @param kbar
   *//*
  public void directSave(HistoryDataKBar kbar){
    try {
      historyDataKBarDao.save(kbar);
      logger.debug("Saved a Kbar: " + kbar.toString());
    } catch (Exception e) {
      logger.error("there might be already one duplicated record", e);

    }
  }*/

  public HistoryDataKBar getLatestBarForPeriod(Instrument instrument, TimeWindowType timeWindowType) {
    return historyDataKBarDao.getLatestBarForPeriod(instrument, timeWindowType);
  }


}
