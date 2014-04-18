package learning.flashget.client;

import learning.flashget.util.Utils;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 1. download from http server simply
 */
public class SimpleHttpDownloadClientWithProgressRecord {
  static Logger logger = Logger.getLogger(SimpleHttpDownloadClientWithProgressRecord.class);


  public static void main(String[] args) throws InterruptedException, ExecutionException {
    //String url = "http://10.129.118.50:8080/Morgan%20Stanley%20Interview%20Question_Chinese.doc";
    String url = "http://10.129.118.50:8080/hanastudio.zip";
    if(args.length >0){
      url = args[0];
    }
    DownloadTask task = new DownloadTask(url);
    FutureTask<Long> futureTask = new FutureTask<Long>(task);
    new Thread(futureTask,"Download Task").start();
    for(;;){
      Long totalLength = task.getTotalLength();
      if(totalLength!=null){
        String currentProgress = getProgress(task, totalLength);
        if(currentProgress!=null)logger.info("Downloaded Progress is " + currentProgress);
        if(currentProgress.equals("100%")){
          logger.info("Total download time is " + futureTask.get()/1000 + " seconds");
        }
      }
      Thread.sleep(2000L);
    }

  }

  private static String getProgress(DownloadTask task,Long totalLength) {
    Long readLength = task.getReadLength().get();
    return Utils.percentageFormatter(readLength/totalLength);
  }

}

class DownloadTask implements Callable<Long> {
  Logger logger = Logger.getLogger(this.getClass());
  private String url;
  private Long totalLength = null;
  private AtomicLong readLength = new AtomicLong(0L);


  public DownloadTask(String url) {
    this.url = url;
  }

  public AtomicLong getReadLength() {
    return readLength;
  }

  public Long getTotalLength() {
    return totalLength;
  }

  @Override
  /**
   * return the download time in milliseconds
   */
  public Long call() throws Exception {
    logger.info("Start Download!");
    long start = System.currentTimeMillis();

    doDownload(url);

    logger.info("Start Finish!");
    long end = System.currentTimeMillis();
    return end -start;
  }

  private void doDownload(String url){
    InputStream in = null;
    FileOutputStream out = null;
    try {
      logger.info("Start Download!");
      out = new FileOutputStream("interview_question.doc");
      HttpURLConnection conn= (HttpURLConnection)new URL(url).openConnection();
      totalLength = getHeadLength(conn);

      in = new BufferedInputStream(conn.getInputStream());

      byte[] bbuf = new byte[10240];
      int hasRead = 0;
      while((hasRead = in.read(bbuf))>0){
        readLength.addAndGet(hasRead);
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



  private String getRemoteFileName(HttpURLConnection conn) {
    List values = conn.getHeaderFields().get("Content-Disposition");
    if (values != null && !values.isEmpty()) {
      // getHeaderFields() returns a Map with key=(String) header
      // name, value = List of String values for that header field.
      // just use the first value here.
      String names = (String) values.get(0);
      if (names != null) {
        logger.info("Content-Disposition is " + names);
        return names.split("\"")[1];
      }
    }
    return null;
  }

  private Long getHeadLength(HttpURLConnection conn) {
    List values = conn.getHeaderFields().get("Content-Length");
    if (values != null && !values.isEmpty()) {
      // getHeaderFields() returns a Map with key=(String) header
      // name, value = List of String values for that header field.
      // just use the first value here.
      String sLength = (String) values.get(0);
      if (sLength != null) {
        logger.info("file size is " + Utils.readableFileSize(totalLength));
        logger.info("file size is " + sLength);
        return Long.valueOf(sLength);
      }
    }
    return 0L;
  }
}