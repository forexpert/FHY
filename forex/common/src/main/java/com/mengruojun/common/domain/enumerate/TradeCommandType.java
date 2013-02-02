package com.mengruojun.common.domain.enumerate;

/**
 * TradeCommandType
 */
public enum TradeCommandType {
    /**
     * open a position at once by using market price
     */
    openAtMarketPrice,

    /**
     * open a position by setting a price, which could be filled by broker server later or cancel. In one word,
     * this should be a pending position
     */
    openAtSetPrice,

    /**
     * once a pending position created, we can change the amount, SL/TP, set price, or any other properties
     */
    change,

    /**
     * close a opened position
     */
    close,

    /**
     * cancel a pending position
     */
    cancel
}
