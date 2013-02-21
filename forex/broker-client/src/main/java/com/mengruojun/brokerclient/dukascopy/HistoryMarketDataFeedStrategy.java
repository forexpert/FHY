package com.mengruojun.brokerclient.dukascopy;

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
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.jms.utils.JMSSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This is a Dukascopy historyMarketDataFeedStrategy Strategy.
 */
@Service("historyMarketDataFeedStrategy")
public class HistoryMarketDataFeedStrategy implements IStrategy {
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
  private List<Instrument> dukascopyInstrumentList = Arrays.asList(Instrument.values());

  @Autowired
  private JMSSender marketDataSender;
  @Autowired
  private JMSSender clientInfoSender;


  public void onStart(final IContext context) throws JFException {
    this.context = context;
    engine = context.getEngine();
    indicators = context.getIndicators();
    this.console = context.getConsole();
    console.getOut().println("Started");
    try {
      this.getAllHistoryData();
    } catch (Exception e) {
      logger.error("", e);
    }

  }

  private void getHistoryData(Instrument instrument, long from_long, long to_long) throws JFException, ParseException {
    logger.info("Getting History Data: Instrument-->" + instrument +
            ";  from-->" + sdf.format(new Date(from_long)) +
            ";  to-->" + sdf.format(new Date(to_long))
              );
    List<IBar> askbars = this.context.getHistory().getBars(instrument, Period.TEN_SECS, OfferSide.ASK, from_long, to_long);
    List<IBar> bidbars = this.context.getHistory().getBars(instrument, Period.TEN_SECS, OfferSide.BID, from_long, to_long);
    for (int i = 0; i < askbars.size(); i++) {
      IBar askBar = askbars.get(i);
      IBar bidBar = bidbars.get(i);
      if (askBar.getTime() != bidBar.getTime()) {
        logger.error("askBars doesn't match bidBars");
        throw new RuntimeException("askBars doesn't match bidBars");
      }

      TimeWindowType twt = TimeWindowType.S10;
      MarketDataMessage mdm = new MarketDataMessage(askBar.getTime(),
              askBar.getOpen(), askBar.getHigh(), askBar.getLow(), askBar.getClose(),
              bidBar.getOpen(), bidBar.getHigh(), bidBar.getLow(), bidBar.getClose(),
              askBar.getVolume(), bidBar.getVolume(), instrument.getPrimaryCurrency(),
              instrument.getSecondaryCurrency(), twt);

      marketDataSender.sendObjectMessage(mdm);
    }

  }

  private void getAllHistoryData() throws JFException, ParseException {
    long oneday_long = 24 * 3600 * 1000L;
    long global_from_long = sdf.parse("2010.01.01 01:00:00 +0000").getTime();
    for (Instrument instrument : dukascopyInstrumentList) {
      long from_long = this.context.getDataService().getTimeOfFirstCandle(instrument, Period.TEN_SECS);
      from_long += 10*1000L;
      if(from_long < global_from_long){
        from_long = global_from_long;
      }
      while (new Date().getTime() > (from_long + oneday_long)) {
        getHistoryData(instrument, from_long, (from_long + oneday_long));
        from_long += oneday_long;
      }
    }
  }

  private String ibarToString(IBar ibar) {
    return "IBar: [" + ibar.getTime() + "] [OHLC is " + ibar.getOpen() + ", " + ibar.getHigh()
            + ", " + ibar.getLow() + ", " + ibar.getClose() + "]";
  }

  /**
   * register client by JMS to the Client Manager
   */
  private void registerClient() throws JFException {
    ClientInfoMessage cim = DukascopyUtils.generateClientInfoMessage(BrokerType.DukascopyMarketDataFeeder, this.context, null);
    clientInfoSender.sendObjectMessage(cim);
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
        /*if (period.equals(Period.TEN_SECS)) {
            TimeWindowType twt = TimeWindowType.S10;
            MarketDataMessage mdm = new MarketDataMessage(askBar.getTime(),
                    askBar.getOpen(), askBar.getHigh(), askBar.getLow(), askBar.getClose(),
                    bidBar.getOpen(), bidBar.getHigh(), bidBar.getLow(), bidBar.getClose(),
                    askBar.getVolume(), bidBar.getVolume(), instrument.getPrimaryCurrency(),
                    instrument.getSecondaryCurrency(), twt);

            marketDataSender.sendObjectMessage(mdm);
        }*/
  }

  public void onMessage(IMessage message) throws JFException {
  }

  public void onAccount(IAccount account) throws JFException {
    //registerClient();
  }
}
