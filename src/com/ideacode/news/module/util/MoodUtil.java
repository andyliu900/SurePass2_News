package com.ideacode.news.module.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.util.Log;

import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.net.SoapWebServiceUtil;

public class MoodUtil {

	private static final String TAG = "MoodUtil";

	public static ArrayList<Mood> getMoodForList(Context context, Paging p, boolean isRefresh) throws AppException {
		ArrayList<Mood> moodList = new ArrayList<Mood>();
		String key = "moodlist_" + p.getPage();
		AppContext appContext = (AppContext) context.getApplicationContext();
		if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
			HashMap params = new HashMap();
			params.put("maxresult", p.getCount());
			params.put("currentpage", p.getPage());

			SoapWebServiceUtil moodSoap = new SoapWebServiceUtil("Mood", "getMoodForList", params);
			SoapObject soapObject = moodSoap.getRespondData();
			Log.i(TAG, soapObject.toString());

			for (int i = 0; i < soapObject.getPropertyCount(); i++) {
				Mood mood = new Mood();
				SoapObject childSoapObject = (SoapObject) soapObject.getProperty(i);
				mood.setMoodId(Integer.parseInt(childSoapObject.getPropertyAsString("moodId")));
				mood.setUserId(Long.parseLong(childSoapObject.getPropertyAsString("userId")));
				mood.setUserName(childSoapObject.getPropertyAsString("userName"));
				mood.setMoodContent(childSoapObject.getPropertyAsString("moodContent"));
				mood.setMoodPraiseCount(Integer.parseInt(childSoapObject.getPropertyAsString("moodPraiseCount")));
				mood.setMoodBelittleCount(Integer.parseInt(childSoapObject.getPropertyAsString("moodBelittleCount")));
				mood.setMoodLocation(StringUtils.formatSoapNullString(childSoapObject.getPropertyAsString("moodLocation")));
				mood.setMoodCreateDate(StringUtils.formatSoapDateTime(childSoapObject.getPropertyAsString("moodCreattime")));

				moodList.add(mood);
			}
			appContext.saveObject(moodList, key);
		} else {
			moodList = (ArrayList<Mood>) appContext.readObject(key);
			if (moodList == null)
				moodList = new ArrayList<Mood>();
		}
		return moodList;
	}

	public static int addMood(Mood mood) throws AppException {
		JSONObject moodJson = convertMood2JSONObject(mood);
		HashMap params = new HashMap();
		params.put("moodJson", moodJson.toString());

		SoapWebServiceUtil memberSoap = new SoapWebServiceUtil("Mood", "addMood", params);
		Object soapObject = memberSoap.getObjectRespondData();
		int code = Integer.parseInt(soapObject.toString());

		return code;
	}

	public static int addMoodPraise(Mood mood) throws AppException {
		JSONObject moodJson = convertMood2JSONObject(mood);
		HashMap params = new HashMap();
		params.put("moodJson", moodJson.toString());

		SoapWebServiceUtil memberSoap = new SoapWebServiceUtil("Mood", "addMoodPraise", params);
		Object soapObject = memberSoap.getObjectRespondData();
		int code = Integer.parseInt(soapObject.toString());

		return code;
	}

	public static int addMoodBelittle(Mood mood) throws AppException {
		JSONObject moodJson = convertMood2JSONObject(mood);
		HashMap params = new HashMap();
		params.put("moodJson", moodJson.toString());

		SoapWebServiceUtil memberSoap = new SoapWebServiceUtil("Mood", "addMoodBelittle", params);
		Object soapObject = memberSoap.getObjectRespondData();
		int code = Integer.parseInt(soapObject.toString());

		return code;
	}

	public static ArrayList<Mood> getUserMoodForList(Context context, long userId, Paging p, boolean isRefresh) throws AppException {
		ArrayList<Mood> moodList = new ArrayList<Mood>();
		String key = "user_moodlist_" + userId + "_" + p.getPage();
		AppContext appContext = (AppContext) context.getApplicationContext();
		if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
			HashMap params = new HashMap();
			params.put("userid", userId);
			params.put("maxresult", p.getCount());
			params.put("currentpage", p.getPage());

			SoapWebServiceUtil moodSoap = new SoapWebServiceUtil("Mood", "getUserMoodForList", params);
			SoapObject soapObject = moodSoap.getRespondData();
			Log.i(TAG, soapObject.toString());

			for (int i = 0; i < soapObject.getPropertyCount(); i++) {
				Mood mood = new Mood();
				SoapObject childSoapObject = (SoapObject) soapObject.getProperty(i);
				mood.setMoodId(Integer.parseInt(childSoapObject.getPropertyAsString("moodId")));
				mood.setUserId(Long.parseLong(childSoapObject.getPropertyAsString("userId")));
				mood.setUserName(childSoapObject.getPropertyAsString("userName"));
				mood.setMoodContent(childSoapObject.getPropertyAsString("moodContent"));
				mood.setMoodPraiseCount(Integer.parseInt(childSoapObject.getPropertyAsString("moodPraiseCount")));
				mood.setMoodBelittleCount(Integer.parseInt(childSoapObject.getPropertyAsString("moodBelittleCount")));
				mood.setMoodLocation(StringUtils.formatSoapNullString(childSoapObject.getPropertyAsString("moodLocation")));
				mood.setMoodCreateDate(StringUtils.formatSoapDateTime(childSoapObject.getPropertyAsString("moodCreattime")));

				moodList.add(mood);
			}
			appContext.saveObject(moodList, key);
		} else {
			moodList = (ArrayList<Mood>) appContext.readObject(key);
			if (moodList == null)
				moodList = new ArrayList<Mood>();
		}
		return moodList;
	}

	private static JSONObject convertMood2JSONObject(Mood mood) {
		JSONObject json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String result = objectMapper.writeValueAsString(mood);
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
