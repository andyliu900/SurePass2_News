package com.ideacode.news.module.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.util.Log;

import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.net.SoapWebServiceUtil;

public class MemberUtil {

	private static final String TAG = "MemberUtil";

	public static HashMap<String, Object> login(TbUser tbUser) throws AppException {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		JSONObject json = convertTbUser2JSONObject(tbUser);

		HashMap paramsMap = new HashMap();
		paramsMap.put("tbUser", json.toString());

		SoapWebServiceUtil regSoap = new SoapWebServiceUtil("Member", "loginUser", paramsMap);
		SoapObject soapObject = regSoap.getRespondData();
		hashMap.put("code", soapObject.getPropertyAsString("code"));
		hashMap.put("userId", soapObject.getPropertyAsString("userId"));
		hashMap.put("userName", soapObject.getPropertyAsString("userName"));
		hashMap.put("isRememberMe", tbUser.isRememberMe());

		return hashMap;
	}

	private static JSONObject convertTbUser2JSONObject(TbUser tbUser) {
		JSONObject json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String result = objectMapper.writeValueAsString(tbUser);
			json = new JSONObject(result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static TbUser getUserInfo(long userId) throws AppException {
		HashMap paramsMap = new HashMap();
		paramsMap.put("loginUserId", userId);

		SoapWebServiceUtil memberSoap = new SoapWebServiceUtil("Member", "getUserInfo", paramsMap);
		SoapObject soapObject = memberSoap.getRespondData();

		TbUser tbUser = new TbUser();
		if (soapObject.hasProperty("userName")) {
			String userName = soapObject.getPropertyAsString("userName");
			String userEmail = soapObject.getPropertyAsString("userEmail");
			String userSex = soapObject.getPropertyAsString("userSex");
			int userProvinceId = Integer.parseInt(soapObject.getPropertyAsString("userProvinceId"));
			int userCityId = Integer.parseInt(soapObject.getPropertyAsString("userCityId"));
			String userSummary = soapObject.getPropertyAsString("userSummary").contains("anyType") == true ? "" : soapObject
					.getPropertyAsString("userSummary");
			String userQQ = soapObject.getPropertyAsString("userQQ").contains("anyType") == true ? "" : soapObject.getPropertyAsString("userQQ");

			tbUser.setUserName(userName);
			tbUser.setUserEmail(userEmail);
			tbUser.setUserSex(userSex);
			tbUser.setUserProvinceId(userProvinceId);
			tbUser.setUserCityId(userCityId);
			tbUser.setUserBirthday(StringUtils.toDate2(soapObject.getProperty("userBirthday").toString()));
			tbUser.setUserSummary(userSummary);
			tbUser.setUserQQ(userQQ);
		}
		return tbUser;
	}

	public static ArrayList<Map<String, Object>> getUserFavouriteForList(Context context, long userId, Paging p, boolean isRefresh)
			throws AppException {
		ArrayList<Map<String, Object>> favouriteList = new ArrayList<Map<String, Object>>();
		String key = "user_favouritelist_" + userId + "_" + p.getPage();
		AppContext appContext = (AppContext) context.getApplicationContext();
		if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
			HashMap params = new HashMap();
			params.put("userid", userId);
			params.put("maxresult", p.getCount());
			params.put("currentpage", p.getPage());

			SoapWebServiceUtil favouriteSoap = new SoapWebServiceUtil("Member", "getUserFavouriteForList", params);
			SoapObject soapObject = favouriteSoap.getRespondData();
			Log.i(TAG, soapObject.toString());

			for (int i = 0; i < soapObject.getPropertyCount(); i++) {
				Map<String, Object> newsTitle = new HashMap<String, Object>();
				SoapObject childSoapObject = (SoapObject) soapObject.getProperty(i);
				newsTitle.put("date", StringUtils.formatSoapDateTime(childSoapObject.getPropertyAsString("favouritelistCreatdate")));
				newsTitle.put("url", childSoapObject.getPropertyAsString("favouritelistUrl"));
				newsTitle.put("title", childSoapObject.getPropertyAsString("favouritelistTitle"));
				newsTitle.put("newsType", childSoapObject.getPropertyAsString("favouritelistNewstype"));

				favouriteList.add(newsTitle);
			}
			appContext.saveObject(favouriteList, key);
		} else {
			favouriteList = (ArrayList<Map<String, Object>>) appContext.readObject(key);
			if (favouriteList == null)
				favouriteList = new ArrayList<Map<String, Object>>();
		}
		return favouriteList;
	}

	public static int updateUserInfo(TbUser tbUser) throws AppException {
		JSONObject json = convertUser2JSONObject(tbUser);
		HashMap paramsMap = new HashMap();
		paramsMap.put("tbUser", json.toString());

		SoapWebServiceUtil memberSoap = new SoapWebServiceUtil("Member", "updateUserInfo", paramsMap);
		SoapObject soapObject = memberSoap.getRespondData();
		int code = Integer.parseInt(soapObject.getProperty("code").toString());

		return code;
	}

	private static JSONObject convertUser2JSONObject(TbUser tbUser) {
		JSONObject json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String result = objectMapper.writeValueAsString(tbUser);
			json = new JSONObject(result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

}
