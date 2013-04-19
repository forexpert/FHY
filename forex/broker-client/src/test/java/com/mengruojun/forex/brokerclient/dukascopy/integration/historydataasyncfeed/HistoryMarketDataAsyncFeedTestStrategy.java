package com.mengruojun.forex.brokerclient.dukascopy.integration.historydataasyncfeed;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.LoadingDataListener;
import com.dukascopy.api.LoadingProgressListener;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.service.HistoryMarketdataService;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a Dukascopy historyMarketDataAsyncFeedTestStrategy.
 */
@Service("historyMarketDataAsyncFeedTestStrategy")
public class HistoryMarketDataAsyncFeedTestStrategy implements IStrategy {
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

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
  Instrument instrument = Instrument.XAUUSD;


  public void onStart(final IContext context) throws JFException {
    this.context = context;
    engine = context.getEngine();
    indicators = context.getIndicators();
    this.console = context.getConsole();
    console.getOut().println("Started");
    try {
      long global_from_long = sdf.parse(testBar_start).getTime();
      long global_to_long = sdf.parse(testBar_end).getTime();
      long from_long = global_from_long + testPeriod.getInterval(); //avoid exception, we skip the first bar. it seems that getTimeOfFirstCandle returns the first Bar's endtime;
      this.startAsyncFeed(instrument, global_from_long, global_to_long);
    } catch (Exception e) {
      logger.error("", e);
    }

  }

  final AtomicBoolean finishAsk = new AtomicBoolean(false);
  final AtomicBoolean finishBid = new AtomicBoolean(false);
  List<IBar> askbars = Collections.synchronizedList(new ArrayList<IBar>());
  List<IBar> bidbars = Collections.synchronizedList(new ArrayList<IBar>());

  private void startAsyncFeed(Instrument instrument, long global_from_long, long global_to_long) throws JFException {
    List<HistoryDataKBar> kBars = new ArrayList<HistoryDataKBar>();


    IHistory history = context.getHistory();
    history.readBars(instrument, Period.TEN_SECS, OfferSide.BID,
            global_from_long,
            global_to_long,
            new MockLoadingDataListener(bidbars), new MockLoadingProgressListener(finishBid)
    );
    history.readBars(instrument, Period.TEN_SECS, OfferSide.ASK,
            global_from_long,
            global_to_long,
            new MockLoadingDataListener(askbars), new MockLoadingProgressListener(finishAsk)
    );
  }

  public void onStop() throws JFException {
    for (IOrder order : engine.getOrders()) {
      order.close();
    }
    console.getOut().println("Stopped");
  }

  public void onTick(Instrument instrument, ITick tick) throws JFException {
  }

  public void onBar(Instrument instrument, Period period, IBar askBar_original, IBar bidBar_original) {
    if (instrument == this.instrument && period == Period.TEN_SECS) {
      if (this.finishAsk.get() && this.finishAsk.get()) {
        List<HistoryDataKBar> kBars = new ArrayList<HistoryDataKBar>();
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

        synchronized (serverBars){
          serverBars.addAll(kBars);
          serverBars.notify();
        }
      }

    }
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
}

class MockIbar implements IBar {
  double open;
  double close;
  double low;
  double high;
  double volume;
  long time;

  MockIbar(double open, double close, double low, double high, double volume, long time) {
    this.open = open;
    this.close = close;
    this.low = low;
    this.high = high;
    this.volume = volume;
    this.time = time;
  }

  public double getOpen() {
    return open;
  }

  public double getClose() {
    return close;
  }

  public double getLow() {
    return low;
  }

  public double getHigh() {
    return high;
  }

  public double getVolume() {
    return volume;
  }

  public long getTime() {
    return time;
  }
}

class MockLoadingProgressListener implements LoadingProgressListener {
  MockLoadingProgressListener(AtomicBoolean finish) {
    this.finish = finish;
  }

  AtomicBoolean finish;
  Logger logger = Logger.getLogger(this.getClass());

  public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
  }

  public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime) {
    if (allDataLoaded) {
      logger.info("All data loaded succesfully");
      finish.set(true);
    } else {
      logger.info("For some reason loading failed or was canceled by the user");
    }
  }

  public boolean stopJob() {
    return false;
  }
}

class MockLoadingDataListener implements LoadingDataListener {
  List<IBar> bars = null;

  MockLoadingDataListener(List<IBar> bars) {
    this.bars = bars;
  }

  Logger logger = Logger.getLogger(this.getClass());

  public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
    //logger.info("newTick -->" + time);
  }

  public void newBar(Instrument instrument, Period period, OfferSide side, final long time, final double open,
                     final double close, final double low, final double high, final double vol) {
    //logger.info("newBar -->" + time);

    bars.add(new MockIbar(open, close, low, high, vol, time));
  }
}