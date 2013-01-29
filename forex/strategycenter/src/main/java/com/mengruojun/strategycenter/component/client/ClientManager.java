package com.mengruojun.strategycenter.component.client;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Manager multiple broker clients
 * 1. registering clients
 * 2. sync portfolio
 * 3. send trading action to clients
 */
public class ClientManager implements Runnable, ApplicationListener {



  @Override
  public void run() {

  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {

  }
}
