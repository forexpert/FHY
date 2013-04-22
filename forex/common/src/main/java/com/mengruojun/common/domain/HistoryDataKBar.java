package com.mengruojun.common.domain;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * HistoryDataKBar takes id as primary key and has an UniqueConstraint
 *
 */
@Entity
@Table(name = "HistoryDataKBar")
public class HistoryDataKBar{
  private static final long serialVersionUID = -1943659317753245831L;
  @Version
  @SuppressWarnings({"UnusedDeclaration"})
  private long version;



  /**
   * Like EUR/USD, USD/JPY and etc
   */
  @Embedded
  private Instrument instrument;

  @Enumerated(EnumType.STRING)
  private TimeWindowType timeWindowType;
  @Id
  private Long openTime;
  private Long closeTime;

  @OneToMany(mappedBy = "historyDataKBar", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN, org.hibernate.annotations.CascadeType.SAVE_UPDATE})
  private final Set<HistoryDataKBarAttribute> historyDataKBarAttribute = new HashSet<HistoryDataKBarAttribute>();

  @Embedded
  private OHLC ohlc;

  /**
   * Default constructor
   */
  public HistoryDataKBar() {

  }

  public HistoryDataKBar(Instrument instrument, TimeWindowType timeWindowType, Long openTime, Long closeTime, OHLC ohlc) {
    this.instrument = instrument;
    this.timeWindowType = timeWindowType;
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.ohlc = ohlc;
  }


  //getter and setter


  public Set<HistoryDataKBarAttribute> getHistoryDataKBarAttribute() {
    return historyDataKBarAttribute;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public TimeWindowType getTimeWindowType() {
    return timeWindowType;
  }

  public void setTimeWindowType(TimeWindowType timeWindowType) {
    this.timeWindowType = timeWindowType;
  }

  public Long getOpenTime() {
    return openTime;
  }

  public void setOpenTime(Long openTime) {
    this.openTime = openTime;
  }

  public Long getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(Long closeTime) {
    this.closeTime = closeTime;
  }

  public OHLC getOhlc() {
    return ohlc;
  }

  public void setOhlc(OHLC ohlc) {
    this.ohlc = ohlc;
  }


  /** helpful for displaying if an entity's update was successful */
  public long getVersion(){
    return version;
  }

    // override method

  @Override
  public String toString(){
    ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("openTime", this.openTime)
            .append("instrument", this.instrument)
            .append("OHLC", this.ohlc)
            .append("timeWindowType", this.timeWindowType);
    return sb.toString();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HistoryDataKBar)) {
      return false;
    }

    final HistoryDataKBar historyDataKBar = (HistoryDataKBar) o;

    return this.toString().equals(historyDataKBar.toString());
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}