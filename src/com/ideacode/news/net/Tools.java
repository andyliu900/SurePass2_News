package com.ideacode.news.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;

import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.Update;
import com.ideacode.news.common.util.CommonSetting;

public class Tools {

    public static final String UTF_8 = "UTF-8";
    private final static int TIMEOUT_CONNECTION = 20000;
    private final static int TIMEOUT_SOCKET = 20000;
    private final static int RETRY_TIME = 3;

    private static String appCookie;
    private static String appUserAgent;

    public static String getHtmlData(String urlpath,String enCodeType) throws AppException {
        try {
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("contentType", enCodeType); 

            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                String html = readStream(inputStream,enCodeType);
                return html;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw AppException.url(e);   
        } catch (ProtocolException e) {
            e.printStackTrace();
            throw AppException.protocol(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AppException.io(e);
        }
        return null;
    }

    public static String readStream(InputStream inputStream,String enCodeType) throws AppException {

        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, enCodeType));
            buffer = new StringBuffer();  
            String line = "";
            while ((line = in.readLine()) != null){  
                buffer.append(line);  
            }
            in.close();
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw AppException.unsupportedEncoding(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AppException.io(e);
        }

        return buffer.toString();
    }

    private static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }

    private static String getUserAgent(AppContext appContext) {
        if (appUserAgent == null || appUserAgent == "") {
            StringBuilder ua = new StringBuilder("OSChina.NET");
            ua.append('/' + appContext.getPackageInfo().versionName + '_' + appContext.getPackageInfo().versionCode);// App版本
            ua.append("/Android");// 手机系统平台
            ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
            ua.append("/" + android.os.Build.MODEL); // 手机型号
            ua.append("/" + appContext.getAppId());// 客户端唯一标识
            appUserAgent = ua.toString();
        }
        return appUserAgent;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集
        httpClient.getParams().setContentCharset(UTF_8);
        return httpClient;
    }

    private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpGet.setRequestHeader("Host", CommonSetting.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        httpGet.setRequestHeader("Cookie", cookie);
        httpGet.setRequestHeader("User-Agent", userAgent);
        return httpGet;
    }

    /**
     * 检查版本更新
     * 
     * @param url
     * @return
     */
    public static Update checkVersion(AppContext appContext) throws AppException {
        try {
            return Update.parse(http_get(appContext, CommonSetting.UPDATE_VERSION));
        } catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }

    /**
     * get请求URL
     * 
     * @param url
     * @throws AppException
     */
    private static InputStream http_get(AppContext appContext, String url) throws AppException {
        // System.out.println("get_url==> "+url);
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        HttpClient httpClient = null;
        GetMethod httpGet = null;

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, cookie, userAgent);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                responseBody = httpGet.getResponseBodyAsString();
                // System.out.println("XMLDATA=====>"+responseBody);
                break;
            } catch (AppException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return new ByteArrayInputStream(responseBody.getBytes());
    }
}
