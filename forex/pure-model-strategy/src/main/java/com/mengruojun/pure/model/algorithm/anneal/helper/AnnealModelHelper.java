package com.mengruojun.pure.model.algorithm.anneal.helper;

/**
 * Created by clyde on 2/24/14.
 */
public interface AnnealModelHelper<S extends Solution> {

  /**
   * The evaluation function.
   *
   * Return a less double number for a better Solution
   *
   * @param s
   * @return
   */
  double evaluation(S s);

  /**
   * Generate a new Solution according to giving s.
   * Usually the generated solution is random and related to s.
   * It's similar with s and just has a little diference from s
   * @param s
   * @return
   */
  S solutionGenerator(S s);

}
