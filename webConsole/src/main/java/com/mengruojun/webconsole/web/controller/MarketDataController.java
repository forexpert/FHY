package com.mengruojun.webconsole.web.controller;

import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.TimeWindowType;
import com.mengruojun.common.utils.TradingUtils;
import com.mengruojun.webconsole.utils.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Controller
public class MarketDataController {
  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  static SimpleDateFormat sdf = TradingUtils.DATE_FORMAT;

  /**
   * ******ComponentInfo*****************
   */
  @RequestMapping(value = "system-console/ComponentInfo", method = RequestMethod.GET)
  public String getComponentInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException, ParseException {
    String instrument = request.getParameter("instrument");
    TimeWindowType twt = TimeWindowType.valueOf(request.getParameter("timeWindowType"));
    Long startTime =sdf.parse(request.getParameter("startTime")).getTime();
    Long endTime = sdf.parse(request.getParameter("endTime")).getTime();
    JSONObject reply = new JSONObject();


    reply.put("chartData", reply);
    JSONUtils.writeJSON(reply, request, response);
    return null;
  }

}
