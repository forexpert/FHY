package com.mengruojun.strategycenter.component.historyBackTesting;

import com.mengruojun.common.domain.Instrument;
import com.mengruojun.jms.domain.MarketDataMessage;

import java.util.Map;

/**
 * The DataHandler will handle mdmMap.
 * Usually, it is used in back testing task. The logic should like what ClientManager does for mdmMap
 */
public interface DataHandler {

  void handle(Map<Instrument, MarketDataMessage> mdmMap);

}
