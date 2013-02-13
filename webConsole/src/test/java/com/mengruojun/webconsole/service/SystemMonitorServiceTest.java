package com.mengruojun.webconsole.service;

import com.mengruojun.forex.activemq.ActivemqServer;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import static junit.framework.Assert.*;


/**
 * SystemMonitorServiceTest
 */

public class SystemMonitorServiceTest {

    Logger logger = Logger.getLogger(SystemMonitorService.class);

    SystemMonitorService sms = null;

    @Before
    public void setUp() {
        sms = new SystemMonitorService();
        sms.setJmsPort(61616);
    }

    @Ignore
    @Test
    public void testActiveMQRunning() throws Exception {
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

    @Test
    public void testCPUAndMem() throws Exception {
        sms.getCPUUsage();
        sms.getMemUsage();
    }
}
