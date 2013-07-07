package com.ideacode.news.test;

import android.test.AndroidTestCase;

import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.module.util.MemberUtil;

public class UserTest extends AndroidTestCase {

    private static final String TAG = "UserTest";

//    public void getUserCommentList() throws AppException{
//        long userId = new Long("121114232130");
//        Paging p = new Paging(1,20);
//        boolean isRefresh = true;
//        MemberUtil.getUserCommentList(this.getContext(),userId,p,isRefresh);
//    }

    public void getUserInfo() throws AppException {
        long userId = new Long("121114232130");
        MemberUtil.getUserInfo(userId);
    }
}
