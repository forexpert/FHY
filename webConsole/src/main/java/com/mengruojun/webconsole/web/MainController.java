package com.mengruojun.webconsole.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * MainController
 */
@Controller
public class MainController {

  @RequestMapping(value="tab/{tabName}", method=RequestMethod.GET)
  public String getMain(@PathVariable String tabName, Model model) {
    //model.addAttribute(new Account());
    return tabName;
  }

}
