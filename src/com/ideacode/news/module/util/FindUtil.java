package com.ideacode.news.module.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.util.Log;

import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.net.SoapWebServiceUtil;

public class FindUtil {

    private static final String TAG = "FindUtil";

    public static ArrayList<Mood> getPopMoodForList(Context context, Paging p, boolean isRefresh) throws AppException {
        ArrayList<Mood> moodList = new ArrayList<Mood>();
        String key = "pop_moodlist_" + p.getPage();
        AppContext appContext = (AppContext) context.getApplicationContext();
        if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
            HashMap params = new HashMap();
            params.put("maxresult", p.getCount());
            params.put("currentpage", p.getPage());

            SoapWebServiceUtil moodSoap = new SoapWebServiceUtil("Find", "getPopMoodForList", params);
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
                mood.setHeatFlag(StringUtils.formatBoolean(childSoapObject.getPropertyAsString("heatFlag")));

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

    public static ArrayList<Map<String, Object>> getPopFavouriteForList(Context context, Paging p, boolean isRefresh) throws AppException {
        ArrayList<Map<String, Object>> favouriteList = new ArrayList<Map<String, Object>>();
        String key = "pop_favouritelist_" + p.getPage();
        AppContext appContext = (AppContext) context.getApplicationContext();
        if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
            HashMap params = new HashMap();
            params.put("maxresult", p.getCount());
            params.put("currentpage", p.getPage());
            SoapWebServiceUtil favouriteSoap = new SoapWebServiceUtil("Find", "getPopFavouriteForList", params);
            SoapObject soapObject = favouriteSoap.getRespondData();
            Log.i(TAG, soapObject.toString());

            for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                Map<String, Object> newsTitle = new HashMap<String, Object>();
                SoapObject childSoapObject = (SoapObject) soapObject.getProperty(i);
                newsTitle.put("date", StringUtils.formatSoapDateTime(childSoapObject.getPropertyAsString("favouritelistCreatdate")));
                newsTitle.put("url", childSoapObject.getPropertyAsString("favouritelistUrl"));
                newsTitle.put("title", childSoapObject.getPropertyAsString("favouritelistTitle"));
                newsTitle.put("newsType", childSoapObject.getPropertyAsString("favouritelistNewstype"));
                newsTitle.put("heatFlag", StringUtils.formatBoolean(childSoapObject.getPropertyAsString("heatFlag")));
                
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

}
