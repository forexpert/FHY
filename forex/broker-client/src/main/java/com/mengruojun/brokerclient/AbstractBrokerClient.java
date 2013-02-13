package com.mengruojun.brokerclient;

import com.mengruojun.common.domain.enumerate.BrokerType;

public abstract class AbstractBrokerClient {
    protected BrokerType brokerType;
    protected String clientId;

    public BrokerType getBrokerType() {
        return brokerType;
    }

    public void setBrokerType(BrokerType brokerType) {
        this.brokerType = brokerType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    //abstract void connect();


}