package com.mengruojun.pure.model.algorithm.anneal;

import com.mengruojun.pure.model.algorithm.anneal.helper.AnnealModelHelper;
import com.mengruojun.pure.model.algorithm.anneal.helper.Solution;
import org.apache.log4j.Logger;


/**
 * http://wiki.mbalib.com/wiki/%E6%A8%A1%E6%8B%9F%E9%80%80%E7%81%AB%E7%AE%97%E6%B3%95
 */
public class AnnealModel {
  private static Logger logger = Logger.getLogger(AnnealModel.class);

  /** initial temperature */
  private double startT = 0.0;
  private double minT = 0.0;
  private double decreaseStep = 0.0;


  /** THe max times trying in one turn searching */
  private int maxTimesTryInOneTrun = 0;


  public AnnealModelHelper annealModelHelper;

  public Solution startSolution = null;

  public AnnealModel(double startT, double minT, double decreaseStep, int maxTimesTryInOneTrun, AnnealModelHelper annealModelHelper, Solution startSolution) {
    this.startT = startT;
    this.minT = minT;
    this.decreaseStep = decreaseStep;
    this.maxTimesTryInOneTrun = maxTimesTryInOneTrun;
    this.annealModelHelper = annealModelHelper;
    this.startSolution = startSolution;
  }

  public void start(){
    double T = startT;
    Solution S = startSolution;

    while(T>minT){
      for(int k = 0; k<=maxTimesTryInOneTrun; k++){
        Solution S_ = annealModelHelper.solutionGenerator(S);
        double deltaT = annealModelHelper.evaluation(S_) -annealModelHelper.evaluation(S);
        if(deltaT < 0){
          S = S_;
          logger.info("improved to:" + annealModelHelper.evaluation(S_));
        } else{
          if(Math.random() < Math.exp(-deltaT/T)) {
            S = S_;
            logger.info("worsened to:" + annealModelHelper.evaluation(S_));
          }
        }
      }


      T *= decreaseStep;
    }

  }
}
