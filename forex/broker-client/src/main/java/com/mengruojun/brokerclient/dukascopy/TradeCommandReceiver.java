package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.IContext;
import com.mengruojun.jms.domain.ClientInfoMessage;
import com.mengruojun.jms.domain.TradeCommandMessage;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * To receive trade command from client manager, then send them to dukascopy broker server
 */

public class TradeCommandReceiver{
    Logger logger = Logger.getLogger(this.getClass());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public TradeCommandReceiver(IContext context, JmsTemplate template, Destination destination) {
        this.context = context;
        this.template = template;
        this.destination = destination;
    }

    IContext context;
    private JmsTemplate template;
    private Destination destination;

    public void receive()  throws JMSException, InterruptedException {
        while (true) {
            Message message = template.receive(destination);
            if (message != null)
                onMessage(message);
            else
                break;
        }
    }
    public void onMessage(Message message){

        if (message instanceof ObjectMessage) {
            try {
                Object msgObj = ((ObjectMessage) message).getObject();
                if (msgObj instanceof TradeCommandMessage) {
                    TradeCommandMessage tcm = (TradeCommandMessage) msgObj;
                    logger.info("TradeCommandMessage: ");
                    logger.info("getTradeCommandType " + tcm.getTradeCommandType());

                    logger.info("");
                    logger.info("");
                    logger.info("");
                    //todo cmeng to handler the command
                    handleCommand(tcm);
                }
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            logger.error("Message should be a ObjectMessage in MarketDataTopic, but the message actuall is " + message);
        }
    }

    private void handleCommand(TradeCommandMessage tcm) {
        //todo
        //context.getEngine().submitOrder()...
        logger.info("sending TradeCommandMessage to Dukascopy server" + tcm);

    }
}
