package com.mengruojun.forex.brokerclient.dukascopy;

import com.dukascopy.api.Instrument;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.enumerate.Currency;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DukascopyUtilsTest
 */
public class DukascopyUtilsTest{
    Logger logger = Logger.getLogger(this.getClass());
    @Test
    public void testInstrumentConversion(){
        com.mengruojun.common.domain.Instrument instrument = DukascopyUtils.fromDukascopyInstrument(Instrument.EURUSD);
        assertEquals(new com.mengruojun.common.domain.Instrument(Currency.EUR, Currency.USD), instrument);

        com.mengruojun.common.domain.Instrument instrument2 = DukascopyUtils.fromDukascopyInstrument(Instrument.USDJPY);
        assertEquals(new com.mengruojun.common.domain.Instrument(Currency.USD, Currency.JPY), instrument2);

        com.mengruojun.common.domain.Instrument instrument3 = DukascopyUtils.fromDukascopyInstrument(Instrument.NZDUSD);
        assertNotSame(new com.mengruojun.common.domain.Instrument(Currency.USD, Currency.NZD), instrument3);
    }
    @Ignore
    @Test
    public void testLogFile(){
        for(int i=0;i<10;i++)  {
        logger.info("This is Info");
        logger.debug("This is debug");
        logger.warn("This is warn");
        logger.error("This is error");
        }
    }
}
