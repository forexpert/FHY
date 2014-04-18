package learning.flashget.client;

import learning.flashget.util.Utils;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 1. download from http server simply
 */
public class SimpleHttpDownloadClient {
  Logger logger = Logger.getLogger(this.getClass());
  public void download(String url){
    //String url = "http://10.129.118.50:8080/hanastudio.zip";
    //String url = "http://10.129.118.50:8080/Morgan%20Stanley%20Interview%20Question_Chinese.doc";
    InputStream in = null;
    FileOutputStream out = null;
    try {
      logger.info("Start Download!");
      File downloadPath = new File(System.getProperty("user.home") + File.separator +
              "javatest" + File.separator );
      if(!downloadPath.exists())downloadPath.mkdirs();
      out = new FileOutputStream(downloadPath.getAbsolutePath() + File.separator + "interview_question.doc");
      HttpURLConnection conn= (HttpURLConnection)new URL(url).openConnection();
      List values = conn.getHeaderFields().get("Content-Length");
      if (values != null && !values.isEmpty()) {
        // getHeaderFields() returns a Map with key=(String) header
        // name, value = List of String values for that header field.
        // just use the first value here.
        String sLength = (String) values.get(0);
        if (sLength != null) {
          logger.info("file size is " + Utils.readableFileSize(Long.valueOf(sLength)));
          logger.info("file size is " + sLength);
        }
      }
      in = new BufferedInputStream(conn.getInputStream());
      byte[] bbuf = new byte[32];
      int hasRead = 0;
      while((hasRead = in.read(bbuf))>0){
        out.write(bbuf, 0, hasRead);
      }

      logger.info("Finished!");
    } catch (IOException e) {
      logger.error("", e);
    }finally {
      try{
        if(in!=null) in.close();
        if(out!=null) out.close();
      }catch (IOException e){
        logger.error("", e);
      }
    }
  }



  public static void main(String[] args){
    String url = "http://10.129.118.50:8080/Morgan%20Stanley%20Interview%20Question_Chinese.doc";
    if(args.length >0){
      url = args[0];
    }
    new SimpleHttpDownloadClient().download(url);
  }

}
