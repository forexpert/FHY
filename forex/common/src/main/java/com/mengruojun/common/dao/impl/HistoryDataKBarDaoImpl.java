package com.mengruojun.common.dao.impl;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import org.springframework.stereotype.Repository;

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


  @Override
  public HistoryDataKBar find(long openTime, Instrument instrument, TimeWindowType timeWindowType) {
    List bars =  getHibernateTemplate().find("from HistoryDataKBar where instrument.currency1 = ? and instrument.currency2 = ?" +
            "and openTime = ? and timeWindowType = ?", instrument.getCurrency1(), instrument.getCurrency2(), openTime, timeWindowType);
    if(bars.size()>0){
      return (HistoryDataKBar) bars.get(0);
    } else {
      return null;
    }
  }
}
