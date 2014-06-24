package bond;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I074995 on 6/24/2014.
 */
public class Yinfang {
    static Logger logger = Logger.getLogger(Yinfang.class);

    public static void main(String[] args) throws Exception {
        new Yinfang().login();
    }


    public String getValidCode(){
        //todo
        return "";
    }
    public void login() throws Exception {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            HttpHost target = new HttpHost("www.yinfangjinrong.com", 80, "http");
            HttpHost proxy = new HttpHost("proxy.successfactors.com", 8080, "http");

            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            HttpGet request = new HttpGet("/validimg.html");
            request.setConfig(config);

            System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

            CloseableHttpResponse response = httpclient.execute(target, request);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                // logger.info(EntityUtils.toString(response.getEntity()));
                FileChannel channel = new FileOutputStream("C:\\Users\\I074995\\temp\\code.jpg").getChannel();
                channel.write(ByteBuffer.wrap(EntityUtils.toByteArray(response.getEntity())));
                channel.close();
                EntityUtils.consume(response.getEntity());

                System.out.println("Initial set of cookies:");
                List<Cookie> cookies = cookieStore.getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        System.out.println("- " + cookies.get(i).toString());
                    }
                }
            } finally {
                response.close();
            }


            HttpPost post = new HttpPost("/user/login.html");
            post.setConfig(config);
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("username", "mengsifang"));
            formparams.add(new BasicNameValuePair("password", "mqy597411"));
            formparams.add(new BasicNameValuePair("actionType", "login"));
            String valicode = "123";
            formparams.add(new BasicNameValuePair("valicode", valicode));
            UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(formparams, "UTF-8");
            post.setEntity(entity1);


            CloseableHttpResponse response2 = httpclient.execute(target, post);
            try {
                HttpEntity entity = response2.getEntity();

                System.out.println("Login form get: " + response2.getStatusLine());
                logger.info(EntityUtils.toString(response2.getEntity()));
                EntityUtils.consume(entity);

                System.out.println("Post logon cookies:");
                List<Cookie> cookies = cookieStore.getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("None");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        System.out.println("- " + cookies.get(i).toString());
                    }
                }
            } finally {
                response2.close();
            }


        } finally {
            httpclient.close();
        }
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

}
