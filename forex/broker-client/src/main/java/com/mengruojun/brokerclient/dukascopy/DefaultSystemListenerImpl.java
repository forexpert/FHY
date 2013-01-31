package com.mengruojun.brokerclient.dukascopy;

import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultSystemListenerImpl
 */
public class DefaultSystemListenerImpl implements ISystemListener {
  IClient client = null;
  String jnlpUrl;
  String userName;
  String password;

  public DefaultSystemListenerImpl(IClient client, String jnlpUrl, String userName, String password){
    this.client = client;
    this.jnlpUrl = jnlpUrl;
    this.userName = userName;
    this.password = password;
  }
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSystemListenerImpl.class);
  private int lightReconnects = 3;

  @Override
  public void onStart(long processId) {
    LOGGER.info("Strategy started: " + processId);
  }

  @Override
  public void onStop(long processId) {
    LOGGER.info("Strategy stopped: " + processId);
    if (client.getStartedStrategies().size() == 0) {
      System.exit(0);
    }
  }

  @Override
  public void onConnect() {
    LOGGER.info("Connected");
    lightReconnects = 3;
  }

  @Override
  public void onDisconnect() {
    LOGGER.warn("Disconnected");
    if (lightReconnects > 0) {
      client.reconnect();
      --lightReconnects;
    } else {
      try {
        //sleep for 10 seconds before attempting to reconnect
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        //ignore
      }
      try {
        client.connect(jnlpUrl, userName, password);
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }
}
