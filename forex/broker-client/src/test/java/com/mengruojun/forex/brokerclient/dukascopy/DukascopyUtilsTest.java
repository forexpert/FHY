package com.mengruojun.forex.brokerclient.dukascopy;

import com.dukascopy.api.Instrument;
import com.mengruojun.brokerclient.dukascopy.utils.DukascopyUtils;
import com.mengruojun.common.domain.enumerate.Currency;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * DukascopyUtilsTest
 */
public class DukascopyUtilsTest extends TestCase{
    @Test
    public void testInstrumentConversion(){
        com.mengruojun.common.domain.Instrument instrument = DukascopyUtils.fromDukascopyInstrument(Instrument.EURUSD);
        assertEquals(new com.mengruojun.common.domain.Instrument(Currency.EUR, Currency.USD), instrument);

        com.mengruojun.common.domain.Instrument instrument2 = DukascopyUtils.fromDukascopyInstrument(Instrument.USDJPY);
        assertEquals(new com.mengruojun.common.domain.Instrument(Currency.USD, Currency.JPY), instrument2);

        com.mengruojun.common.domain.Instrument instrument3 = DukascopyUtils.fromDukascopyInstrument(Instrument.NZDUSD);
        assertNotSame(new com.mengruojun.common.domain.Instrument(Currency.USD, Currency.NZD), instrument3);
    }
}
