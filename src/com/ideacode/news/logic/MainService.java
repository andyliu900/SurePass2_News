package com.ideacode.news.logic;

/**
 * <p>
 * FileName: MainService.java
 * </p>
 * <p>
 * Description: 后台总调度服务类
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
 *          CreatDate: 2012-9-7 上午11:35:56
 *          </p>
 *          <p>
 *          Modification History
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ideacode.news.app.AppException;
import com.ideacode.news.app.AppManager;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.NewsDetail;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.bean.TbFeedBack;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.module.util.AppStartUtil;
import com.ideacode.news.module.util.FeedBackUtil;
import com.ideacode.news.module.util.FindUtil;
import com.ideacode.news.module.util.MemberUtil;
import com.ideacode.news.module.util.MoodUtil;
import com.ideacode.news.module.util.NewsUtil;
import com.ideacode.news.module.util.RegUtil;

public class MainService extends Service implements Runnable {

    public static boolean isrun = false;
    private static ArrayList<Task> allTask = new ArrayList<Task>();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    // 添加窗口到集合中
    public static void addActivity(IdeaCodeActivity ia) {
        AppManager.getAppManager().addActivity(ia);
    }

    public static void removeActivity(IdeaCodeActivity ia) {
        AppManager.getAppManager().finishActivity(ia);
    }

    // 添加任务
    public static void newTask(Task ts) {
        allTask.add(ts);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isrun = false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        isrun = true;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isrun) {
            if (allTask.size() > 0) {
                doTask(allTask.get(0));
            } else {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
            }
        }
    }

    private void doTask(Task ts) {
        Message message = hand.obtainMessage();
        message.what = ts.getTaskID();
        switch (ts.getTaskID()) {
            case TaskType.TS_EXAM_GETINITIALIZEDATA: // 闪屏界面时，获取assset下面的初始化数据
                IdeaCodeActivity appStart = (IdeaCodeActivity) ts.getTaskParam().get("context");
                try {
                    AppStartUtil.getProvinces(appStart);
                } catch (Exception e) {
                    e.printStackTrace();
                    message.arg1 = CommonSetting.InitSystemDataException;
                }
                message.obj = null;
                break;
            case TaskType.TS_EXAM_SEARCH_NEWS: // 查询新闻
                try {
                    int newsType = (Integer) ts.getTaskParam().get("newsType");
                    int currentpage = (Integer) ts.getTaskParam().get("currentpage");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = NewsUtil.getNewsForList(this, newsType, currentpage, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_NEWS_MORE: // 查询更多新闻
                try {
                    int newsType = (Integer) ts.getTaskParam().get("newsType");
                    int currentpage = (Integer) ts.getTaskParam().get("currentpage");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = NewsUtil.getNewsForList(this, newsType, currentpage, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_NEWS_DETAIL_LOAD:// 查询新闻详细数据
                try {
                    int newsType = (Integer) ts.getTaskParam().get("newsType");
                    String newsDetail_url = (String) ts.getTaskParam().get("newsDetail_url");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    NewsDetail newsDetail = NewsUtil.getNewsDetailByUrl(this, newsType, newsDetail_url, isRefresh);
                    message.obj = newsDetail;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_NEWS_FAVOURITE: // 收藏新闻
                try {
                    long uid = (Long) ts.getTaskParam().get("uid");
                    NewsDetail newsDetail = (NewsDetail) ts.getTaskParam().get("newsDetail");
                    int code = NewsUtil.addFavouriteNews(uid, newsDetail);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_MOOD: // 查询心情
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = MoodUtil.getMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_MOOD_MORE: // 查询更多心情
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = MoodUtil.getMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEND_MOOD: // 发送心情
                try {
                    Mood mood = (Mood) ts.getTaskParam().get("mood");
                    int code = MoodUtil.addMood(mood);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_PRAISE_MOOD: // 顶一下心情
                try {
                    Mood mood = (Mood) ts.getTaskParam().get("mood");
                    int code = MoodUtil.addMoodPraise(mood);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_BELITTLE_MOOD: // 踩一下心情
                try {
                    Mood mood = (Mood) ts.getTaskParam().get("mood");
                    int code = MoodUtil.addMoodBelittle(mood);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_MOOD: // 查询用户心情
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> userMoodList = MoodUtil.getUserMoodForList(this, userId, p, isRefresh);
                    message.obj = userMoodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_MOOD_MORE: // 查询更多用户心情
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> userMoodList = MoodUtil.getUserMoodForList(this, userId, p, isRefresh);
                    message.obj = userMoodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE: // 查询用户收藏的新闻
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = MemberUtil.getUserFavouriteForList(this, userId, p, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE_MORE: // 查询更多用户收藏的新闻
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = MemberUtil.getUserFavouriteForList(this, userId, p, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_LOGIN: // 用户登录
                try {
                    TbUser tbUser = (TbUser) ts.getTaskParam().get("tbUser");
                    HashMap<String, Object> return_map = MemberUtil.login(tbUser);
                    message.obj = return_map;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_USER_INFO: // 获得用户信息
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    TbUser tbUser = MemberUtil.getUserInfo(userId);
                    message.obj = tbUser;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_UPDATEUSERINFO: // 更新用户信息
                try {
                    TbUser tbUser = (TbUser) ts.getTaskParam().get("tbUser");
                    int updateUser_code = MemberUtil.updateUserInfo(tbUser);
                    message.obj = updateUser_code;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_CHECKUSER: // 检查用户注册邮箱
                try {
                    String reg_name = (String) ts.getTaskParam().get("reg_name");
                    String reg_email = (String) ts.getTaskParam().get("reg_email");

                    int email_code = RegUtil.checkUser(reg_name, reg_email);
                    message.obj = email_code;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_REGUSER: // 注册用户
                try {
                    TbUser RegtbUser = (TbUser) ts.getTaskParam().get("tbUser");

                    Map reg_map = RegUtil.regUser(RegtbUser);
                    message.obj = reg_map;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SENDFEEDBACK: // 发送用户反馈信息
                try {
                    TbFeedBack tbFeedBack = (TbFeedBack) ts.getTaskParam().get("feedback");
                    FeedBackUtil.sendFeedBackInfo(tbFeedBack);
                    message.obj = CommonSetting.Success;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_MOOD: // 查询热门心情
                try {
                    Paging p = (Paging)ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = FindUtil.getPopMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_MOOD_MORE: // 查询更多热门心情
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = FindUtil.getPopMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE: // 查询热门收藏
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> favouriteList = FindUtil.getPopFavouriteForList(this, p, isRefresh);
                    message.obj = favouriteList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE_MORE: // 查询更多热门收藏
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> favouriteList = FindUtil.getPopFavouriteForList(this, p, isRefresh);
                    message.obj = favouriteList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
        }
        allTask.remove(ts);
        hand.sendMessage(message);
    }

    private final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TaskType.TS_EXAM_GETINITIALIZEDATA: // 闪屏界面时，获取assset下面的初始化数据
                    AppManager.getAppManager().getActivityByName("AppStart").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_NEWS: // 查询新闻
                    AppManager.getAppManager().getActivityByName("TabNewsActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_NEWS_MORE: // 查询更多新闻
                    AppManager.getAppManager().getActivityByName("TabNewsActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_NEWS_DETAIL_LOAD: // 查询新闻详细数据
                    if (AppManager.getAppManager().getActivityByName("NewsDetailActivity") != null) {
                        AppManager.getAppManager().getActivityByName("NewsDetailActivity").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_NEWS_FAVOURITE: // 收藏新闻
                    if (AppManager.getAppManager().getActivityByName("NewsDetailActivity") != null) {
                        AppManager.getAppManager().getActivityByName("NewsDetailActivity").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_SEARCH_MOOD: // 查询心情
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_MOOD_MORE: // 查询更多心情
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEND_MOOD: // 发送心情
                    if (AppManager.getAppManager().getActivityByName("AddMoodActivity") != null) {
                        AppManager.getAppManager().getActivityByName("AddMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_PRAISE_MOOD: // 顶一下心情
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_BELITTLE_MOOD: // 踩一下心情
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_MOOD: // 查询用户心情
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_MOOD_MORE: // 查询更多用户心情
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE: // 查询用户收藏的新闻
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE_MORE: // 查询更多用户收藏的新闻
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_LOGIN: // 用户登录
                    AppManager.getAppManager().getActivityByName("LoginDialog").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_USER_INFO:// 获取用户信息
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_UPDATEUSERINFO: // 更新用户信息
                    AppManager.getAppManager().getActivityByName("LoadingActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_CHECKUSER: // 检查用户注册邮箱
                    AppManager.getAppManager().getActivityByName("LoadingActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_REGUSER: // 注册用户
                    AppManager.getAppManager().getActivityByName("LoadingActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SENDFEEDBACK: // 发送用户反馈信息
                    if (AppManager.getAppManager().getActivityByName("FeedBack") != null) {
                        AppManager.getAppManager().getActivityByName("FeedBack").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_MOOD: // 查询热门心情
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_MOOD_MORE: // 查询更多热门心情
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE: // 查询热门收藏
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE_MORE: // 查询更多热门收藏
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
            }
        };
    };
}
