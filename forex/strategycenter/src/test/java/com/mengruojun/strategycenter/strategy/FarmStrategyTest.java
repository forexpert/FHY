package com.mengruojun.strategycenter.strategy;


import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

public class FarmStrategyTest {
  Logger logger = Logger.getLogger(this.getClass());
  @Test
  /**
   * Do trades daily. For each day, if getting profit, stop trading; else do trades for 10 times maximally
   *
   * The result is the final balance is still not profitable . Sometimes, it is 65, 100, 97, 128 and etc.
   */
  public void test1(){
    int balance = 100;
    for(int i = 0 ;i<2000;i++){
      for(int j=0; j<10; j++){
        int dailyPnl = 0;
        int pnl = doTrade();
        balance += pnl;
        dailyPnl +=pnl;
        if(dailyPnl ==1) break;
      }

      logger.info(balance);
    }


  }

  /**
   * return 1 or -1, means win 1 or loss 1
   * @return int
   */
  private int doTrade(){
    double a = Math.random();
    if(a > 0.5)return 1;
    if(a<0.5)return -1;
    return 0;
  }
}
