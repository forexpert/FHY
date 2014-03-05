package com.mengruojun.pure.model.algorithm.anneal.tsplib_att48;

import com.mengruojun.pure.model.algorithm.anneal.AnnealModel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clyde on 2/24/14.
 */
public class Att48Main {
  static Logger logger = Logger.getLogger(Att48Main.class);

  public static void main(String[] args){
    Att48Main.test1();
  }

  public static void test1(){
    List<String> tourLine = new ArrayList<String>();
    for(int i=0; i<48;i++){
      tourLine.add("node" + (i+1));
    }
    Att48Solution startSolution = new Att48Solution(tourLine);
    new AnnealModel(1,0.00001,0.9, 1000, new Att48SolutionHelper(), startSolution).start();

  }


  public static void test2(){
    List<String> tourLine = new ArrayList<String>();
    tourLine.add("node1");
    tourLine.add("node8");
    tourLine.add("node38");
    tourLine.add("node31");
    tourLine.add("node44");
    tourLine.add("node18");
    tourLine.add("node7");
    tourLine.add("node28");
    tourLine.add("node6");
    tourLine.add("node37");
    tourLine.add("node19");
    tourLine.add("node27");
    tourLine.add("node17");
    tourLine.add("node43");
    tourLine.add("node30");
    tourLine.add("node36");
    tourLine.add("node46");
    tourLine.add("node33");
    tourLine.add("node20");
    tourLine.add("node47");
    tourLine.add("node21");
    tourLine.add("node32");
    tourLine.add("node39");
    tourLine.add("node48");
    tourLine.add("node5");
    tourLine.add("node42");
    tourLine.add("node24");
    tourLine.add("node10");
    tourLine.add("node45");
    tourLine.add("node35");
    tourLine.add("node4");
    tourLine.add("node26");
    tourLine.add("node2");
    tourLine.add("node29");
    tourLine.add("node34");
    tourLine.add("node41");
    tourLine.add("node16");
    tourLine.add("node22");
    tourLine.add("node3");
    tourLine.add("node23");
    tourLine.add("node14");
    tourLine.add("node25");
    tourLine.add("node13");
    tourLine.add("node11");
    tourLine.add("node12");
    tourLine.add("node15");
    tourLine.add("node40");
    tourLine.add("node9");
    logger.info(new Att48SolutionHelper().evaluation(new Att48Solution(tourLine)));

  }



}
