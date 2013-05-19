package com.mengruojun.webconsole.utils;

import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 5/18/13
 * Time: 8:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class FEUtil {
  /**
   * a common method to help write the excel exporting response
   * @param wb
   * @param fileName
   * @param response
   * @throws java.io.IOException
   */
  public static void writeExcelExportingResponse(Workbook wb, String fileName, HttpServletResponse response) throws IOException {
    //stream XLS workbook on the response as a file attachment
    response.setContentType("application/vnd.ms-excel");
    // work around IE bug
    response.setHeader("Pragma", "private");
    response.setHeader("Cache-Control", "private, must-revalidate");

    response.setHeader("Content-disposition", "attachment;filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + ".xls\"");
    response.addHeader("Content-description", "Event Attendees");
    OutputStream out = response.getOutputStream();
    wb.write(out);
    out.flush();
    out.close();
  }
}
