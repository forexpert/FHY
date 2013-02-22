package com.mengruojun.common.dao;


import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;

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

  HistoryDataKBar getLatest10SBar(Instrument instrument);
}
