package com.mengruojun.forex.brokerclient.dukascopy;

import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.mengruojun.brokerclient.dukascopy.DefaultSystemListenerImpl;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 1/31/13
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestMultipleClient{

  @Test
  @Ignore
  public void testClientConnection() throws Exception {
    IClient client1 = ClientFactory.getDefaultInstance();
    //url of the DEMO jnlp
    String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
    //user name
    String userName1 = "DEMO2iCdRy";
    //password
    String password1 = "iCdRy";
    //set the listener that will receive system events
    client1.setSystemListener(new DefaultSystemListenerImpl(client1, jnlpUrl, userName1, password1));

    //connect to the server using jnlp, user name and password
    client1.connect(jnlpUrl, userName1, password1);
    //wait for it to connect
    int i = 10; //wait max ten seconds
    while (i > 0 && !client1.isConnected()) {
      Thread.sleep(1000);
      i--;
    }
    assertEquals(true, client1.isConnected());

    client1.startStrategy(new MA_Play());
    //client1..context.getAccount().getLeverage()
  }

}
