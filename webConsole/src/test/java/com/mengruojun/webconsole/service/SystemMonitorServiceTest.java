package com.mengruojun.webconsole.service;
import com.mengruojun.forex.activemq.ActivemqServer;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * SystemMonitorServiceTest
 */

public class SystemMonitorServiceTest extends TestCase{

  Logger logger = Logger.getLogger(SystemMonitorService.class);

  SystemMonitorService sms = null;

  @Override
  public void setUp(){
    sms = new SystemMonitorService();
    sms.setJmsPort(61616);
  }


  @Test
  public void testActiveMQRunning() throws Exception{
    boolean isJMSServerRunning = sms.isJMSServerRunning();
    assertFalse(isJMSServerRunning);
    ActivemqServer as = new ActivemqServer();
    as.start();
    isJMSServerRunning = sms.isJMSServerRunning();
    assertTrue(isJMSServerRunning);
    as.stop();
    isJMSServerRunning = sms.isJMSServerRunning();
    assertFalse(isJMSServerRunning);
  }
}
