package com.ideacode.news.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.webkit.CacheManager;

import com.ideacode.news.R;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.MD5;
import com.ideacode.news.common.util.MethodsCompat;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;

public class AppContext extends Application {

	public String mLocation;
	public double mLongitude;
	public double mLatitude;

	public static boolean SHOW_LOGIN_FLAG = false;

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	public static final int NEWSTYPE_FOCUS = 0;
	public static final int NEWSTYPE_CIVIL = 1;
	public static final int NEWSTYPE_WORK = 2;
	public static final int NEWSTYPE_COLLEGEEXAM = 3;
	public static final int NEWSTYPE_GRADUATE = 4;
	public static final int NEWSTYPE_CHINA = 5;
	public static final int NEWSTYPE_WORLD = 6;

	// 网页编码
	public static final String UTF_8 = "UTF-8";
	public static final String GB2312 = "GB2312";
	public static final String GBK = "GBK";

	// 中国教育新闻网前缀
	public static final String JOB_JYB_CN = "http://job.jyb.cn/jysx"; // 就业
	public static final String GAOKAO_JYB_CN = "http://gaokao.jyb.cn/gksx"; // 高考
	public static final String KAOYAN_JYB_CN = "http://kaoyan.jyb.cn/kysx"; // 考研
	public static final String CHINA_JYB_CN = "http://china.jyb.cn"; // 国内
	public static final String WORLD_JYB_CN = "http://world.jyb.cn/gjsx"; // 国际

    // 有米应用包发布ID、应用密钥
    public static final String YOUMI_ID = "7b45b6d12491f649 ";
    public static final String YOUMI_KEY = "635dd8877bceb878";

    // 主界面的bottom高度
    public static int bottomHeight;

	private boolean login = false; // 登录状态
	private long loginUid = 0; // 登录用户的id
	private final Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

	private final Handler unLoginHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				UIHelper.showLoginDialog(AppContext.this);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器
		// Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}

	/**
	 * 获取登录用户id
	 * 
	 * @return
	 */
	public long getLoginUid() {
		return this.loginUid;
	}

	/**
	 * 用户注销
	 */
	public void Logout() {
		this.cleanCookie();
		cleanLoginInfo();
	}

	/**
	 * 初始化用户登录信息
	 */
	public void initLoginInfo() {
		TbUser loginUser = getLoginInfo();
		if (loginUser != null && loginUser.getUserId() > 0 && loginUser.isRememberMe()) {
			this.loginUid = loginUser.getUserId();
			this.login = true;
		} else {
			this.Logout();
		}
	}

	/**
	 * 保存登录信息
	 * 
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo(final TbUser user) {
		this.loginUid = user.getUserId();
		this.login = true;
		setProperties(new Properties() {
			{
				setProperty("user.uid", String.valueOf(user.getUserId()));
				setProperty("user.name", user.getUserName());
				setProperty("user.pwd", MD5.MD5Encode(user.getUserPassword()));
				setProperty("user.location", user.getLocation());
				setProperty("user.isRememberMe", String.valueOf(user.isRememberMe()));// 是否记住我的信息
			}
		});
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty("user.uid", "user.name", "user.pwd", "user.location", "user.isRememberMe");
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public TbUser getLoginInfo() {
		TbUser lu = new TbUser();
		lu.setUserId(StringUtils.toLong(getProperty("user.uid")));
		lu.setUserName(getProperty("user.name"));
		lu.setUserPassword(MD5.MD5Encode(getProperty("user.pwd")));
		lu.setLocation(getProperty("user.location"));
		lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		return lu;
	}

	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 是否加载显示文章图片
	 * 
	 * @return
	 */
	public boolean isLoadImage() {
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		// 默认是加载的
		if (StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}

	/**
	 * 设置是否加载文章图片
	 * 
	 * @param b
	 */
	public void setConfigLoadimage(boolean b) {
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}

	/**
	 * 是否左右滑动
	 * 
	 * @return
	 */
	public boolean isScroll() {
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		// 默认是关闭左右滑动
		if (StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}

	/**
	 * 设置是否左右滑动
	 * 
	 * @param b
	 */
	public void setConfigScroll(boolean b) {
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 判断缓存数据是否可读 有为true 没有为false
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// 清除webview缓存
		File file = CacheManager.getCacheFileBaseDir();
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			file.delete();
		}
		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this), System.currentTimeMillis());
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}
}
