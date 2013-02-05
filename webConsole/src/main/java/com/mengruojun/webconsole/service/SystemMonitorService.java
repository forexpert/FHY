package com.mengruojun.webconsole.service;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * SystemMonitorService
 */
public class SystemMonitorService {
  Logger logger = Logger.getLogger(SystemMonitorService.class);

  private int jmsPort;

  public boolean isJMSServerRunning() {
    try {
      String[] cmd = {
              "/bin/sh",
              "-c",
              "netstat -ntpl | grep " + jmsPort
      };
      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String s = "";
      while ((s = stdInput.readLine()) != null) {
        logger.debug(s);
        if(s.contains(jmsPort+"")){
          return true;
        }
      }
    } catch (IOException e) {
      logger.error("", e);
    } catch (InterruptedException e) {
      logger.error("", e);
    }
    return false;
  }

  public double getMemUsage() {
    int[] memInfo = new int[0];
    try {
      memInfo = LinuxSystemTool.getMemInfo();
      return memInfo[1] / memInfo[0];
    } catch (IOException e) {
      logger.error("", e);
    } catch (InterruptedException e) {
      logger.error("", e);
    }

    return Double.NaN;
  }

  public double getCPUUsage() {
    try {
      return LinuxSystemTool.getCpuInfo();
    } catch (IOException e) {
      logger.error("", e);
    } catch (InterruptedException e) {
      logger.error("", e);
    }
    return Double.NaN;
  }


  public static void main(String[] args) throws Exception {
    int[] memInfo = LinuxSystemTool.getMemInfo();
    System.out.println("MemTotal：" + memInfo[0]);
    System.out.println("MemFree：" + memInfo[1]);
    System.out.println("SwapTotal：" + memInfo[2]);
    System.out.println("SwapFree：" + memInfo[3]);

    System.out.println("CPU利用率：" + LinuxSystemTool.getCpuInfo());
  }


  //getter and setter

  public int getJmsPort() {
    return jmsPort;
  }

  public void setJmsPort(int jmsPort) {
    this.jmsPort = jmsPort;
  }
}

/**
 * 取得linux系统下的cpu、内存信息
 */
final class LinuxSystemTool {
  /**
   * get memory by used info
   *
   * @return int[] result
   *         result.length==4;int[0]=MemTotal;int[1]=MemFree;int[2]=SwapTotal;int[3]=SwapFree;
   * @throws IOException
   * @throws InterruptedException
   */
  public static int[] getMemInfo() throws IOException, InterruptedException {
    File file = new File("/proc/meminfo");
    BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(file)));
    int[] result = new int[4];
    String str = null;
    StringTokenizer token = null;
    while ((str = br.readLine()) != null) {
      token = new StringTokenizer(str);
      if (!token.hasMoreTokens())
        continue;

      str = token.nextToken();
      if (!token.hasMoreTokens())
        continue;

      if (str.equalsIgnoreCase("MemTotal:"))
        result[0] = Integer.parseInt(token.nextToken());
      else if (str.equalsIgnoreCase("MemFree:"))
        result[1] = Integer.parseInt(token.nextToken());
      else if (str.equalsIgnoreCase("SwapTotal:"))
        result[2] = Integer.parseInt(token.nextToken());
      else if (str.equalsIgnoreCase("SwapFree:"))
        result[3] = Integer.parseInt(token.nextToken());
    }

    return result;
  }

  /**
   * get memory by used info
   *
   * @return float efficiency
   * @throws IOException
   * @throws InterruptedException
   */
  public static float getCpuInfo() throws IOException, InterruptedException {
    File file = new File("/proc/stat");
    BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(file)));
    StringTokenizer token = new StringTokenizer(br.readLine());
    token.nextToken();
    int user1 = Integer.parseInt(token.nextToken());
    int nice1 = Integer.parseInt(token.nextToken());
    int sys1 = Integer.parseInt(token.nextToken());
    int idle1 = Integer.parseInt(token.nextToken());

    Thread.sleep(1000);

    br = new BufferedReader(
            new InputStreamReader(new FileInputStream(file)));
    token = new StringTokenizer(br.readLine());
    token.nextToken();
    int user2 = Integer.parseInt(token.nextToken());
    int nice2 = Integer.parseInt(token.nextToken());
    int sys2 = Integer.parseInt(token.nextToken());
    int idle2 = Integer.parseInt(token.nextToken());

    return (float) ((user2 + sys2 + nice2) - (user1 + sys1 + nice1)) / (float) ((user2 + nice2 + sys2 + idle2) - (user1 + nice1 + sys1 + idle1));
  }
}