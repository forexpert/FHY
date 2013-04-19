package com.mengruojun.forex.brokerclient.dukascopy.integration.historydataasyncfeed;

import com.dukascopy.api.Period;
import com.mengruojun.brokerclient.dukascopy.DukascopyTradeClient;
import com.mengruojun.common.domain.HistoryDataKBar;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.List;

/**
 * We use Dukascopy client to feed the history data. Usually we feed 10s period data, then computing all kinds of period bars,
 * finally we compute all indicators for each bars on each period type.
 *
 *
 * In this test, it will use Dukascopy client to feed history data for all period type data[Done by manually],  also feed most kind of indicators.
 * Then compare them with what we computing.
 */

@ContextConfiguration(
        locations = {"classpath:/app-test.xml"})
public class HistoryDataAsyncFeedTest extends AbstractTransactionalJUnit4SpringContextTests {
  Logger logger = Logger.getLogger(this.getClass());

  @Autowired
  DukascopyTradeClient dukascopyHistoryMarketDataFeedClient;
  @Autowired
  HistoryMarketDataAsyncFeedTestStrategy historyMarketDataAsyncFeedTestStrategy;
  final List<HistoryDataKBar> kBarsFromDukascopyServer = new ArrayList<HistoryDataKBar>();
  @Before
  public void setUp() {
  }
  @Ignore
  @Test
  public void testGetHistoryBarsFromDukascopyServer() throws InterruptedException {

    String start="2011.01.09 00:00:00 +0000";
    String end="2011.01.10 01:00:00 +0000";
    historyMarketDataAsyncFeedTestStrategy.setServerBars(kBarsFromDukascopyServer);
    historyMarketDataAsyncFeedTestStrategy.setTestBar_start(start);
    historyMarketDataAsyncFeedTestStrategy.setTestBar_end(end);
    historyMarketDataAsyncFeedTestStrategy.setTestPeriod(Period.TEN_SECS);

    dukascopyHistoryMarketDataFeedClient.setStrategy(historyMarketDataAsyncFeedTestStrategy);
    new Thread(){
      public void run(){
        dukascopyHistoryMarketDataFeedClient.start();
      }
    }.start();

    synchronized (kBarsFromDukascopyServer){
      kBarsFromDukascopyServer.wait();
      logger.info("verify the data in kBarsFromDukascopyServer");
      logger.info("kBarsFromDukascopyServer size is " + kBarsFromDukascopyServer.size());

    }

  }
}
