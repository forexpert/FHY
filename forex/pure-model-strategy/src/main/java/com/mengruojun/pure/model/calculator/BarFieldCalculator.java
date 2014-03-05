package com.mengruojun.pure.model.calculator;

import com.mengruojun.pure.model.BarField;

/**
 * Created by clyde on 2/20/14.
 */
public class BarFieldCalculator {

  public static Object calculate(BarField barField, BarField[] metaData, Object[][] barData)
          throws IllegalArgumentException {
    if(barField.getType() != BarField.BarFieldType.Calculation) {
      throw new IllegalArgumentException("The passed in BarField is not Calculation Type");
    }

    switch (barField.getCalType()){
      case EMA:
        //cmeng todo
        break;
      case MACD:
        //cmeng todo
        break;
      default:
        throw new IllegalArgumentException("unknown cal type " + barField.getCalType());
    }

    return null;
  }

}
