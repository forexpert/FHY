package com.mengruojun.strategycenter.component.jmsreceiver;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.strategycenter.springevent.MarketDataReceivedEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * MarketDataReceiver
 */
@Service("marketDataReceiver")
public class MarketDataReceiver implements MessageListener, ApplicationContextAware {
    Logger logger = Logger.getLogger(this.getClass());

    static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

    {
      logger.info("marketDataReceiver started");
    }


    private ApplicationContext applicationContext;

    /**
     * @param applicationContext the applicationContext to set
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Object msgObj = ((ObjectMessage) message).getObject();
                if (msgObj instanceof Map) {
                    Map mdmMap = (Map<Instrument, MarketDataMessage>)msgObj;
                    if(mdmMap.size() > 0 && mdmMap.values().toArray()[0]  instanceof  MarketDataMessage){
                        logger.debug("mdmMap size should be " + TradingUtils.getInterestInstrumentList().size() + ", Actual is " + mdmMap.size());
                        applicationContext.publishEvent(new MarketDataReceivedEvent(mdmMap));
                    }
                }
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            logger.error("Message should be a ObjectMessage in MarketDataTopic, but the message actual is " + message);
        }
    }
}
