package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.IStrategy;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 1/25/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("dukascopyTradeClient")
public class DukascopyTradeClient implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(DukascopyTradeClient.class);

  //url of the DEMO jnlp
  private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
  //user name
  private static String userName = "DEMO2PNQsL";
  //password
  private static String password = "PNQsL";

  @Autowired
  private IStrategy msgRouterStrategy;

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
    instruments.add(Instrument.EURUSD);
    LOGGER.info("Subscribing instruments...");
    client.setSubscribedInstruments(instruments);

    //workaround for LoadNumberOfCandlesAction for JForex-API versions > 2.6.64
    Thread.sleep(5000);

    //start the strategy
    LOGGER.info("Starting strategy");
    client.startStrategy(msgRouterStrategy);
    //now it's running
  }

  public void start(){
    try{
      this.initClient();
    } catch (Exception e){
      LOGGER.error("", e);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.start();
  }
}
