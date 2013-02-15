package com.mengruojun.strategycenter.component.strategy;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A simpleStrategy just for test purpose
 */
public class SampleStrategy extends BaseStrategy {
    public SampleStrategy() {

    }

    @Override
    public List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {
        List<TradeCommandMessage> tcmList = new ArrayList<TradeCommandMessage>();
        TradeCommandMessage tcm = new TradeCommandMessage();
        tcm.setAmount(1d);
        tcm.setInstrument(Instrument.MakeInstrument(Currency.EUR,Currency.USD));
        tcm.setTradeCommandType(TradeCommandType.openAtMarketPrice);
        tcm.setDirection(Direction.Long);
        tcmList.add(tcm);
        return tcmList;
    }
}
