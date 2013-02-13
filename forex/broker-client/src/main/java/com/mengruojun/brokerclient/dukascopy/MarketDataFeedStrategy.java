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
import com.dukascopy.api.Period;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.utils.JMSSender;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * This is a Dukascopy marketDataFeedStrategy Strategy, which won't do anything about trading but just send market data msg to
 * MarketDataReceiver.
 */
@Service("marketDataFeedStrategy")
public class MarketDataFeedStrategy implements IStrategy {
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
    }

    /**
     * register client by JMS to the Client Manager
     */
    private void registerClient() throws JFException {
        ClientInfoMessage cim = DukascopyUtils.generateClientInfoMessage(BrokerType.DukascopyMarketDataFeeder, this.context);
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
        if (period.equals(Period.TEN_SECS)) {
            TimeWindowType twt = TimeWindowType.S10;
            MarketDataMessage mdm = new MarketDataMessage(askBar.getTime(),
                    askBar.getOpen(), askBar.getHigh(), askBar.getLow(), askBar.getClose(),
                    bidBar.getOpen(), bidBar.getHigh(), bidBar.getLow(), bidBar.getClose(),
                    askBar.getVolume(), bidBar.getVolume(), instrument.getPrimaryCurrency(),
                    instrument.getSecondaryCurrency(), twt);

            marketDataSender.sendObjectMessage(mdm);
        }
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
        registerClient();
    }
}
