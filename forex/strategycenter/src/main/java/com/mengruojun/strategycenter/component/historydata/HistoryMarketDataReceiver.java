package com.mengruojun.strategycenter.component.historydata;

import com.mengruojun.common.service.HistoryMarketdataService;
import com.mengruojun.jms.domain.MarketDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * MarketDataReceiver
 */
//@Service("historyMarketDataReceiver")
@Deprecated
public class HistoryMarketDataReceiver implements MessageListener, ApplicationContextAware {
  Logger logger = Logger.getLogger(this.getClass());

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    logger.info("marketDataReceiver started");
  }

  @Autowired
  private HistoryMarketdataService historyMarketdataService;
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
        if (msgObj instanceof MarketDataMessage) {
          MarketDataMessage mdm = (MarketDataMessage) msgObj;
          historyMarketdataService.handle(mdm.convertToHistorydataKBar());
        }
      } catch (JMSException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      logger.error("Message should be a ObjectMessage in MarketDataTopic, but the message actuall is " + message);
    }
  }
}
