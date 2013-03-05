package com.mengruojun.common;

import com.mengruojun.common.domain.TimeWindowType;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 3/5/13
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class TimeWindowTypeTest {

  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

  {
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  @Test
  public void getLastAvailableEndTimeTest() throws ParseException {


    testD1();
    testH4();
    testH1();
    testM30();
    testM10();
    testM5();
    testM1();
    testS30();
    testS20();
    testS10();
  }

  private void testS10()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:41";
    String endTime_4 = "2003.03.01 00:46:21";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S10, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S10, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S10, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S10, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:10:10" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:59:50" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:23:40" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:46:20" , sdf.format(new Date(good_endTime_4)));

  }

  private void testS20()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:41";
    String endTime_4 = "2003.03.01 00:46:21";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S20, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S20, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S20, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S20, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:10:00" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:59:40" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:23:40" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:46:20" , sdf.format(new Date(good_endTime_4)));

  }

  private void testS30()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:41";
    String endTime_4 = "2003.03.01 00:46:21";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S30, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S30, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S30, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.S30, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:10:00" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:59:30" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:23:30" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:46:00" , sdf.format(new Date(good_endTime_4)));

  }


  private void testM1()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:41";
    String endTime_4 = "2003.03.01 00:46:21";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M1, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M1, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M1, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M1, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:10:00" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:59:00" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:23:00" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:46:00" , sdf.format(new Date(good_endTime_4)));

  }

  private void testM5()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:41";
    String endTime_4 = "2003.03.01 00:46:21";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M5, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M5, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M5, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M5, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:10:00" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:55:00" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:20:00" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:45:00" , sdf.format(new Date(good_endTime_4)));

  }

  private void testM10()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:01";
    String endTime_4 = "2003.03.01 00:40:01";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M10, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M10, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M10, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M10, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:10:00" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:50:00" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:20:00" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:40:00" , sdf.format(new Date(good_endTime_4)));

  }

  private void testM30()  throws ParseException {
    String endTime_1 = "1989.03.01 08:10:10";
    String endTime_2 = "2003.02.28 23:59:59";
    String endTime_3 = "2003.03.01 15:23:01";
    String endTime_4 = "2003.03.01 00:40:01";

    Long good_endTime_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M30, sdf.parse(endTime_1).getTime());
    Long good_endTime_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M30, sdf.parse(endTime_2).getTime());
    Long good_endTime_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M30, sdf.parse(endTime_3).getTime());
    Long good_endTime_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.M30, sdf.parse(endTime_4).getTime());

    assertEquals("1989.03.01 08:00:00" , sdf.format(new Date(good_endTime_1)));
    assertEquals("2003.02.28 23:30:00" , sdf.format(new Date(good_endTime_2)));
    assertEquals("2003.03.01 15:00:00" , sdf.format(new Date(good_endTime_3)));
    assertEquals("2003.03.01 00:30:00" , sdf.format(new Date(good_endTime_4)));

  }

  private void testH1()  throws ParseException {
    String endTime_h1_1 = "1989.03.01 08:10:10";
    String endTime_h1_2 = "2003.02.28 23:59:59";
    String endTime_h1_3 = "2003.03.01 15:23:01";
    String endTime_h1_4 = "2003.03.01 00:00:01";

    Long good_endTime_h1_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H1, sdf.parse(endTime_h1_1).getTime());
    Long good_endTime_h1_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H1, sdf.parse(endTime_h1_2).getTime());
    Long good_endTime_h1_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H1, sdf.parse(endTime_h1_3).getTime());
    Long good_endTime_h1_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H1, sdf.parse(endTime_h1_4).getTime());

    assertEquals("1989.03.01 08:00:00" , sdf.format(new Date(good_endTime_h1_1)));
    assertEquals("2003.02.28 23:00:00" , sdf.format(new Date(good_endTime_h1_2)));
    assertEquals("2003.03.01 15:00:00" , sdf.format(new Date(good_endTime_h1_3)));
    assertEquals("2003.03.01 00:00:00" , sdf.format(new Date(good_endTime_h1_4)));

  }


  private void testH4()  throws ParseException {

    //Test H4;
    String endTime_h4_1 = "1989.03.01 08:10:10";
    String endTime_h4_2 = "2003.02.28 23:59:59";
    String endTime_h4_3 = "2003.03.01 15:23:01";
    String endTime_h4_4 = "2003.03.01 00:00:01";

    Long good_endTime_h4_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H4, sdf.parse(endTime_h4_1).getTime());
    Long good_endTime_h4_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H4, sdf.parse(endTime_h4_2).getTime());
    Long good_endTime_h4_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H4, sdf.parse(endTime_h4_3).getTime());
    Long good_endTime_h4_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.H4, sdf.parse(endTime_h4_4).getTime());

    assertEquals("1989.03.01 08:00:00" , sdf.format(new Date(good_endTime_h4_1)));
    assertEquals("2003.02.28 20:00:00" , sdf.format(new Date(good_endTime_h4_2)));
    assertEquals("2003.03.01 12:00:00" , sdf.format(new Date(good_endTime_h4_3)));
    assertEquals("2003.03.01 00:00:00" , sdf.format(new Date(good_endTime_h4_4)));


  }

  private void testD1()  throws ParseException {
    //Test D1
    String endTime_d1_1 = "1989.03.01 00:00:00";
    String endTime_d1_2 = "2003.02.28 23:59:59";
    String endTime_d1_3 = "2003.03.01 00:00:00";
    String endTime_d1_4 = "2003.03.01 00:00:01";


    Long good_endTime_d1_1 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.D1, sdf.parse(endTime_d1_1).getTime());
    assertEquals(sdf.format(new Date(good_endTime_d1_1)),endTime_d1_1);

    Long good_endTime_d1_2 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.D1, sdf.parse(endTime_d1_2).getTime());
    assertEquals(sdf.format(new Date(good_endTime_d1_2)),"2003.02.28 00:00:00");

    Long good_endTime_d1_3 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.D1, sdf.parse(endTime_d1_3).getTime());
    assertEquals(sdf.format(new Date(good_endTime_d1_3)),endTime_d1_3);

    Long good_endTime_d1_4 = TimeWindowType.getLastAvailableEndTime(TimeWindowType.D1, sdf.parse(endTime_d1_4).getTime());
    assertEquals(sdf.format(new Date(good_endTime_d1_4)),endTime_d1_3);
  }
}
