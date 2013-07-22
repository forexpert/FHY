package com.mengruojun.strategycenter.component.marketdata;

import com.mengruojun.common.domain.HistoryDataKBar;
import com.mengruojun.common.domain.enumerate.KBarAttributeKey;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.strategycenter.component.marketdata.Calculator.Calculator;
import com.mengruojun.strategycenter.component.marketdata.Calculator.EMACalculator;
import com.mengruojun.strategycenter.component.marketdata.Calculator.MACDCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Kbar's property calculator
 */
public class PropertyCal {
  public static Double cal(KBarAttributeKey key, TreeMap<Long, HistoryDataKBar> openTimeKBars) {
    //TradingUtils.assertStat(openTimeKBars.lastEntry() ==null || openTimeKBars.lastEntry().getValue()==null || openTimeKBars.lastEntry().getValue().getCloseTime().equals(key.getEndTime()));


    return calculatorMap.get(key.getType()).cal(openTimeKBars);
  }

  private static Map<KBarAttributeType, Calculator> calculatorMap = new HashMap<KBarAttributeType, Calculator>();

  static {
    initCalculatorMap();
  }

  private static void initCalculatorMap() {

    calculatorMap.put(KBarAttributeType.EMA_5, new EMACalculator(5));
    calculatorMap.put(KBarAttributeType.EMA_10, new EMACalculator(10));
    calculatorMap.put(KBarAttributeType.EMA_20, new EMACalculator(20));
    calculatorMap.put(KBarAttributeType.EMA_30, new EMACalculator(30));
    calculatorMap.put(KBarAttributeType.EMA_40, new EMACalculator(40));
    calculatorMap.put(KBarAttributeType.EMA_60, new EMACalculator(60));


    calculatorMap.put(KBarAttributeType.MACD_12_26_9_hist, new MACDCalculator(12,26,9, MACDCalculator.MACD_VALUE_TYPE.histogram));
    calculatorMap.put(KBarAttributeType.MACD_12_26_9_macd, new MACDCalculator(12,26,9, MACDCalculator.MACD_VALUE_TYPE.MACD));
    calculatorMap.put(KBarAttributeType.MACD_12_26_9_signal, new MACDCalculator(12,26,9, MACDCalculator.MACD_VALUE_TYPE.signial));


  }


}
