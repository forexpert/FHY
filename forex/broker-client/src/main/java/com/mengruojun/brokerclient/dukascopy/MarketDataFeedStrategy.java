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

import java.util.*;

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
    private List<Instrument> dukascopyInstrumentList = DukascopyUtils.getInterestInstrumentList();

    @Autowired
    private JMSSender marketDataSender;
    @Autowired
    private JMSSender clientInfoSender;

    private Map<com.mengruojun.common.domain.Instrument, MarketDataMessage> marketDataMessageMap = new HashMap<com.mengruojun.common.domain.Instrument, MarketDataMessage>();


    public void onStart(final IContext context) throws JFException {
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.console = context.getConsole();
        console.getOut().println("Started");
        registerClient();
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
        if (period.equals(Period.TEN_SECS)) {
            TimeWindowType twt = TimeWindowType.S10;
            MarketDataMessage mdm = new MarketDataMessage(askBar.getTime(),
                    askBar.getOpen(), askBar.getHigh(), askBar.getLow(), askBar.getClose(),
                    bidBar.getOpen(), bidBar.getHigh(), bidBar.getLow(), bidBar.getClose(),
                    askBar.getVolume(), bidBar.getVolume(), instrument.getPrimaryCurrency(),
                    instrument.getSecondaryCurrency(), twt);
            com.mengruojun.common.domain.Instrument  fiInstrument = DukascopyUtils.fromDukascopyInstrument(instrument);

            if(marketDataMessageMap.get(instrument) != null){
                //理论上不会走到这一步
                logger.error("Dukascopy Markder Data Feeder has error. marketDataMessageMap didn't collect all interested data!. Now clear the map!");
                marketDataMessageMap.clear();
            }
            marketDataMessageMap.put(fiInstrument, mdm);
            if(marketDataMessageMap.size() == dukascopyInstrumentList.size()){
                marketDataSender.sendObjectMessage(marketDataMessageMap);
                marketDataMessageMap = new HashMap<com.mengruojun.common.domain.Instrument, MarketDataMessage>();
            }
        }
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
    }
}
