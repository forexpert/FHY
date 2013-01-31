package com.mengruojun.brokerclient.dukascopy.utils;

import com.dukascopy.api.system.IClient;

/**
 * DukascopyClientFactory is similar with Dukascopy's original ClientFactory, but this one can create multiple different
 * clients, while the original one can only create a singleton instance of client.
 */
public class DukascopyClientFactory {

  /**
   * Returns default instance of Dukascopy IClient. Instance is created only once, each call will return the same instance
   *
   * @return instance of IClient
   * @throws ClassNotFoundException if jar file with implementation was not found
   * @throws IllegalAccessException if there is some security problems
   * @throws InstantiationException if it's not possible to instantiate new instance of Dukascopy IClient
   */
  public static IClient createNewInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    synchronized (DukascopyClientFactory.class) {
        Class dcClientImpl = Thread.currentThread().getContextClassLoader().loadClass("com.dukascopy.api.impl.connect.DCClientImpl");
        return (IClient) dcClientImpl.newInstance();

    }
  }
}
