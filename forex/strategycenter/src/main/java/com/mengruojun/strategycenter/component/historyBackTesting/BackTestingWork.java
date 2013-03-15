package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.dao.HistoryDataKBarDao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * We read db data via jdbc style, here we implement the do Work function, in which , we do the following:
 * 1. construct rs data to HistoryDataKBar object
 * 2. Pass them to MarketDataManager
 *
 */
public class BackTestingWork implements HistoryDataKBarDao.ResultSetWork{
  @Override
  public void doWork(ResultSet rs) throws SQLException {

  }
}
