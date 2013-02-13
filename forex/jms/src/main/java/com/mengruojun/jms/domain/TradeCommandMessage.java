package com.mengruojun.jms.domain;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.TradeCommandType;

import java.io.Serializable;

/**
 * TradeCommandMessage
 */
public class TradeCommandMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    Instrument instrument = null; //new Instrument(Currency.EUR, Currency.USD);
    Direction direction;
    /**
     * Amount in K unit
     */
    Double amount;
    Double openPrice;
    Double closePrice;
    Double stopLossPrice;
    Double takeProfitPrice;
    String positionId;
    private TradeCommandType tradeCommandType;



    public TradeCommandMessage() {
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public Double getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(Double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public Double getTakeProfitPrice() {
        return takeProfitPrice;
    }

    public void setTakeProfitPrice(Double takeProfitPrice) {
        this.takeProfitPrice = takeProfitPrice;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public TradeCommandType getTradeCommandType() {
        return tradeCommandType;
    }

    public void setTradeCommandType(TradeCommandType tradeCommandType) {
        this.tradeCommandType = tradeCommandType;
    }
}
