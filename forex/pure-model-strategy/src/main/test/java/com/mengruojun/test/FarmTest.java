package com.mengruojun.test;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by clyde on 2/24/14.
 */
public class FarmTest {
  Logger logger = Logger.getLogger(FarmTest.class);

  @Test
  public void testFloorCeil(){
    System.out.println("==============Math.floor()==============");
    System.out.println("Math.floor(99.1) = " + Math.floor(99.1));
    System.out.println("Math.floor(-99.1) = " + Math.floor(-99.1));
    System.out.println("Math.floor(99.9) = " + Math.floor(99.9));
    System.out.println("Math.floor(-99.9) = " + Math.floor(-99.9));

    System.out.println("\n\n==============Math.ceil()==============");
    System.out.println("Math.ceil(99.1) = " + Math.ceil(99.1));
    System.out.println("Math.ceil(-99.1) = " + Math.ceil(-99.1));
    System.out.println("Math.ceil(99.9) = " + Math.ceil(99.9));
    System.out.println("Math.ceil(-99.9) = " + Math.ceil(-99.9));
  }
  @Test
  public void testRondom(){
    logger.info(Math.random());
    logger.info(Math.random());
    logger.info(Math.random());
    logger.info(Math.random());
    logger.info(Math.random());
    logger.info(Math.random());
  }
}
