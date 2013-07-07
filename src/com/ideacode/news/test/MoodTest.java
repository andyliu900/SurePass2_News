package com.ideacode.news.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import android.util.Log;

import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.NewsDetail;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.module.util.MoodUtil;
import com.ideacode.news.module.util.NewsUtil;

public class MoodTest extends AndroidTestCase {

	private static final String TAG = "MoodTest";

	public void getMoodList() throws Exception {
		int nowpage = 1;
		int pagesize = 10;
		Paging p = new Paging(nowpage, pagesize);
        ArrayList<Mood> list = MoodUtil.getMoodForList(this.getContext(), p, true);
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
		int code = NewsUtil.addFavouriteNews(uid, newsDetail);
		Log.i(TAG, code + "");
	}
}
