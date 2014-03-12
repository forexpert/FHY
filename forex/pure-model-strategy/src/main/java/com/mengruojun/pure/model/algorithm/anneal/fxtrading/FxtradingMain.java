package com.mengruojun.pure.model.algorithm.anneal.fxtrading;

import com.mengruojun.pure.model.algorithm.anneal.AnnealModel;
import com.mengruojun.pure.model.algorithm.anneal.helper.Solution;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clyde on 3/6/14.
 */
public class FxtradingMain {

  static Logger logger = Logger.getLogger(FxtradingMain.class);

  public static void main(String[] args){
    new FxtradingMain().test1();
  }

  public void test1(){
    FxtradingStrategySolutionHelper helper = new FxtradingStrategySolutionHelper();
    List<String> tourLine = new ArrayList<String>();
    for(int i=0; i<48;i++){
      tourLine.add("node" + (i+1));
    }
    Solution startSolution = generateStartSolution();
    new AnnealModel(1000,0.001,0.9, 1000, helper, startSolution).start();

  }

  private Solution generateStartSolution(){
    return null;
  }
}
