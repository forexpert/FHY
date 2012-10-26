package com.historydatacenter.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 10/16/12
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class HistoryDataKBarAttribute extends PersistentAttribute<HistoryDataKBar>{
  private static final long serialVersionUID = 9062633143778097851L;

  @ManyToOne(optional=false)
  private HistoryDataKBar historyDataKBar;

  @Override
  public void setOwner(HistoryDataKBar owner) {
    historyDataKBar =  owner;
  }

  /**
   * Default constructor
   */
  public HistoryDataKBarAttribute(){

  }

  public HistoryDataKBarAttribute(String name, String value)
  {
    super(name, value);
  }

  public HistoryDataKBar getHistoryDataKBar() {
    return historyDataKBar;
  }

}
