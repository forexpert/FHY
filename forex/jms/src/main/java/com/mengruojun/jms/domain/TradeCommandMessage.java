package com.mengruojun.jms.domain;

import com.mengruojun.common.domain.enumerate.TradeCommandType;

import java.io.Serializable;

/**
 * TradeCommandMessage
 */
public class TradeCommandMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private TradeCommandType tradeCommandType;
    //todo


    public TradeCommandMessage() {
    }

    public TradeCommandType getTradeCommandType() {
        return tradeCommandType;
    }

    public void setTradeCommandType(TradeCommandType tradeCommandType) {
        this.tradeCommandType = tradeCommandType;
    }
}
