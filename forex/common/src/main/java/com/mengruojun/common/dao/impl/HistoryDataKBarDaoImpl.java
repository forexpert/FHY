package com.mengruojun.common.dao.impl;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Table;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class interacts with Spring's HibernateTemplate to save/delete and
 * retrieve Role objects.
 *
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 */
@Repository
public class HistoryDataKBarDaoImpl extends GenericDaoHibernate<HistoryDataKBar, Long> implements HistoryDataKBarDao {

  /**
   * Constructor to create a Generics-based version using Role as the entity
   */
  public HistoryDataKBarDaoImpl() {
    super(HistoryDataKBar.class);
  }

  //private HibernateTemplate hibernateTemplate;
  private JdbcTemplate jdbcTemplate;

  /**
   * @param dataSource the jdbc data source
   */
  @Autowired
  public final void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
    if (jdbcTemplate == null || dataSource != ((JdbcAccessor) jdbcTemplate.getDataSource()).getDataSource()) {
      jdbcTemplate = new JdbcTemplate(dataSource);
    }
  }


  @Override
  public void readAll(final Work work){
    final String sql = "SELECT * FROM HistoryDataKBar ORDER BY openTime ASC";
    this.getHibernateTemplate().executeFind(new HibernateCallback() {
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        Map<Instrument, HistoryDataKBar> mdmMap;
        List<HistoryDataKBar> result = new ArrayList();
        session.doWork(work);
        return result;
      }
    });
  }

  @Override
  public HistoryDataKBar find(long openTime, Instrument instrument, TimeWindowType timeWindowType) {
    List bars = getHibernateTemplate().find("from HistoryDataKBar where instrument.currency1 = ? and instrument.currency2 = ?" +
            "and openTime = ? and timeWindowType = ?", instrument.getCurrency1(), instrument.getCurrency2(), openTime, timeWindowType);
    if (bars.size() > 0) {
      return (HistoryDataKBar) bars.get(0);
    } else {
      return null;
    }
  }

  @Override
  public HistoryDataKBar getLatestBarForPeriod(final Instrument instrument, final TimeWindowType timeWindowType) {
    final String hql = "from HistoryDataKBar where instrument.currency1 = :currency1 and instrument.currency2 = :currency2 " +
            "and timeWindowType = :timeWindowType order by openTime desc ";
    HistoryDataKBar bar = (HistoryDataKBar) getHibernateTemplate().execute(
            new HibernateCallback() {
              public Object doInHibernate(Session session)
                      throws HibernateException, SQLException {
                Query query = session.createQuery(hql);
                query.setParameter("currency1", instrument.getCurrency1());
                query.setParameter("currency2", instrument.getCurrency2());
                query.setParameter("timeWindowType", timeWindowType);
                query.setFirstResult(0);
                query.setMaxResults(1);
                return query.uniqueResult();
              }
            });
    return bar;
  }

  /**
   * getBarsByOpenTimeRange
   * The openTimes of return bars are all more than or equal to openTimeFrom, but less than openTimeTo
   * @param instrument instrument
   * @param timeWindowType timeWindowType
   * @param openTimeFrom openTimeFrom
   * @param openTimeTo openTimeTo
   * @return a list of HistoryDataKBar
   */
  @Override
  public List<HistoryDataKBar> getBarsByOpenTimeRange(final Instrument instrument, final TimeWindowType timeWindowType, final long openTimeFrom, final long openTimeTo){
    List bars = getHibernateTemplate().find("from HistoryDataKBar where instrument.currency1 = ? and instrument.currency2 = ?" +
            "and openTime >= ? and openTime < ? and timeWindowType = ? order by openTime",
            instrument.getCurrency1(), instrument.getCurrency2(), openTimeFrom, openTimeTo, timeWindowType);
    return bars;
  }


  @Override
  /**
   * use jdbc mode to save bars in a batch. if get any exception, than, use hibernate saving one row by one row;
   */
  public void batchSave(final List<HistoryDataKBar> bars) {
    //id      | version | closeTime     | currency1 | currency2 | askClose | askHigh | askLow  | askOpen | askVolume | bidClose | bidHigh | bidLow  | bidOpen | bidVolume | openTime      | timeWindowType
    if (bars != null && bars.size() > 0) {
      String sql = "INSERT INTO HistoryDataKBar " +
              "( version,closeTime,currency1,currency2,askClose,askHigh,askLow,askOpen,askVolume,bidClose,bidHigh,bidLow,bidOpen,bidVolume,openTime,timeWindowType)" +
              "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          HistoryDataKBar historyDataKBar = bars.get(i);
          ps.setInt(1, 0);
          ps.setLong(2, historyDataKBar.getCloseTime());

          ps.setString(3, historyDataKBar.getInstrument().getCurrency1().toString());
          ps.setString(4, historyDataKBar.getInstrument().getCurrency2().toString());

          ps.setDouble(5,historyDataKBar.getOhlc().getAskClose());
          ps.setDouble(6,historyDataKBar.getOhlc().getAskHigh());
          ps.setDouble(7,historyDataKBar.getOhlc().getAskLow());
          ps.setDouble(8,historyDataKBar.getOhlc().getAskOpen());
          ps.setDouble(9,historyDataKBar.getOhlc().getAskVolume());

          ps.setDouble(10,historyDataKBar.getOhlc().getBidClose());
          ps.setDouble(11,historyDataKBar.getOhlc().getBidHigh());
          ps.setDouble(12,historyDataKBar.getOhlc().getBidLow());
          ps.setDouble(13,historyDataKBar.getOhlc().getBidOpen());
          ps.setDouble(14,historyDataKBar.getOhlc().getBidVolume());

          ps.setLong(15, historyDataKBar.getOpenTime());
          ps.setString(16, historyDataKBar.getTimeWindowType().toString());
        }

        @Override
        public int getBatchSize() {
          return bars.size();
        }
      });

    }

  }

  @Override
  public String getMysqlTimeZone(){

    final StringBuffer timezone = new StringBuffer();
    jdbcTemplate.query("show variables LIKE 'time_zone'",
            new Object[] {},
            new RowCallbackHandler() {
              public void processRow(ResultSet rs) throws SQLException {
                timezone.append(rs.getString(2)) ;
              }
            });

    return timezone.toString();


  }
}
