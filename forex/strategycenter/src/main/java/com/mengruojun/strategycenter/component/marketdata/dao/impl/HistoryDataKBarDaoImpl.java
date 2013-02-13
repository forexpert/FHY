package com.mengruojun.strategycenter.component.marketdata.dao.impl;


import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.strategycenter.common.dao.GenericDaoHibernate;
import com.mengruojun.strategycenter.component.marketdata.dao.HistoryDataKBarDao;
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
}
