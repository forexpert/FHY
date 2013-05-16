package com.mengruojun.common.domain.enumerate;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.TimeWindowType;

/**
 * HistoryData Kbar Attribute class, is similar to an emun. but allowed to insert by creating instance.
 */
public class KBarAttributeKey implements Comparable<KBarAttributeKey> {

  private KBarAttributeType type;
  private Instrument instrument;
  private TimeWindowType twt;
  private Long endTime;

  public KBarAttributeKey(KBarAttributeType type, Instrument instrument, TimeWindowType twt, Long endTime) {
    this.type = type;
    this.instrument = instrument;
    this.twt = twt;
    this.endTime = endTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KBarAttributeKey)) return false;

    KBarAttributeKey that = (KBarAttributeKey) o;

    if (!endTime.equals(that.endTime)) return false;
    if (!instrument.equals(that.instrument)) return false;
    if (twt != that.twt) return false;
    if (type != that.type) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + instrument.hashCode();
    result = 31 * result + twt.hashCode();
    result = 31 * result + endTime.hashCode();
    return result;
  }

  // getter and setter
  public KBarAttributeType getType() {
    return type;
  }

  public void setType(KBarAttributeType type) {
    this.type = type;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public TimeWindowType getTwt() {
    return twt;
  }

  public void setTwt(TimeWindowType twt) {
    this.twt = twt;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @Override
  public int compareTo(KBarAttributeKey o) {
    if(this.equals(o))return 0;
    if (this.endTime < o.getEndTime()) {
      return -1;
    } else if (this.endTime > o.getEndTime()) {
      return 1;
    } else{ // endTimes equal to each other
      if(this.getInstrument().compareTo(o.getInstrument()) != 0) {
        return this.getInstrument().compareTo(o.getInstrument());
      } else{  // instruments are identical
        if(this.getTwt().compareTo(o.getTwt())!=0){
          return this.getTwt().compareTo(o.getTwt());
        } else {  // Time Window Type are identical
          if(this.getType().compareTo(o.getType()) !=0){
            return this.getType().compareTo(o.getType());
          } else{ //property type are the same
            return 0; // all attributes are the same , to the two object are the same
          }
        }
      }
    }
  }
}
