package com.mengruojun.farm;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.Assert.*;

public class FarmTest{
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

}
