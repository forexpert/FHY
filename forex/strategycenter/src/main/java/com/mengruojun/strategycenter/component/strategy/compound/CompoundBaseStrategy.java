package com.mengruojun.strategycenter.component.strategy.compound;

import com.mengruojun.jms.domain.TradeCommandMessage;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 4/17/13
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompoundBaseStrategy extends BaseStrategy{
  @Override
  protected List<TradeCommandMessage> OnAnalysis(BrokerClient bc, long currentTime) {

    return null;
  }
}
