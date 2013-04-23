package com.mengruojun.common.dao;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.OHLC;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.utils.TradingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HistoryDataKBarDaoTest extends BaseDaoTestCase {
  Log log = LogFactory.getLog(HistoryDataKBarDaoTest.class);

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
  SimpleDateFormat sdf_gmt8 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    logger.info("HistoryDataKBarDaoTest started");
  }

  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  @Autowired
  SessionFactory sessionFactory;

  @Before
  public void setUp() {
  }

  @Test
  public void testMysqlTimeZone(){
    //show variables like 'time_zone';
    assertEquals("+00:00", historyDataKBarDao.getMysqlTimeZone());
  }

  @Test
  public void getBar() {
    assertFalse(historyDataKBarDao.find(0L,new Instrument(Currency.EUR, Currency.USD),TimeWindowType.S10) !=null);
  }

  @Test
  public void testTimeZone() throws ParseException {
    Date now = new Date();

    log.info("For GMT+8, now is :" + sdf_gmt8.format(now));
    log.info("For GMT+0, now is :" + sdf.format(now));

    log.info(sdf.format(new Date(1295447600000L)));
    log.info(sdf.parse("2010.03.01 04:00:00 +0000").getTime());
    log.info(sdf.parse("2010.03.01 03:00:00 +0000").getTime());
    log.info(sdf.parse("2011.01.05 04:26:40 +0000").getTime());
  }

    @Test
    public void convertLongToData() throws ParseException {
        log.info(sdf.format(new Date(1267401600000L)));
        log.info(sdf.format(new Date(1267401610000L)));
        log.info(sdf.format(new Date(1267401690000L)));
        log.info(sdf.format(new Date(1267408800000L)));
    }



  @Test
  public void save() throws ParseException {
    String startTime = "3000.01.01 00:00:00 +0000";
    String endTime = "3000.01.01 00:00:10 +0000";
    HistoryDataKBar bar = new HistoryDataKBar(new Instrument(Currency.EUR, Currency.USD), TimeWindowType.S10,
            sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime(), new OHLC(2d, 2d, 2d, 2d, 2d, 2d, 2d, 2d, 2d, 2d));
    bar = historyDataKBarDao.save(bar);

    HistoryDataKBar bar2 = historyDataKBarDao.find(bar.getOpenTime(),bar.getInstrument(),bar.getTimeWindowType());
    assertEquals(bar2.getOpenTime(), sdf.parse(startTime).getTime(), 0);

    HistoryDataKBar bar3 = historyDataKBarDao.find(sdf.parse(startTime).getTime(), new Instrument(Currency.EUR, Currency.USD), TimeWindowType.S10);
    assertEquals(bar3.getOpenTime(), sdf.parse(startTime).getTime(), 0);
    assertEquals(bar2, bar3);


     startTime = "3000.01.01 00:00:10 +0000";
     endTime = "3000.01.01 00:00:20 +0000";
    HistoryDataKBar bar_2 = new HistoryDataKBar(new Instrument(Currency.EUR, Currency.USD), TimeWindowType.S10,
            sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime(), new OHLC(2d, 2d, 2d, 2d, 2d, 2d, 2d, 2d, 2d, 2d));
    bar_2 = historyDataKBarDao.save(bar_2);

    HistoryDataKBar latestBar = historyDataKBarDao.getLatestBarForPeriod(new Instrument(Currency.EUR, Currency.USD), TimeWindowType.S10);
    assertEquals(bar_2, latestBar);
  }


  @Test
  public void testJDBCLoading() {
    HistoryDataKBarDao.ResultSetWork resultSetWork  = new HistoryDataKBarDao.ResultSetWork() {
      @Override
      public void doWork(ResultSet rs) throws SQLException {
        logger.info(rs.getLong("openTime"));
      }
    };
    historyDataKBarDao.readS10BarsByTimeRangeOrderByOpenTime(TradingUtils.getGlobalTradingStartTime(), TradingUtils.getGlobalTradingStartTime() + 1000*30,resultSetWork);
  }
}
