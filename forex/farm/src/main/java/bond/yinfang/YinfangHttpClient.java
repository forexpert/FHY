package bond.yinfang;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I074995 on 6/24/2014.
 */
public class YinfangHttpClient {
    static Logger logger = Logger.getLogger(YinfangHttpClient.class);

    public static void main(String[] args) throws Exception {
        new YinfangHttpClient().startWorkFlow();
    }

    private BasicCookieStore cookieStore = new BasicCookieStore();
    private CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    private HttpHost target = new HttpHost("www.yinfangjinrong.com", 80, "http");
    private HttpHost proxy = new HttpHost("proxy.successfactors.com", 8080, "http");
    private RequestConfig config = RequestConfig.custom().setProxy(proxy).build();


    public YinfangHttpClient() {

    }

    public void startWorkFlow() throws Exception {
        try {
            String redirectURL = login();
            AccountInfo accountInfo = (AccountInfo) getRequest(redirectURL, new AccountInfoResponseHandler());

        } finally {
            httpclient.close();
        }
    }


    public void downloadTrainImage() throws Exception {

        HttpGet request = new HttpGet("/validimg.html");
        request.setConfig(config);
        logger.info("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);
        for (int i = 0; i < 1; i++) {
            CloseableHttpResponse response = httpclient.execute(target, request);
            try {
                File dest = new File("C:\\Users\\I074995\\temp\\yinfang\\training1");
                if (!dest.exists()) dest.mkdirs();
                FileChannel channel = new FileOutputStream(dest.getAbsolutePath() + "\\code" + i + ".jpg").getChannel();
                channel.write(ByteBuffer.wrap(EntityUtils.toByteArray(response.getEntity())));
                channel.close();
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        }
    }

    public String recognizeValidCode(byte[] image) {
        try {
            return new YinfangValidCodeRecognizer().recognize(image);
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }

    private Object getRequest(String URL, ResponseHandler handler) throws Exception {
        HttpGet request = new HttpGet(URL);
        request.setConfig(config);
        CloseableHttpResponse response = httpclient.execute(target, request);
        try {
            logger.info("Status of " + request.getRequestLine() + " is " + response.getStatusLine());
            return handler.handleResponse(response);
        } finally {
            response.close();
        }
    }

    private String getValidCode() throws Exception {
        HttpGet request = new HttpGet("/validimg.html");
        request.setConfig(config);
        logger.info("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

        CloseableHttpResponse response = httpclient.execute(target, request);
        try {
            logger.info("Status of " + request.getRequestLine() + " is " + response.getStatusLine());
            FileChannel channel = new FileOutputStream("C:\\Users\\I074995\\temp\\code.jpg").getChannel();
            byte[] image = EntityUtils.toByteArray(response.getEntity());
            channel.write(ByteBuffer.wrap(image));
            channel.close();
            String validCode = recognizeValidCode(image);
            EntityUtils.consume(response.getEntity());
            return validCode;
        } finally {
            response.close();
        }
    }

    private String login() throws Exception {
        boolean login = false;
        int maxLoginTimes = 50;
        while (!login && maxLoginTimes-- > 0) {

            String validCode = getValidCode();
            logger.info("The validCode is " + validCode);
            HttpPost post = new HttpPost("/user/login.html");
            post.setConfig(config);
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("username", "mengsifang"));
            formparams.add(new BasicNameValuePair("password", "mqy597411"));
            formparams.add(new BasicNameValuePair("actionType", "login"));
            formparams.add(new BasicNameValuePair("valicode", validCode));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            post.setEntity(formEntity);

            CloseableHttpResponse response = httpclient.execute(target, post);
            try {
                HttpEntity entity = response.getEntity();
                logger.info("Login response http status: " + response.getStatusLine());
                if (response.getStatusLine().getStatusCode() == 302 || response.getStatusLine().getStatusCode() == 301) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    //logger.info("Response is : " + responseStr);
                    login = true;
                    Header[] headers = response.getHeaders("location");
                    if (headers.length == 0) {
                        logger.error("No Redirect URL");
                    } else {
                        String redirectURL = headers[0].getValue();
                        return redirectURL;
                    }
                } else {
                    logger.info("Incorrect Valid Code Recognition");
                }

                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        }
        return null;
    }


//=========temp code======

    public static void testCookie() throws Exception {
        //获取Cookie
        DefaultHttpClient client2 = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://www.yinfangjinrong.com/validimg.html");
        HttpHost proxy = new HttpHost("proxy.successfactors.com", 8080, "http");
        client2.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        HttpResponse res = client2.execute(httpget);

        List<Cookie> cookies = client2.getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
            System.out.println(cookie.getValue());
        }

        EntityUtils.consume(res.getEntity());
    }

    private class AccountInfoResponseHandler implements ResponseHandler {

        @Override
        public AccountInfo handleResponse(HttpResponse response) throws HttpResponseException, IOException {
            AccountInfo  accountInfo= new AccountInfo();
            final StatusLine statusLine = response.getStatusLine();
            final HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            String accountPage = EntityUtils.toString(entity);
            String accountAvailableBlance = "";
            Document doc = Jsoup.parse(accountPage);
            Element myAccountTable = doc.select("#myTabContent table").get(0);
            for(Element element : myAccountTable.select("td")){
                if(element.getElementsByTag("a").size()==1 && element.getElementsByTag("a").text().equals("可用余额")){
                    if(element.getElementsByTag("font").size()==1){
                        accountAvailableBlance = element.getElementsByTag("font").text();
                        logger.info("accountAvailableBlance is " + accountAvailableBlance);
                        accountInfo.setAvailableBlance(new BigDecimal(accountAvailableBlance.substring(1)));
                    }
                }
            }
            return accountInfo;
        }
    }


    private class AccountInfo{
        private BigDecimal availableBlance;

        public BigDecimal getAvailableBlance() {
            return availableBlance;
        }

        public void setAvailableBlance(BigDecimal availableBlance) {
            this.availableBlance = availableBlance;
        }
    }
}

