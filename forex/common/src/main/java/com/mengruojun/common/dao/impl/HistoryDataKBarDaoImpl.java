package com.mengruojun.common.dao.impl;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;


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
  private NamedParameterJdbcTemplate jdbcTemplate;

  /**
   * @param dataSource the jdbc data source
   */
  @Autowired
  public final void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
    if (jdbcTemplate == null || dataSource != ((JdbcAccessor) jdbcTemplate.getJdbcOperations()).getDataSource()) {
      jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }
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
}
