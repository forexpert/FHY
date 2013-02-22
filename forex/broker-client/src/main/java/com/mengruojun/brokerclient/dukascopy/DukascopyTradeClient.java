package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.IStrategy;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.mengruojun.brokerclient.AbstractBrokerClient;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dukascopy Trade Client
 */

public class DukascopyTradeClient extends AbstractBrokerClient implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DukascopyTradeClient.class);

    //url of the DEMO jnlp
    private String jnlpUrl;
    //user name
    private String userName;
    //password
    private String password;

    private IStrategy strategy;
  private List<Instrument> dukascopyInstrumentList = DukascopyUtils.getInterestInstrumentList();

    private void initClient() throws Exception {
        //get the instance of the IClient interface
        final IClient client = ClientFactory.getDefaultInstance();
        //set the listener that will receive system events
        client.setSystemListener(new DefaultSystemListenerImpl(client, jnlpUrl, userName, password));

        LOGGER.info("Connecting...");
        //connect to the server using jnlp, user name and password
        client.connect(jnlpUrl, userName, password);

        //wait for it to connect
        int i = 10; //wait max ten seconds
        while (i > 0 && !client.isConnected()) {
            Thread.sleep(1000);
            i--;
        }
        if (!client.isConnected()) {
            LOGGER.error("Failed to connect Dukascopy servers");
            System.exit(1);
        }

        //subscribe to the instruments
        Set<Instrument> instruments = new HashSet<Instrument>();
      for(Instrument instrument : dukascopyInstrumentList){
        instruments.add(instrument);
      }

        LOGGER.info("Subscribing instruments...");
        client.setSubscribedInstruments(instruments);

        //workaround for LoadNumberOfCandlesAction for JForex-API versions > 2.6.64
        Thread.sleep(5000);

        //start the strategy
        LOGGER.info("Starting strategy");
        client.startStrategy(strategy);
        //now it's running
    }

    public void start() {
        try {
            this.initClient();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //this.start();  to start it in main method
    }


    //getter and setter

    public String getJnlpUrl() {
        return jnlpUrl;
    }

    public void setJnlpUrl(String jnlpUrl) {
        this.jnlpUrl = jnlpUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public IStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }
}
