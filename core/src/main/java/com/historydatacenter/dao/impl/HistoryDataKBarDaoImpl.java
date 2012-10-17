package com.historydatacenter.dao.impl;

import com.historydatacenter.dao.HistoryDataKBarDao;
import com.historydatacenter.model.HistoryDataKBar;
import com.mengruojun.dao.RoleDao;
import com.mengruojun.dao.hibernate.GenericDaoHibernate;
import com.mengruojun.model.Role;
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
