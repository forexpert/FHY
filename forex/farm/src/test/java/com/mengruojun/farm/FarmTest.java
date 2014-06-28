package com.mengruojun.farm;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

import org.joda.time.*;
import org.joda.time.format.*;
import static junit.framework.Assert.assertNull;

public class FarmTest{
  Double a;
  Logger logger = Logger.getLogger(this.getClass());
  @Test
  public void MapTest(){
    Map a = new HashMap();
    a.put(1,"1");
    a.put(2,"2");
    a.put(3,"3");
    a.put(4,"4");

    for (Iterator itr = a.entrySet().iterator(); itr.hasNext();) {
      Map.Entry entry = (Map.Entry) itr.next();

      logger.info(entry.getValue());
    }
  }

  @Test
  public void DoubleTest(){
    logger.info(a);
    assertNull(a);
  }

  @Test
  public void assertValueTest(){
    DecimalFormat df = new DecimalFormat("####.00W");
    double currentBalance = 384393;
    double annualSalary1 = 14285*13;
    double annualSalary1_increaseRate = 0.05;
    double annualSalary2 = 6644*13;
    double annualSalary2_increaseRate = 0.05;
    double annualExpend = 80000;
    double annualExpend_increaseRate = 0.05;

    double assertValueIncreaseRate = 0.11;

    int totalYear = 25;
    logger.info("init Balance is " + df.format(currentBalance/10000.0));
    for (int i=0; i<totalYear; i++){
      currentBalance = currentBalance*(1+assertValueIncreaseRate) +
              (annualSalary1*(1+annualSalary1_increaseRate) + annualSalary2*(1+annualSalary2_increaseRate)
              - annualExpend*(1+annualExpend_increaseRate))*(1+assertValueIncreaseRate/2.0);

      logger.info("At the end of year " + (i+1) + ", The balance is " +  df.format(currentBalance/10000.0));

    }

  }


  @Test
  public void assertValueTest2(){
    DecimalFormat df = new DecimalFormat("####.00w");
    double currentBalance = 50;
    double annualSalary = 0 ;
    double annualAssert_increaseRate = 0.50;
    int totalYear = 25;
    logger.info("init Balance is " + df.format(currentBalance));
    for (int i=0; i<totalYear; i++){
      currentBalance = currentBalance*(1+annualAssert_increaseRate) + annualSalary;
      logger.info("At the end of year " + (i+1) + ", The balance is " +  df.format(currentBalance));
    }

  }

  @Test
  public void DateTimeTest(){
    String text = "2013-03-31T22:00:00.000Z";
    //String text = "1899-12-31T23:00:00.000Z";
    DateTimeFormatter parser = ISODateTimeFormat.dateTime();
    DateTime dt = parser.parseDateTime(text);
    parser.withZone(DateTimeZone.forID("EET"));
    logger.info(parser.print(new Date().getTime()));

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    formatter.setTimeZone(TimeZone.getTimeZone("EET"));

    logger.info(formatter.format(dt.toDate()));
  }


  @Test
  public void DateTimeTest1() throws ParseException {
    //String text = "2013-03-31T22:00:00.000Z";
    String text = "1899-12-31T23:00:00.000Z";
    DateTimeFormatter parser = ISODateTimeFormat.dateTime();
    DateTime dt = parser.parseDateTime(text);

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSzzzz");
    //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    //String date = "1899-12-31T00:00:00.000 EET";
    String date = "2013-04-01T00:00:00.000 EET";

    DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    formatter2.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
    logger.info(formatter2.format(formatter.parse(date)));

  }

  @Test
  public void DateTimeTest2() throws ParseException {
    Date date = new Date(1386662400000L);
    DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    logger.info(formatter2.format(date));
    System.currentTimeMillis();
  }


  @Test
  public void DoubleTest1(){
    String a = "abc";
    String b= new String(a);
    logger.info(a==b);
    logger.info(a=="abc");
    logger.info(b=="abc");
    logger.info("abc".intern()==b);
    logger.info(a==b.intern());
    logger.info("ddd" == "ddd");
  }


  @Test
  public void xmlReadTest(){
    String a = "abc";
    String b= new String(a);
    logger.info(a==b);
    logger.info(a=="abc");
    logger.info(b=="abc");
    logger.info("abc".intern()==b);
    logger.info(a==b.intern());
    logger.info("ddd" == "ddd");
  }

  @Test
  public void testLocale(){
    Locale locale = Locale.CHINA;

    logger.info(locale.toString());
    logger.info(locale.getDisplayName());
    logger.info(locale.getDisplayLanguage());
  }



  @Test
  public void testOutput(){
    StringBuffer out = new StringBuffer("");
    String temp = "emp_location";
    for(int i=0;i<80;i++){
      out.append("'");
      out.append(temp);
      out.append(i);
      out.append("'");
      out.append(",");
    }
    logger.info(out.toString());

  }

  @Test
  public void testOutput1(){
    StringBuffer out = new StringBuffer("");
    String temp1 = "<urn:sfobject><urn:type>EmpJob</urn:type><urn:user_id>user9</urn:user_id><eventReasonCode>DTACHBU</eventReasonCode><urn:start_date>2014-03-12</urn:start_date>";
    String temp2 = "<urn:seq_number>";
    String temp3 = "</urn:seq_number>";
    String temp4 = "<urn:job_title>Engineer</urn:job_title>  <urn:departmentCode>Dept 1</urn:departmentCode>  <urn:divisionCode>Div 3</urn:divisionCode>  <urn:locationCode>TESTTT</urn:locationCode>  <urn:businessUnitCode>BU2</urn:businessUnitCode>  <urn:costCenterCode>CC 2</urn:costCenterCode>  <urn:legalEntityCode>ADOBEIND</urn:legalEntityCode>  </urn:sfobject>";

    for(int i=2;i<3;i++){
      out.append(temp1);
      out.append(temp2);
      out.append(i);
      out.append(temp3);
      out.append(temp4);
      out.append("\n\n");
    }
    logger.info(out.toString());

  }

  @Test
  public void testOutput2(){

      logger.info("12345.jpg".split("\\.")[0]);
  }





}
