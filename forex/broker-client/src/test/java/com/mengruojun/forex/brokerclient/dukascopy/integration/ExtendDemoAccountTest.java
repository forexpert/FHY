package com.mengruojun.forex.brokerclient.dukascopy.integration;

import com.dukascopy.api.Period;
import com.mengruojun.brokerclient.dukascopy.DukascopyTradeClient;
import com.mengruojun.brokerclient.dukascopy.utils.account.ExtendDemoAccountStrategy;
import com.mengruojun.common.domain.HistoryDataKBar;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import org.junit.Ignore;

/**
 * We use Dukascopy client to feed the history data. Usually we feed 10s period data, then computing all kinds of period bars,
 * finally we compute all indicators for each bars on each period type.
 *
 *
 * In this test, it will use Dukascopy client to feed history data for all period type data[Done by manually],  also feed most kind of indicators.
 * Then compare them with what we computing.
 */

@ContextConfiguration(
        locations = {"classpath:/com/mengruojun/brokerclient/app.xml"})
public class ExtendDemoAccountTest extends AbstractTransactionalJUnit4SpringContextTests {
  Logger logger = Logger.getLogger(this.getClass());

  @Autowired
  DukascopyTradeClient dukascopyAutoExpandedDemoClient1;

  @Before
  public void setUp() {
  }
  @Test
  public void testExtendDemoAccount() throws InterruptedException {
    ((ExtendDemoAccountStrategy)dukascopyAutoExpandedDemoClient1.getStrategy()).setTest(true);
    dukascopyAutoExpandedDemoClient1.start();

    long maxWaitTime = 60 * 1000L;
    long onceWaitTime = 5* 1000L;
    while(!(((ExtendDemoAccountStrategy)dukascopyAutoExpandedDemoClient1.getStrategy()).isDoneFirstTrade())
            ) {
      Thread.sleep(onceWaitTime);
      maxWaitTime -= onceWaitTime;
      if(maxWaitTime<=0){
        break;
      }
    }

    dukascopyAutoExpandedDemoClient1.getClient().disconnect();
    assertTrue((((ExtendDemoAccountStrategy)dukascopyAutoExpandedDemoClient1.getStrategy()).isDoneFirstTrade()));

  }
}
