package com.mengruojun.strategycenter.springevent;

import org.springframework.context.ApplicationEvent;

/**
 * ClientRegisterEvent, dispatched to ClientManger by spring event context
 */
public class ClientRegisterEvent extends ApplicationEvent {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 646140097162842368L;

    public ClientRegisterEvent(Object source){
        super(source);
    }
}
