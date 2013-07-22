package com.mengruojun.webconsole.web.controller;

import com.google.gson.Gson;
import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.common.domain.Position;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.domain.enumerate.BrokerType;
import com.mengruojun.common.domain.enumerate.Direction;
import com.mengruojun.common.domain.enumerate.KBarAttributeType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.strategycenter.component.historyBackTesting.HistoryBackTestingProcessor;
import com.mengruojun.strategycenter.component.strategy.BaseStrategy;
import com.mengruojun.strategycenter.component.strategy.simple.AntiM5MomentumStrategy;
import com.mengruojun.strategycenter.component.strategy.simple.M5MomentumStrategy;
import com.mengruojun.strategycenter.component.strategy.simple.MAStrategy;
import com.mengruojun.strategycenter.domain.BrokerClient;
import com.mengruojun.strategycenter.domain.PerformanceData;
import com.mengruojun.webconsole.utils.FEUtil;
import com.mengruojun.webconsole.utils.JSONUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MarketDataController implements InitializingBean {
  Logger logger = Logger.getLogger(this.getClass());
  DecimalFormat df1 = new DecimalFormat("####.00");
  @Autowired
  HistoryBackTestingProcessor historyBackTestingProcessor;
  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  private void init() {
    initStrategy();
    initTestBrokerClient();
  }

  private void initStrategy() {
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy("M5MomentumStrategy_GBPUSD", new M5MomentumStrategy(Instrument.EURUSD));
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy("AntiM5MomentumStrategy_GBPUSD", new AntiM5MomentumStrategy(Instrument.EURUSD));


    MAStrategy mas1 = new MAStrategy(Instrument.EURUSD, TimeWindowType.M5, KBarAttributeType.EMA_5, KBarAttributeType.EMA_20);
    MAStrategy mas2 = new MAStrategy(Instrument.EURUSD, TimeWindowType.M5, KBarAttributeType.EMA_5, KBarAttributeType.EMA_60);
    MAStrategy mas3 = new MAStrategy(Instrument.EURUSD, TimeWindowType.D1, KBarAttributeType.EMA_5, KBarAttributeType.EMA_20);
    MAStrategy mas4 = new MAStrategy(Instrument.EURUSD, TimeWindowType.D1, KBarAttributeType.EMA_5, KBarAttributeType.EMA_60);
    MAStrategy mas5 = new MAStrategy(Instrument.EURUSD, TimeWindowType.H4, KBarAttributeType.EMA_5, KBarAttributeType.EMA_20);
    MAStrategy mas6 = new MAStrategy(Instrument.EURUSD, TimeWindowType.H4, KBarAttributeType.EMA_5, KBarAttributeType.EMA_60);


    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy(mas1.toString(), mas1);
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy(mas2.toString(), mas2);
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy(mas3.toString(), mas3);
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy(mas4.toString(), mas4);
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy(mas5.toString(), mas5);
    this.historyBackTestingProcessor.getBackTestingStrategyManager().addStrategy(mas6.toString(), mas6);

  }

  private void initTestBrokerClient() {
 /*   BrokerClient testBrokerClient = new BrokerClient(BrokerType.MockBroker, "M5MomentumStrategy_GBPUSD", "M5MomentumStrategy_GBPUSD",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());

    BrokerClient testBrokerClient2 = new BrokerClient(BrokerType.MockBroker, "AntiM5MomentumStrategy_GBPUSD", "AntiM5MomentumStrategy_GBPUSD",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());

    BrokerClient testBrokerClient3 = new BrokerClient(BrokerType.MockBroker, "AntiM5MomentumStrategy_GBPUSD", "AntiM5MomentumStrategy_GBPUSD",
            200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
            new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());*/

    historyBackTestingProcessor.clearClient();

    for(String strategyName :this.historyBackTestingProcessor.getBackTestingStrategyManager().getStrategyMap().keySet()){
      BrokerClient tempBrokerClient = new BrokerClient(BrokerType.MockBroker, strategyName, strategyName,
              200.0, Currency.getInstance("USD"), 10000.0, 10000.0,
              new ArrayList<Position>(), new ArrayList<Position>(), new ArrayList<Position>());
      historyBackTestingProcessor.addClient(tempBrokerClient);
    }
  }




  public void addStrategy() {

  }

  public void addTestBrokerClient() {

  }

  private void startBackTesting(final String startTime, final String endTime) {
    new Thread() {
      public void run() {
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
    for (BrokerClient bc : this.historyBackTestingProcessor.getClientList()) {
      JSONObject bcJson = new JSONObject();
      bcJson.put("ClientId", bc.getClientId());
      bcJson.put("StrategyName", bc.getStrategyName());
      clients.put(bcJson);
    }


    JSONArray strategies = new JSONArray();
    Map<String, BaseStrategy> strategMap = this.historyBackTestingProcessor.getBackTestingStrategyManager().getStrategyMap();
    for (String bsKey : strategMap.keySet()) {
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
  public synchronized String getTestBrokerClientPerformanceData(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {
    final Gson gson = new Gson();
    JSONObject reply = new JSONObject();
    String testBrokerClientId = request.getParameter("testBrokerClientId");
    for (BrokerClient bc : this.historyBackTestingProcessor.getClientList()) {
      if (bc.getClientId().equals(testBrokerClientId)) {
        outputJsonData(response, gson.toJson(bc.getPerformanceData()));
        break;
      }
    }
    return null;
  }


  @RequestMapping(value = "system-console/downloadClientPerformanceCSV", method = RequestMethod.GET)
  public synchronized String downloadClientPerformanceCSV(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {

    String testBrokerClientId = request.getParameter("testBrokerClientId");

    List<Position> positions = new ArrayList<Position>();
    BrokerClient targetBroker = null;
    for (BrokerClient bc : this.historyBackTestingProcessor.getClientList()) {
      if (bc.getClientId().equals(testBrokerClientId)) {
        positions = bc.getClosedPositions();
        targetBroker = bc;
        break;
      }
    }

    Workbook wb = new HSSFWorkbook();
    String fileName = testBrokerClientId + "_PerformanceReport";
    try {
      Sheet sheet = wb.createSheet("closePosition");
      Row headerRow = sheet.createRow(0);

      headerRow.createCell(0).setCellValue("positionId");
      headerRow.createCell(1).setCellValue("instrument");
      headerRow.createCell(2).setCellValue("openTime");
      headerRow.createCell(3).setCellValue("closeTime");
      headerRow.createCell(4).setCellValue("direction");
      headerRow.createCell(5).setCellValue("status");
      headerRow.createCell(6).setCellValue("closeReason");
      headerRow.createCell(7).setCellValue("amount");
      headerRow.createCell(8).setCellValue("openPrice");
      headerRow.createCell(9).setCellValue("closePrice");
      headerRow.createCell(10).setCellValue("stopLossInPips");
      headerRow.createCell(11).setCellValue("takeProfitInPips");
      headerRow.createCell(12).setCellValue("PL in pips");
      headerRow.createCell(13).setCellValue("PL in Equity");

      int rowCount = 0;
      int profitNum = 0;
      int lostNum = 0;
      for (Position p : positions) {
        Row row = sheet.createRow(++rowCount);  //increment to account for header row
        row.createCell(0).setCellValue(p.getPositionId());
        row.createCell(1).setCellValue(p.getInstrument().toString());
        row.createCell(2).setCellValue(sdf.format(new Date(p.getOpenTime())));
        row.createCell(3).setCellValue(sdf.format(new Date(p.getCloseTime())));

        row.createCell(4).setCellValue(p.getDirection().toString());
        row.createCell(5).setCellValue(p.getStatus().toString());
        row.createCell(6).setCellValue(p.getCloseReason().toString());
        row.createCell(7).setCellValue(p.getAmount());
        row.createCell(8).setCellValue(p.getOpenPrice());
        row.createCell(9).setCellValue(p.getClosePrice());
        row.createCell(10).setCellValue(p.getStopLossInPips());
        row.createCell(11).setCellValue(p.getTakeProfitInPips());
        row.createCell(12).setCellValue(
                p.getDirection() == Direction.Short ?
                        ((p.getOpenPrice() - p.getClosePrice()) / p.getInstrument().getPipsValue()) :
                        ((p.getClosePrice() - p.getOpenPrice()) / p.getInstrument().getPipsValue()));

        row.createCell(13).setCellValue(
                p.getDirection() == Direction.Short ?
                        ((p.getOpenPrice() - p.getClosePrice()) * p.getAmount() * TradingUtils.getGolbalAmountUnit()) :
                        ((p.getClosePrice() - p.getOpenPrice()) * p.getAmount() * TradingUtils.getGolbalAmountUnit()));

        if (row.getCell(13).getNumericCellValue() > 0) {
          profitNum += 1;
        } else {
          lostNum += 1;
        }
      }

      rowCount += 4;
      Row row = sheet.createRow(++rowCount);
      row.createCell(1).setCellValue("Summary");

      row = sheet.createRow(++rowCount);
      row.createCell(0).setCellValue("Start Equity");
      row.createCell(1).setCellValue("End Equity");
      row.createCell(2).setCellValue("Trade Number");
      row.createCell(3).setCellValue("Profit Trade Number");
      row.createCell(4).setCellValue("Lost Trade Number");

      PerformanceData pd = targetBroker.getPerformanceData();
      row = sheet.createRow(++rowCount);
      row.createCell(0).setCellValue(targetBroker.getStartBalance());
      row.createCell(1).setCellValue(pd.getEquityRecords().get(pd.getEquityRecords().size() - 1).getEquity());
      row.createCell(2).setCellValue(targetBroker.getClosedPositions().size());

      String profitPercentage = df1.format(profitNum / (targetBroker.getClosedPositions().size() * 1.0) * 100);
      String lostPercentage = df1.format(lostNum / (targetBroker.getClosedPositions().size() * 1.0) * 100);

      row.createCell(3).setCellValue(profitNum + " (" + profitPercentage + "%)");
      row.createCell(4).setCellValue(lostNum + " (" + lostPercentage + "%)");


      //=================================Equity Sheet Start =============================
      sheet = wb.createSheet("Equity");
      headerRow = sheet.createRow(0);
      headerRow.createCell(0).setCellValue("endTime");
      headerRow.createCell(1).setCellValue("Equity");
      rowCount = 0;
      for (PerformanceData.EquityRecord er : pd.getEquityRecords()) {
        row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue(sdf.format(new Date(er.getEndTime())));
        row.createCell(1).setCellValue(er.getEquity());
      }


    } catch (Exception e) {
      logger.error(e, e);
    }
    FEUtil.writeExcelExportingResponse(wb, fileName, response);
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
    startBackTesting(startTime, endTime);
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
    Long startTime = sdf.parse(request.getParameter("startTime")).getTime();
    Long endTime = sdf.parse(request.getParameter("endTime")).getTime();
    JSONObject reply = new JSONObject();


    reply.put("chartData", reply);
    JSONUtils.writeJSON(reply, request, response);
    return null;
  }


  /**
   * Utility method responsible for writing JSON data to HttpServletResponse
   *
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
