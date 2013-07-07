package com.ideacode.news.common.util;

import com.ideacode.news.bean.CityEntity;
import com.ideacode.news.bean.ProvinceEntity;

/**
 * <p>
 * FileName: CommonSetting.java
 * </p>
 * <p>
 * Description: 常量文字说明
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 * 
 * @author Vic Su
 *         </p>
 *         <p>
 * @content andyliu900@gmail.com
 *          </p>
 *          <p>
 * @version 1.0
 *          </p>
 *          <p>
 *          CreatDate: 2012-11-6 下午10:57:01
 *          </p>
 *          <p>
 *          Modification History
 */
public class CommonSetting {

	public static ProvinceEntity[] provinces = null;
	public static CityEntity[][] cities = null;
	public static Integer[] ids = new Integer[2];

	public static final String ERROR_TAG = "SurePass2.0";
	public static final String FileNameTag = "fileNameTag";
	public static final String loginUserId = "loginUserId";
	public static final String loginUserName = "loginUserName";

    // public final static String HOST = "10.87.61.111:6060/surepass2server";
    // public final static String HOST = "192.168.1.101:8116/surepass2server";
    public final static String HOST = "1.surepass2server.sinaapp.com";
    public final static String STORAGR = "surepass2server-surepass2domain.stor.sinaapp.com";
	public final static String HTTP = "http://";
	private final static String URL_SPLITTER = "/";
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
    public final static String UPDATE_VERSION = HTTP + STORAGR + URL_SPLITTER + "updates/MobileAppVersion.xml";
	public static final String WebServiceUrl = URL_API_HOST + "services/";
	public static final int Fail = 0; // 后台传输数据失败
	public static final int Success = 1; // 后台传输数据成功
	public static final int SoapException = 2; // soap传输错误
	public static final int InitSystemDataException = 3; // 初始化系统数据失败
	public static final int SoapFault = 4; // 解析soap对象失败
}