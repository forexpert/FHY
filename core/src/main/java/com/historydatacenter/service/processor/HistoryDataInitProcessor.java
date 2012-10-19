package com.historydatacenter.service.processor;

import au.com.bytecode.opencsv.CSVReader;
import com.historydatacenter.dao.HistoryDataKBarDao;
import com.historydatacenter.model.HistoryDataKBar;
import com.historydatacenter.model.Instrument;
import com.historydatacenter.model.OHLC;
import com.historydatacenter.model.enumerate.Currency;
import com.historydatacenter.model.enumerate.TimeWindowType;
import org.apache.log4j.Logger;
import org.compass.core.converter.extended.DataTimeConverter;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 1. parse data from original data file
 * 2. insert structured data to db
 */
@Service
public class HistoryDataInitProcessor implements Processor, ApplicationContextAware {
  ApplicationContext context;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
  Logger log = Logger.getLogger(HistoryDataInitProcessor.class);
  @Autowired
  HistoryDataKBarDao dao;


  public void run() {
    // loadHistoryData();   // this task has been down. There is no need to repeat it when each run
    generateDataAttribute();

  }

  /**
   * import historyData form text csv file to db.
   */
  private void loadHistoryData() {
    CSVReader reader_ask = null;
    CSVReader reader_bid = null;
    try {
      reader_ask = new CSVReader(
              new InputStreamReader(
                      ClassLoader.getSystemResourceAsStream("com/historydatacenter/EURUSD_5_Mins_Ask_2012_01_01__2012_10_11.csv"))
              , ' ');
      reader_bid = new CSVReader(
              new InputStreamReader(
                      ClassLoader.getSystemResourceAsStream("com/historydatacenter/EURUSD_5_Mins_Bid_2012_01_01__2012_10_11.csv"))
              , ' ');
      List<String[]> ask_data = reader_ask.readAll();
      List<String[]> bid_data = reader_bid.readAll();

      for(int i = 1 ; i < ask_data.size(); i++){
      //for (int i = 1; i < 5; i++) {
        //Time Open High Low Close Volume
        String[] ask = ask_data.get(i);
        String[] bid = bid_data.get(i);
        assert ask.length == 7;
        assert (bid.length == 7);
        assert (ask[0].equals(bid[0])); //same date
        assert (ask[1].equals(bid[1])); //same time

        if (ask[6].equals("0") && bid[6].equals("0") &&
                (ask[2].equals(ask[3]) && ask[3].equals(ask[4]) && ask[4].equals(ask[5])) &&
                (bid[2].equals(bid[3]) && bid[3].equals(bid[4]) && bid[4].equals(bid[5]))) { // if volume is 0, means it's not trading time. skip it.
          continue;
        }
        OHLC ohlc = new OHLC();
        ohlc.setAskOpen(Double.valueOf(ask[2]));
        ohlc.setAskHigh(Double.valueOf(ask[3]));
        ohlc.setAskLow(Double.valueOf(ask[4]));
        ohlc.setAskClose(Double.valueOf(ask[5]));
        ohlc.setBidOpen(Double.valueOf(bid[2]));
        ohlc.setBidHigh(Double.valueOf(bid[3]));
        ohlc.setBidLow(Double.valueOf(bid[4]));
        ohlc.setBidClose(Double.valueOf(bid[5]));

        DateTime openTime = new DateTime(sdf.parse(ask[0] + " " + ask[1]));
        DateTime closeTime = openTime.plusMinutes(5);

        HistoryDataKBar bar = new HistoryDataKBar(new Instrument(Currency.EUR, Currency.USD), TimeWindowType.M5,
                openTime.getMillis(), closeTime.getMillis(), ohlc);

        dao.save(bar);

      }
      log.info("Loading Finished");
    } catch (FileNotFoundException e) {
      log.error("", e);
    } catch (IOException e) {
      log.error("", e);
    } catch (ParseException e) {
      log.error("", e);
    }
  }

  /**
   * generate indicates
   */
  private void generateDataAttribute() {

  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }
}
