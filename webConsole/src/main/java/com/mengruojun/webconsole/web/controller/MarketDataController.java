package com.mengruojun.webconsole.web.controller;

import com.google.gson.Gson;
import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.strategycenter.component.historyBackTesting.HistoryBackTestingProcessor;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.component.strategy.simple.M5MomentumStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import com.mengruojun.webconsole.utils.JSONUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;
import java.util.TimeZone;

@Controller
public class MarketDataController  implements InitializingBean {
  Logger logger = Logger.getLogger(this.getClass());

  @Autowired
  HistoryBackTestingProcessor historyBackTestingProcessor;
  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  private void init(){
    initStrategy();
    initTestBrokerClient();
  }

  private void initStrategy() {
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy("M5MomentumStrategy_GBPUSD", new M5MomentumStrategy(Instrument.GBPUSD));
  }

  private void initTestBrokerClient(){
    BrokerClient testBrokerClient = new BrokerClient(BrokerType.MockBroker, "M5MomentumStrategy_GBPUSD", "M5MomentumStrategy_GBPUSD",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());


    historyBackTestingProcessor.clearClient();
    historyBackTestingProcessor.addClient(testBrokerClient);
  }

  public void addStrategy(){

  }
  public void addTestBrokerClient(){

  }

  private void startBackTesting(final String startTime, final String endTime){
    new Thread(){
      public void run(){
        try {
          historyBackTestingProcessor.oneTurnTest(sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
        } catch (ParseException e) {
          logger.error("", e);
        }
      }
    }.start();
  }


  @RequestMapping(value = "system-console/getBackTestingInfo", method = RequestMethod.GET)
  public String getBackTestingInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {
    JSONObject reply = new JSONObject();
    JSONArray clients = new JSONArray();
    for(BrokerClient bc : this.historyBackTestingProcessor.getClientList()){
      JSONObject bcJson = new JSONObject();
      bcJson.put("ClientId", bc.getClientId());
      bcJson.put("StrategyName", bc.getStrategyName());
      clients.put(bcJson);
    }


    JSONArray strategies = new JSONArray();
    Map<String, BaseStrategy> strategMap = this.historyBackTestingProcessor.getBackTestingStrategyManager().getStrategyMap();
    for(String bsKey : strategMap.keySet()){
      //JSONObject bsJson = new JSONObject();
      //bsJson.put("StrategyName", bsKey);
      strategies.put(bsKey);
    }

    reply.put("clients", clients);
    reply.put("strategies", strategies);

    JSONUtils.writeJSON(reply, request, response);
    return null;
  }
  @RequestMapping(value = "system-console/getTestBrokerClientPerformanceData", method = RequestMethod.GET)
  public String getTestBrokerClientPerformanceData(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {
    final Gson gson = new Gson();
    JSONObject reply = new JSONObject();
    String testBrokerClientId = request.getParameter("testBrokerClientId");
    for(BrokerClient bc : this.historyBackTestingProcessor.getClientList()){
      if(bc.getClientId().equals(testBrokerClientId)){
        outputJsonData(response, gson.toJson(bc.getPerformanceData()));
        break;
      }
    }
    return null;
  }


  @RequestMapping(value = "system-console/getBackTestingStatus", method = RequestMethod.GET)
  public String getBackTestingStatus(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {

    JSONObject reply = new JSONObject();
    reply.put("status", this.historyBackTestingProcessor.isInTesting());
    JSONUtils.writeJSON(reply, request, response);
    return null;
  }
  @RequestMapping(value = "system-console/startBackTesting", method = RequestMethod.POST)
  public String startBackTesting(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {
    //String startTime = "2011.03.21 00:00:00 +0000";
    String startTime = request.getParameter("from");

    //String endTime = "2013.03.21 00:00:10 +0000";
    String endTime = request.getParameter("to");
    startBackTesting( startTime,  endTime);
    JSONObject reply = new JSONObject();
    reply.put("StartBackTesting", true);
    JSONUtils.writeJSON(reply, request, response);
    return null;
  }






  /**
   * ******MarketDataInfo*****************
   */
  @RequestMapping(value = "system-console/MarketDataInfo", method = RequestMethod.GET)
  public String getMarketDataInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {
    String instrument = request.getParameter("instrument");
    TimeWindowType twt = TimeWindowType.valueOf(request.getParameter("timeWindowType"));
    Long startTime =sdf.parse(request.getParameter("startTime")).getTime();
    Long endTime = sdf.parse(request.getParameter("endTime")).getTime();
    JSONObject reply = new JSONObject();


    reply.put("chartData", reply);
    JSONUtils.writeJSON(reply, request, response);
    return null;
  }


  /**
   * Utility method responsible for writing JSON data to HttpServletResponse
   * @param response
   * @param output
   */
  protected void outputJsonData(HttpServletResponse response, String output) {
    PrintWriter writer = null;
    try {
      writer = response.getWriter();
      writer.write(output);
    } catch (IOException e) {
      logger.error("", e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    init();
  }
}
