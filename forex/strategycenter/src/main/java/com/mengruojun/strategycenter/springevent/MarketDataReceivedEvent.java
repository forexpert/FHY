package com.mengruojun.strategycenter.springevent;

import org.springframework.context.ApplicationEvent;

/**
 * ClientRegisterEvent, dispatched to ClientManger by spring event context
 */
public class MarketDataReceivedEvent extends ApplicationEvent {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 646140097162842368L;

    public MarketDataReceivedEvent(Object source){
        super(source);
    }
}
