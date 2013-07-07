package com.ideacode.news.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ideacode.news.common.util.StringUtils;

/**
 * 应用程序配置类：用于保存用户相关信息及设罄1�7
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppConfig {

    private final static String APP_CONFIG = "config";

    public final static String TEMP_TWEET = "temp_tweet";
    public final static String TEMP_TWEET_IMAGE = "temp_tweet_image";
    public final static String TEMP_MESSAGE = "temp_message";
    public final static String TEMP_COMMENT = "temp_comment";
    public final static String TEMP_POST_TITLE = "temp_post_title";
    public final static String TEMP_POST_CATALOG = "temp_post_catalog";
    public final static String TEMP_POST_CONTENT = "temp_post_content";

    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
    public final static String CONF_COOKIE = "cookie";
    public final static String CONF_ACCESSTOKEN = "accessToken";
    public final static String CONF_ACCESSSECRET = "accessSecret";
    public final static String CONF_EXPIRESIN = "expiresIn";
    public final static String CONF_LOAD_IMAGE = "perf_loadimage";
    public final static String CONF_SCROLL = "perf_scroll";
    public final static String CONF_HTTPS_LOGIN = "perf_httpslogin";
    public final static String CONF_VOICE = "perf_voice";

    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 是否加载显示文章图片
     */
    public static boolean isLoadImage(Context context) {
        return getSharedPreferences(context).getBoolean(CONF_LOAD_IMAGE, true);
    }

    public String getCookie() {
        return get(CONF_COOKIE);
    }

    public void setAccessToken(String accessToken) {
        set(CONF_ACCESSTOKEN, accessToken);
    }

    public String getAccessToken() {
        return get(CONF_ACCESSTOKEN);
    }

    public void setAccessSecret(String accessSecret) {
        set(CONF_ACCESSSECRET, accessSecret);
    }

    public String getAccessSecret() {
        return get(CONF_ACCESSSECRET);
    }

    public void setExpiresIn(long expiresIn) {
        set(CONF_EXPIRESIN, String.valueOf(expiresIn));
    }

    public long getExpiresIn() {
        return StringUtils.toLong(get(CONF_EXPIRESIN));
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }
}
