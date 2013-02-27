package com.mengruojun.common.dao;


import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;

import java.util.List;

/**
 * HistoryDataKBar Data Access Object (GenericDao) interface.
 *
 */
public interface HistoryDataKBarDao extends GenericDao<HistoryDataKBar, Long> {


  /**
   * find the specified HistoryDataKBar and return null if not found;
   * @param startTime
   * @param instrument
   * @param timeWindowType
   * @return
   */
  HistoryDataKBar find(long startTime, Instrument instrument, TimeWindowType timeWindowType);

  HistoryDataKBar getLatestBarForPeriod(Instrument instrument, TimeWindowType timeWindowType);

  /**
   * use jdbc mode to save bars in a batch. if get any exception, than, use hibernate saving one row by one row;
   * @param bars
   */
  void batchSave(List<HistoryDataKBar> bars);
}
