package com.mengruojun.strategycenter.component.strategy.compound;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.strategycenter.domain.BrokerClient;

import java.util.Map;

/**
 *  Factor will only focus on a certain giving instruments.
 *  It is not responsible for multiple instruments trading coordination.
 */
public interface Factor {
  enum SLClosePolicyType{
    SettingSL,
    SettingMovingSL,

  }

  //Open policy

  /**
   * Which direction does a factor think in the future.
   * It imply which direction the factor wants to open a position if it actually wants to open.
   *
   * return Long or Short or null;
   * null means the factor doesn't prefer any direction
   * @param bc BrokerClient
   * @param currentTime currentTime
   * @return Direction
   */
  Direction determineDirection(BrokerClient bc, long currentTime);

  /**
   * For each interesting instruments, determine how much amount the factor think we should trade.
   * If the amount value is >0, it means the amount for opening new position;
   * If the amount value is <=0; it means do nothing for opening or closing positions;
   *
   * @param bc BrokerClient
   * @param currentTime currentTime
   * @return Direction
   */
  Double determineOpenAmount(BrokerClient bc, long currentTime);


  //Close policy
  //todo cmeng tobe consider and design
  //ClosePolicyType getClosePolicyType();

  /**
   * For each interesting instruments, determine how much amount the factor think we should trade.
   * If the amount value is >0, it means the amount for closing new position;
   * If the amount value is <=0; it means close
   *
   * @param bc BrokerClient
   * @param currentTime currentTime
   * @return Direction
   */
  Double determineCloseAmount(BrokerClient bc, long currentTime);



}
