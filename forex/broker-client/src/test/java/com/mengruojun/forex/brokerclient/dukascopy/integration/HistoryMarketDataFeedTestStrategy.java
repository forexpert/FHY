package com.mengruojun.forex.brokerclient.dukascopy.integration;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.service.HistoryMarketdataService;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This is a Dukascopy historyMarketDataFeedStrategy Strategy.
 */
@Service("historyMarketDataFeedTestStrategy")
public class HistoryMarketDataFeedTestStrategy implements IStrategy {
  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  private IEngine engine = null;
  private IContext context = null;
  private IIndicators indicators = null;
  private int tagCounter = 0;
  private double[] ma1 = new double[Instrument.values().length];
  private IConsole console;
  Logger logger = Logger.getLogger(this.getClass());
  private List<Instrument> dukascopyInstrumentList = DukascopyUtils.getInterestInstrumentList();

  @Autowired
  HistoryMarketdataService historyMarketdataService;
  private List<HistoryDataKBar> serverBars;
  Period testPeriod;
  String testBar_start;
  String testBar_end;
  Instrument instrument;

  public void onStart(final IContext context) throws JFException {
    this.context = context;
    engine = context.getEngine();
    indicators = context.getIndicators();
    this.console = context.getConsole();
    console.getOut().println("Started");
    try {
      synchronized (serverBars){
        long timeTick = context.getDataService().getTimeOfFirstCandle(instrument, testPeriod);
        logger.info("The earliest bar openTime is " + sdf.format(new Date(timeTick)));
        serverBars.addAll(this.getAllHistoryData(instrument, testPeriod, testBar_start, testBar_end));
        serverBars.notify();
      }
    } catch (Exception e) {
      logger.error("", e);
    }

  }

  private List<HistoryDataKBar> getHistoryData(Period period, Instrument instrument, long from_long, long to_long) throws JFException, ParseException {
    List<HistoryDataKBar> kBars = new ArrayList<HistoryDataKBar>();
    try {

      logger.info("Getting History Data: Instrument-->" + instrument +
              ";  from-->" + sdf.format(new Date(from_long)) +
              ";  to-->" + sdf.format(new Date(to_long))
      );
      List<IBar> askbars = this.context.getHistory().getBars(instrument, period, OfferSide.ASK, Filter.WEEKENDS, from_long, to_long);
      List<IBar> bidbars = this.context.getHistory().getBars(instrument, period, OfferSide.BID, Filter.WEEKENDS, from_long, to_long);
      for (int i = 0; i < askbars.size(); i++) {
        IBar askBar = askbars.get(i);
        IBar bidBar = bidbars.get(i);
        if (askBar.getTime() != bidBar.getTime()) {
          logger.error("askBars doesn't match bidBars");
          throw new RuntimeException("askBars doesn't match bidBars");
        }

        TimeWindowType twt = DukascopyUtils.convertPeriodToTimeWindowType(period);
        MarketDataMessage mdm = new MarketDataMessage(askBar.getTime(),
                askBar.getOpen(), askBar.getHigh(), askBar.getLow(), askBar.getClose(),
                bidBar.getOpen(), bidBar.getHigh(), bidBar.getLow(), bidBar.getClose(),
                askBar.getVolume(), bidBar.getVolume(), instrument.getPrimaryCurrency(),
                instrument.getSecondaryCurrency(), twt);
        HistoryDataKBar kbar = mdm.convertToHistorydataKBar();
        kBars.add(kbar);
      }
    } catch (Exception e) {
      logger.error("", e);
      logger.info("run again in 5 minutes:");
      try {
        Thread.sleep(5 * 60 * 1000L);
      } catch (InterruptedException e1) {
        logger.error("", e1);
      }
      getHistoryData(period, instrument, from_long, to_long);
    }

    return kBars;
  }

  private List<HistoryDataKBar> getAllHistoryData(Instrument instrument, Period period, String from, String to) throws JFException, ParseException {
    List<HistoryDataKBar> kBars = new ArrayList<HistoryDataKBar>();

    long intervalEachTimeForGetData = period.getInterval() * 1000; // 1000 rows each time

    long global_from_long = sdf.parse(from).getTime();
    long global_to_long = sdf.parse(to).getTime();


    //for (Instrument instrument : dukascopyInstrumentList) {

    long from_long = global_from_long + period.getInterval(); //avoid exception, we skip the first bar. it seems that getTimeOfFirstCandle returns the first Bar's endtime;


    while (true) {
      if (global_to_long > (from_long + intervalEachTimeForGetData)) {
        long to_long = from_long + intervalEachTimeForGetData;
        kBars.addAll(getHistoryData(period, instrument, from_long, to_long));

        from_long += intervalEachTimeForGetData;
      } else {
        kBars.addAll(getHistoryData(period, instrument, from_long, global_to_long));
        break;
      }
    }

    return kBars;
  }

  private String ibarToString(IBar ibar) {
    return "IBar: [" + ibar.getTime() + "] [OHLC is " + ibar.getOpen() + ", " + ibar.getHigh()
            + ", " + ibar.getLow() + ", " + ibar.getClose() + "]";
  }

  public void onStop() throws JFException {
    for (IOrder order : engine.getOrders()) {
      order.close();
    }
    console.getOut().println("Stopped");
  }

  public void onTick(Instrument instrument, ITick tick) throws JFException {
  }

  public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {

  }

  public void onMessage(IMessage message) throws JFException {
  }

  public void onAccount(IAccount account) throws JFException {
    //registerClient();
  }

  public void setServerBars(List<HistoryDataKBar> serverBars) {
    this.serverBars = serverBars;
  }

  public List<HistoryDataKBar> getServerBars() {
    return serverBars;
  }


  public String getTestBar_start() {
    return testBar_start;
  }

  public void setTestBar_start(String testBar_start) {
    this.testBar_start = testBar_start;
  }

  public String getTestBar_end() {
    return testBar_end;
  }

  public void setTestBar_end(String testBar_end) {
    this.testBar_end = testBar_end;
  }

  public Period getTestPeriod() {
    return testPeriod;
  }

  public void setTestPeriod(Period testPeriod) {
    this.testPeriod = testPeriod;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }
}
