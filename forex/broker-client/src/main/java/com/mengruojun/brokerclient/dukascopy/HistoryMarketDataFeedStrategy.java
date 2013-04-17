package com.mengruojun.brokerclient.dukascopy;

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
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.service.HistoryMarketdataService;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.jms.utils.JMSSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

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
    private List<Instrument> dukascopyInstrumentList = DukascopyUtils.getInterestInstrumentList();

    @Autowired
    private JMSSender marketDataSender;
    @Autowired
    private JMSSender clientInfoSender;
    @Autowired
    HistoryMarketdataService historyMarketdataService;

    private String globalFromStr = "2010.03.01 00:00:00 +0000";

    public void onStart(final IContext context) throws JFException {
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        try {
            context.executeTask(new Callable() {   // use asyn mode to get history data to avoid the warning mes like
            // 'Strategy thread queue overloaded with tasks. Ticks in queue - 2, bars - 0, other tasks - 838'
                @Override
                public Object call() throws Exception {
                    this.getAllHistoryData(Period.TEN_SECS);
                    return null;
                }

                private void getHistoryData(Period period, Instrument instrument, long from_long, long to_long) throws JFException, ParseException {
                    try {

                        logger.info("Getting History Data: Instrument-->" + instrument +
                                ";  from-->" + sdf.format(new Date(from_long)) +
                                ";  to-->" + sdf.format(new Date(to_long))
                        );
                        List<IBar> askbars = context.getHistory().getBars(instrument, period, OfferSide.ASK, Filter.WEEKENDS, from_long, to_long);
                        List<IBar> bidbars = context.getHistory().getBars(instrument, period, OfferSide.BID, Filter.WEEKENDS, from_long, to_long);
                        logger.info("Getting ends");

                        //kbars which will be inserted.
                        List<HistoryDataKBar> kbars = new ArrayList<HistoryDataKBar>();
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
                            //historyMarketdataService.handle(kbar);
                            kbars.add(kbar);
                        }
                        logger.info("saving" + kbars.size() + " bars start");
                        historyMarketdataService.handle(kbars);
                        logger.info("saving" + kbars.size() + " bars end");
                    } catch (Exception e) {
                        logger.error("", e);
                        logger.error("run again in 5 minutes:");
                        try {
                            Thread.sleep(5 * 60 * 1000L);
                        } catch (InterruptedException e1) {
                            logger.error("", e1);
                        }
                        getHistoryData(period, instrument, from_long, to_long);
                    }

                }

                private void getAllHistoryData(Period period) throws JFException, ParseException {
                    long intervalEachTimeForGetData = period.getInterval() * 9999; // 10000 rows each time

                    long global_from_long = sdf.parse(globalFromStr).getTime();
                    for (Instrument instrument : dukascopyInstrumentList) {
                        HistoryDataKBar db_latest = historyMarketdataService.getLatestBarForPeriod(
                                new com.mengruojun.common.domain.Instrument(instrument.getPrimaryCurrency() + "/" + instrument.getSecondaryCurrency()),
                                DukascopyUtils.convertPeriodToTimeWindowType(period)
                        );

                        long from_long = 0;
                        if (db_latest == null) {
                            from_long = context.getDataService().getTimeOfFirstCandle(instrument, period);
                            from_long += period.getInterval(); //avoid exception, we skip the first bar. it seems that getTimeOfFirstCandle returns the first Bar's endtime;
                        } else {         //continue last execution
                            from_long = db_latest.getOpenTime();
                        }
                        if (from_long < global_from_long) {
                            from_long = global_from_long;
                        }

                        while (true) {
                            if (new Date().getTime() > (from_long + intervalEachTimeForGetData)) {
                                long to_long = from_long + intervalEachTimeForGetData;
                                getHistoryData(period, instrument, from_long, to_long);
                                from_long = to_long + period.getInterval();
                            } else {// the remain history data  : The getBar method returns an IBar by shift. Shift of value 0 refers to the current candle that's not is not yet fully formed, 1 - latest fully formed candle, 2 - second to last candle.
                                IBar prevBar = context.getHistory().getBar(instrument, period, OfferSide.BID, 1);
                                if (prevBar != null && from_long <= prevBar.getTime()) {
                                    getHistoryData(period, instrument, from_long, prevBar.getTime());
                                }
                                break;
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("", e);
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
    }
}
