package com.ideacode.news.test;

import java.util.ArrayList;
import java.util.Map;

import android.test.AndroidTestCase;
import android.util.Log;

import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.NewsDetail;
import com.ideacode.news.module.util.NewsUtil;

public class NewsTest extends AndroidTestCase {

    private static final String TAG = "NewsTest";

    public void getNewsList() throws Exception {
        int currentpage = 1;
        ArrayList<Map<String,Object>> list = NewsUtil.getNewsForList(this.getContext(), AppContext.NEWSTYPE_FOCUS,currentpage,
                true);
        Log.i(TAG, list.size() + "");
    }

    public void addFavouriteNews() throws Exception {
        long uid = new Long("121216210058");
        NewsDetail newsDetail = new NewsDetail();
        newsDetail.setNewsType(0);
        newsDetail.setNewsDetailsTitle("标题");
        newsDetail.setNewsDetailsAuthor("作者");
        newsDetail.setNewsDetailsCreateDate("2012-05-10 10:23:11");
        newsDetail.setNewsDetailsUrl("http://www.baidu.com");
        int code = NewsUtil.addFavouriteNews(uid,newsDetail);
        Log.i(TAG, code+"");
    }
}
