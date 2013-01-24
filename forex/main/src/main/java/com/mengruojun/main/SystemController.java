package com.mengruojun.main;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 1/22/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemController {
  Logger log = Logger.getLogger(this.getClass());

  public void startAll(){
    log.info("All processors are started!");
  }
}
