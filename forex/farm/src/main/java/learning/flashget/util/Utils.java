package learning.flashget.util;

import java.text.DecimalFormat;

/**
 * Created by clyde on 4/18/14.
 */
public class Utils {

  public static String readableFileSize(long size) {
    try{
      if(size <= 0) return "0";
      final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
      int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
      return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }catch(Exception e){
      e.printStackTrace();
      return "0KB";
    }
  }

  public static String percentageFormatter(double percentage){
    return new DecimalFormat("##.#%").format(percentage);
  }
}