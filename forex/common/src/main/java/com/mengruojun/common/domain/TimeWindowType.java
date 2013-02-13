package com.mengruojun.common.domain;

public enum TimeWindowType {
    S10(10 * 1000),
    M1(60 * 1000),
    M5(60 * 5 * 1000),
    H1(3600 * 1000),
    H4(4 * 3600 * 1000),
    D1(24 * 3600 * 1000);

    private long timeInMillis;

    private TimeWindowType(long timeInMillis) {

    }

    private TimeWindowType() {

    }

    public long getTimeInMillis() {
        return timeInMillis;
    }
}
