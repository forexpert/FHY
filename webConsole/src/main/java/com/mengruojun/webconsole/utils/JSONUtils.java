package com.mengruojun.webconsole.utils;

/**
 * Created with IntelliJ IDEA.
 * User: clyde
 * Date: 2/5/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */

import org.json.JSONObject;
import org.json.JSONException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Utility methods for dealing with JSON.
 *
 * @author jmartin
 */
@SuppressWarnings({"UtilityClass"})
public class JSONUtils {
  public static final String JSON_CONTENT_TYPE = "application/json";
  public static final String JSON_P_CONTENT_TYPE = "text/javascript";
  public static final String JSON_ERROR_MESSAGE_KEY = "message";

  /** empty private constructor */
  private JSONUtils() {
  }

  /**
   * Writes JSON to an http response using the "application/json" content type.
   * <br/><br/>
   * Optional http parameters: <br/><br/>
   *
   * indent: indent json <br/>
   *
   * @param jsonObject json object
   * @param request http request
   * @param response http response
   * @throws IOException on IOException
   * @throws org.json.JSONException on JSONException
   */
  public static void writeJSON(JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response)
          throws IOException, JSONException
  {
    int indentFactor = 0;
    String indentFactorParam = request.getParameter("indent");
    if (indentFactorParam != null) {
      indentFactor = Integer.parseInt(indentFactorParam);
    }

    response.setContentType(JSON_CONTENT_TYPE);
    if (indentFactor > 0)
      response.getWriter().write(jsonObject.toString(indentFactor)); // for debugging
    else
      jsonObject.write(response.getWriter()); // else don't make a copy of the string

  }

  /**
   * Standard mechanism for sending JSON and indicating success.
   *
   * @param jsonObject json object
   * @param request http request
   * @param response http response
   * @throws IOException on IOException
   * @throws JSONException on JSONException
   */
  public static void writeJSONSuccess(JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
    writeJSON(jsonObject, request, response);
  }

  /**
   * Standard mechanism for sending JSON that encapsulates an error message and indicates an error via HTTP Status.
   *
   * @param message message
   * @param request http request
   * @param response http response
   * @throws IOException on IOException
   * @throws JSONException on JSONException
   */
  public static void writeJSONFailure(String message, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(JSON_ERROR_MESSAGE_KEY, message);
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    writeJSON(jsonObject, request, response);
  }

  /**
   * Writes JSON to an http response using the "application/json" content type.
   * <br/><br/>
   * WARNING: this method allows for a javascript callback function to be wrapped around the json, which will be
   * called when the json is loaded on the client.  Do not use this method unless you specifically want to provide
   * this functionallity to the client.
   * <br/> <br/>
   * Optional http parameters: <br/><br/>
   *
   * indent: indent json <br/>
   * callback: wrap json with a javascript callback method and change content type to "text/javascript"<br/>
   *
   *
   * @param jsonObject json object
   * @param request http request
   * @param response http response
   * @throws IOException on IOException
   * @throws org.json.JSONException on JSONException
   */
  public static void writeJSONWithCallback(JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response)
          throws IOException, JSONException
  {
    int indentFactor = 0;
    String indentFactorParam = request.getParameter("indent");
    if (indentFactorParam != null) {
      indentFactor = Integer.parseInt(indentFactorParam);
    }

    String callback = request.getParameter("callback");

    if (callback != null) {
      response.setContentType(JSON_P_CONTENT_TYPE);
    } else {
      response.setContentType(JSON_CONTENT_TYPE);
    }

    if (callback != null) {
      response.getWriter().write(callback + '(');
    }

    response.getWriter().write(indentFactor > 0 ? jsonObject.toString(indentFactor) : jsonObject.toString());

    if (callback != null) {
      response.getWriter().write(");");
    }

  }

}

