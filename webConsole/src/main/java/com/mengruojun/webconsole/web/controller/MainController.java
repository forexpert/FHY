package com.mengruojun.webconsole.web.controller;

import com.mengruojun.webconsole.service.SystemMonitorService;
import com.mengruojun.webconsole.utils.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MainController
 */
@Controller
public class MainController {

  @Autowired
  SystemMonitorService systemMonitorService;

  @RequestMapping(value="tab/{tabName}", method=RequestMethod.GET)
  public String getTabContentHtml(@PathVariable String tabName, Model model) {
    //model.addAttribute(new Account());
    return tabName;
  }


  /*********tab system-console******************/

    /*********ComponentInfo******************/
  @RequestMapping(value="system-console/ComponentInfo", method=RequestMethod.GET)
  public String getComponentInfo(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {

    JSONObject reply = new JSONObject();
    String cpuUsage = systemMonitorService.getCPUUsage();
    String memUsage = systemMonitorService.getMemUsage();
    boolean isJMSServerRunning = systemMonitorService.isJMSServerRunning();
    boolean isMockBrokerServerRunning = systemMonitorService.isMockBrokerServerRunning();
    boolean isStrategyServerRunning = systemMonitorService.isStrategyServerRunning();
    reply.put("cpuUsage", cpuUsage);
    reply.put("memUsage", memUsage);
    reply.put("isJMSServerRunning", isJMSServerRunning);
    reply.put("isMockBrokerServerRunning", isMockBrokerServerRunning);
    reply.put("isStrategyServerRunning", isStrategyServerRunning);
    JSONUtils.writeJSON(reply, request, response);
    //ManagementFactory.
    return null;
  }

  @RequestMapping(value="system-console/startComponent", method=RequestMethod.POST)
  public void startComponent(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {


  }

}
