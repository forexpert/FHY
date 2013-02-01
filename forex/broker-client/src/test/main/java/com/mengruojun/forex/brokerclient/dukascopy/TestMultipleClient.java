package com.mengruojun.forex.brokerclient.dukascopy;

import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.mengruojun.brokerclient.dukascopy.DefaultSystemListenerImpl;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyClientFactory;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 1/31/13
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestMultipleClient  extends TestCase {

  @Test
  public void testMultipleClient() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    IClient client1 = DukascopyClientFactory.createNewInstance();
    IClient client2 = DukascopyClientFactory.createNewInstance();

    IClient client3 = ClientFactory.getDefaultInstance();
    IClient client4 = ClientFactory.getDefaultInstance();
    assertNotSame(client1, client2);
    assertEquals(client3, client4);
  }

  @Test
  @Ignore
  public void testMultipleClientConnection() throws Exception {
    IClient client1 = DukascopyClientFactory.createNewInstance();
    IClient client2 = DukascopyClientFactory.createNewInstance();
    //url of the DEMO jnlp
    String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
    //user name
    String userName1 = "DEMO2PNQsL";
    //password
    String password1 = "PNQsL";

    //user name
    String userName2 = "DEMO2iCdRy";
    //password
    String password2 = "iCdRy";
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

    //set the listener that will receive system events
    client2.setSystemListener(new DefaultSystemListenerImpl(client2, jnlpUrl, userName2, password2));

    //connect to the server using jnlp, user name and password
    client2.connect(jnlpUrl, userName2, password2);
    //wait for it to connect
     i = 10; //wait max ten seconds
    while (i > 0 && !client2.isConnected()) {
      Thread.sleep(1000);
      i--;
    }
    assertEquals(true, client2.isConnected());
    client2.startStrategy(new MA_Play());

  }

}
