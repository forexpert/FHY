package com.mengruojun.strategycenter.component.strategy.compound;

import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.List;

/**
 All
 */
public abstract class CompoundBaseStrategy extends BaseStrategy{



  @Override
  protected List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {

    return null;
  }
}
