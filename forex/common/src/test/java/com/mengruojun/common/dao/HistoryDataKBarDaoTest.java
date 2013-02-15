package com.mengruojun.common.dao;

import com.mengruojun.common.domain.HistoryDataKBar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HistoryDataKBarDaoTest extends BaseDaoTestCase {
    Log log = LogFactory.getLog(HistoryDataKBarDaoTest.class);
    @Autowired
    HistoryDataKBarDao historyDataKBarDao;
    @Autowired
    SessionFactory sessionFactory;

    @Before
    public void setUp() {
    }

    @Ignore
    @Test
    public void getUser() {
        HistoryDataKBar kdb = historyDataKBarDao.get(0L);
        assertNull(kdb);
    }
}
