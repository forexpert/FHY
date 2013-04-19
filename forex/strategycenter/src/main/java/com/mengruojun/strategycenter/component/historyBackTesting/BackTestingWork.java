package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.jms.domain.MarketDataMessage;
import com.mengruojun.strategycenter.component.marketdata.MarketDataManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * We read db data via jdbc style, here we implement the do Work function, in which , we do the following:
 * 1. construct rs data to HistoryDataKBar object
 * 2. Pass them to MarketDataManager
 *
 */
public class BackTestingWork implements HistoryDataKBarDao.ResultSetWork{

  Logger logger = Logger.getLogger(getClass());

  private DataHandler dataHandler = null;


  Map<Instrument, MarketDataMessage> mdmMap = null;
  Long lastBarOpenTime = null;
  @Override
  /**
   * rs data structure
   * | id   | version | closeTime     | currency1 | currency2 |
   *
   * askClose | askHigh | askLow | askOpen | askVolume |
   * bidClose | bidHigh | bidLow | bidOpen | bidVolume |
   *
   * openTime      | timeWindowType |
   */
  public void doWork(ResultSet rs) throws SQLException {
    if(mdmMap == null && lastBarOpenTime == null){
      mdmMap = new HashMap<Instrument, MarketDataMessage>();
      lastBarOpenTime = rs.getLong("openTime");
    }

    if(lastBarOpenTime != null && lastBarOpenTime != rs.getLong("openTime")) { // bars with new openTime will coming
      if(mdmMap !=null){
        logger.error("There are something wrong in the data flow", new Exception());
      }
      mdmMap = null;
      mdmMap = new HashMap<Instrument, MarketDataMessage>();
      lastBarOpenTime = rs.getLong("openTime");

    }
    MarketDataMessage mdm = new MarketDataMessage(rs.getLong("openTime"),
            rs.getDouble("askOpen"),rs.getDouble("askHigh"),rs.getDouble("askLow"),rs.getDouble("askClose"),
            rs.getDouble("bidOpen"),rs.getDouble("bidHigh"),rs.getDouble("bidLow"),rs.getDouble("bidClose"),
            rs.getDouble("askVolume"),rs.getDouble("bidVolume"), java.util.Currency.getInstance(rs.getString("currency1")),
            java.util.Currency.getInstance(rs.getString("currency2")), TimeWindowType.valueOf(rs.getString("timeWindowType"))
            );
    Instrument instrument = new Instrument(Currency.fromJDKCurrency(java.util.Currency.getInstance(rs.getString("currency1"))),
            Currency.fromJDKCurrency(java.util.Currency.getInstance(rs.getString("currency2"))));
    mdmMap.put(instrument, mdm);
    if(mdmMap.size() == MarketDataManager.interestInstrumentList.size()){
      dataHandler.handle(mdmMap);
      mdmMap = null;
    }


  }


  //======getter and setter=============
  public DataHandler getDataHandler() {
    return dataHandler;
  }

  public void setDataHandler(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }
}
