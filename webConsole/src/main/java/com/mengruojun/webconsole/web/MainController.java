package com.mengruojun.webconsole.web;

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
    double cpuUsage = systemMonitorService.getCPUUsage();
    double memUsage = systemMonitorService.getMemUsage();


    reply.put("status", "success");
    JSONUtils.writeJSON(reply, request, response);
    //ManagementFactory.
    return null;
  }

}
