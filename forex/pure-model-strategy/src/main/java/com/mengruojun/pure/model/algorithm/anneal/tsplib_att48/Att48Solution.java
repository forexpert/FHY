package com.mengruojun.pure.model.algorithm.anneal.tsplib_att48;

import com.mengruojun.pure.model.algorithm.anneal.helper.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clyde on 2/24/14.
 */
public class Att48Solution implements Solution, Cloneable{
  private List<String> tourLine = new ArrayList<String>();

  public Att48Solution(List<String> tourLine) {
    this.tourLine = tourLine;
  }

  public List<String> getTourLine() {
    return tourLine;
  }

  public void setTourLine(List<String> tourLine) {
    this.tourLine = tourLine;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return  new Att48Solution((List<String>)((ArrayList<String>)tourLine).clone());
  }
}
