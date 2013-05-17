package com.mengruojun.strategycenter.domain;

import com.mengruojun.common.domain.Position;

import java.util.List;

/**
 Broker Client Performance Data
 */
public class PerformanceData {
  String testClientName;
  String strategyname;
  List<Position> closePositions;
  List<EquityRecord> equityRecords;

  ///===========setter and getter==================



  public String getTestClientName() {
    return testClientName;
  }

  public void setTestClientName(String testClientName) {
    this.testClientName = testClientName;
  }

  public String getStrategyname() {
    return strategyname;
  }

  public void setStrategyname(String strategyname) {
    this.strategyname = strategyname;
  }

  public List<Position> getClosePositions() {
    return closePositions;
  }

  public void setClosePositions(List<Position> closePositions) {
    this.closePositions = closePositions;
  }

  public List<EquityRecord> getEquityRecords() {
    return equityRecords;
  }

  public void setEquityRecords(List<EquityRecord> equityRecords) {
    this.equityRecords = equityRecords;
  }



  static class EquityRecord{
    private Long endTime;
    private Double equity;

    EquityRecord(Long endTime, Double equity) {
      this.endTime = endTime;
      this.equity = equity;
    }

    public Long getEndTime() {
      return endTime;
    }

    public void setEndTime(Long endTime) {
      this.endTime = endTime;
    }

    public Double getEquity() {
      return equity;
    }

    public void setEquity(Double equity) {
      this.equity = equity;
    }
  }

}
