/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mengruojun.pure.model.strategy.tools;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.mengruojun.common.dao.HistoryDataKBarDao;
import com.mengruojun.common.domain.Instrument;
import com.mengruojun.pure.model.BarField;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author clyde
 */
@Service
public class HistoryBackTestingService implements Runnable{


  public static Logger log = Logger.getLogger(HistoryBackTestingService.class);

  public static final String historyMarketDataFileName = "historyMarketData.csv";
  public static final String historyMarketDataDirectory = ".ForexInvest/historyMarketData/";
  private Instrument testInstrument = Instrument.EURUSD;

  @Autowired
  HistoryDataKBarDao historyDataKBarDao;
  @Override
  public void run() {
    try {
      log.info("Start to doBackTesting ");
      doBackTesting();
    } catch (Exception e) {
      log.error("", e);
    }
  }

  private String getHistoryMarketDataFullFileName(){
    return System.getProperty("user.home") +
            historyMarketDataDirectory +
            testInstrument.getCurrency1().toString() +
            testInstrument.getCurrency2().toString() + "_" + historyMarketDataFileName;
  }

  /**
   * doBackTesting
   * <p/>
   * Step1 generate backtesting market data
   * <p/>
   * <p/>
   * Step2 doBacktestingInGenericAlgorithm
   */
  public void doBackTesting() throws IOException {
    // Declare Variables
    CSVReader historyMarketData = null;



    //Step1 generate backtesting market data
    historyMarketData = generateBackTestingMarketData();
    log.info("marketData is " + historyMarketData);

    //Step2 doBacktestingInGenericAlgorithm
    doBackTestingInGeneticAlgorithm(historyMarketData);

    // clean file handles
    historyMarketData.close();

  }

  /**
   * generateBackTestingMarketData
   */
  private CSVReader generateBackTestingMarketData() throws IOException {
    CSVReader marketData = null;
    boolean reGenerateData = false;
    try {
      marketData = new CSVReader(new FileReader(getHistoryMarketDataFullFileName()));
      reGenerateData = determineIfNeedReGenerateMarketData(marketData);
    } catch (FileNotFoundException e) {
      reGenerateData = true;
    }
    if(reGenerateData){
      if(marketData != null)marketData.close();
      marketData = generateMarketData();
    }


    return marketData;
  }

  private boolean determineIfNeedReGenerateMarketData(CSVReader marketData) {
    //todo cmeng
    return true;
  }


  private String[] getMetaDataNames(){
    BarField[] metaDataBarFields = BarField.values();
    String[] metaDataNames = new String[metaDataBarFields.length];
    for(int i = 0; i < metaDataBarFields.length; i++){
      BarField barField = metaDataBarFields[i];
      metaDataNames[i] = barField.name();
    }
    return metaDataNames;
  }
  private CSVReader generateMarketData() throws IOException {
    CSVWriter csvWriter = null;
    try {
      csvWriter = new CSVWriter(new FileWriter(getHistoryMarketDataFullFileName()));
      //write first line -- metadata
      csvWriter.writeNext(getMetaDataNames());

      //write data content -- data
      BarField[] metaDataBarFields = BarField.values();




    } catch (IOException e) {
      log.error("", e);
    } finally {
      csvWriter.close();
      return new CSVReader(new FileReader(getHistoryMarketDataFullFileName()));
    }

  }


  private void doBackTestingInGeneticAlgorithm(CSVReader historyMarketData) {
    // step1 prepare the first generation robots

    // step2 do one turn backTesting for each robots

    // step3 keep robots with good performance

    // step4 mate the robot randomly for robot and produce the next generation


  }


  private void doBackTestingInAnnealingAlgorithm(CSVReader historyMarketData) {

  }

}
