package com.mengruojun.strategycenter.component.marketdata;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.Currency;
import com.mengruojun.common.utils.HistoryDataKBarUtils;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.lang.model.type.PrimitiveType;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/19/12
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */

@ContextConfiguration(
        locations = {"classpath:/com/mengruojun/strategycenter/app-test.xml"})
public class EMATest extends AbstractTransactionalJUnit4SpringContextTests {

  private MInteger outBegIdx;
  private MInteger outNbElement;
  private RetCode retCode;
  private Core lib;
  private int lookback;

  @Autowired
  HistoryDataKBarDao historyDataKBarDao;

  static public double[] close = null;

  public EMATest() {
    setUp();
  }

  protected void setUp() {

    // Create the library (typically done only once).
    lib = new Core();
    outBegIdx = new MInteger();
    outNbElement = new MInteger();

    outBegIdx.value = -1;
    outNbElement.value = -1;
    retCode = RetCode.InternalError;
    lookback = -1;
  }

  //@Ignore
  @org.junit.Test
  /**
   * Already verified by using manually algorithm and Excel tool.
   * The EMA algorithm can be found on
   * http://en.wikipedia.org/wiki/Exponential_moving_average#Exponential_moving_average
   */
  public void test_EMA() throws Exception {

    List<HistoryDataKBar> list = historyDataKBarDao.getBarsByOpenTimeRange(
            new Instrument(Currency.EUR, Currency.USD), TimeWindowType.S10, 1267401690000L, 1267401690000L+100*10000L );

    /* double macd[] = new double[close.length];
double signal[] = new double[close.length];
double hist[] = new double[close.length];
lookback = lib.macdLookback(15, 26, 9);
retCode = lib.macd(0, close.length - 1, close, 15, 26, 9, outBegIdx, outNbElement, macd, signal, hist);*/
    double[] askCloseInput = HistoryDataKBarUtils.getPropertyArray(list, "getAskClose");

    close = askCloseInput;
    double ema5[] = new double[close.length];
    lookback = lib.emaLookback(5);
    retCode = lib.ema(0, close.length - 1, close, 5, outBegIdx, outNbElement, ema5);
    int ema5BegIdx = outBegIdx.value;

    double ema20[] = new double[close.length];
    lookback = lib.emaLookback(20);
    retCode = lib.ema(0, close.length - 1, close, 20, outBegIdx, outNbElement, ema20);
    int ema20BegIdx = outBegIdx.value;

    //CSVWriter csvWriter = new CSVWriter(new FileWriter("EMA"));
    List<String[]> allElements = new ArrayList<String[]>();
    allElements.add(new String[]{"AskClose", "EMA5", "EMA20"});
    for (int i = 0; i < askCloseInput.length; i++) {
      double askClose = askCloseInput[i];
      double ema5Value = -1;
      double ema20Value = -1;
      if (i >= ema5BegIdx) {
        ema5Value = ema5[i - ema5BegIdx];
      }
      if (i >= ema20BegIdx) {
        ema20Value = ema20[i - ema20BegIdx];
      }
      allElements.add(new String[]{Double.toString(askClose), Double.toString(ema5Value), Double.toString(ema20Value)});

    }
    for(String[] s: allElements){
      logger.info(s[0] + s[1] + s[2]);
    }

    //csvWriter.writeAll(allElements);
    //csvWriter.close();
  }


}
