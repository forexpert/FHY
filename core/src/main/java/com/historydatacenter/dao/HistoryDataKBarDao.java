package com.historydatacenter.dao;

import com.historydatacenter.model.HistoryDataKBar;
import com.mengruojun.dao.GenericDao;
import com.mengruojun.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * HistoryDataKBar Data Access Object (GenericDao) interface.
 *
 */
public interface HistoryDataKBarDao extends GenericDao<HistoryDataKBar, Long> {


    
}
