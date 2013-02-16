package com.mengruojun.strategycenter.component.jmsreceiver;

import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.strategycenter.springevent.ClientRegisterEvent;
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
import java.util.Date;
import java.util.TimeZone;

/**
 * client msg about
 * register client
 * specifying a strategy name
 * Client Type/name
 * Account Info Portfolio
 *

 */


@Service("clientDataReceiver")
public class ClientDataReceiver implements MessageListener, ApplicationContextAware {
    Logger logger = Logger.getLogger(this.getClass());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      logger.info("clientDataReceiver started");
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
                if (msgObj instanceof ClientInfoMessage) {
                    ClientInfoMessage cim = (ClientInfoMessage) msgObj;
                    logger.info("ClientInfoMessage: ");
                    logger.info("getClientId " + cim.getClientId());
                    logger.info("getStrategyId " + cim.getStrategyId());
                    logger.info("getBaseCurrency " + cim.getBaseCurrency());
                    logger.info("getBrokerType " + cim.getBrokerType());
                    logger.info("getLeverage " + cim.getLeverage());
                    logger.info("getCurrentBalance " + cim.getCurrentBalance());
                    logger.info("getCurrentEquity " + cim.getCurrentEquity());
                    logger.info("OpenPositionList Size " + cim.getOpenPositionList().size());
                    logger.info("PendingPositionList Size " + cim.getPendingPositionList().size());


                    logger.info("");
                    logger.info("");
                    logger.info("");
                    applicationContext.publishEvent(new ClientRegisterEvent(cim));

                }
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            logger.error("Message should be a ObjectMessage in MarketDataTopic, but the message actuall is " + message);
        }
    }
}
