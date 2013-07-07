package com.ideacode.news.logic;

/**
 * 任务类型
 * 
 * @author Vic Su
 * 
 *         2013-4-26下午2:06:47
 */
public class TaskType {

	public static final int TS_EXAM_SEARCH_INIT = 0; // 初始化查询
	public static final int TS_EXAM_SEARCH_NEWS = 1; // 查询新闻
	public static final int TS_EXAM_SEARCH_NEWS_MORE = 2; // 查询更多新闻
	public static final int TS_EXAM_SEARCH_NEWS_DETAIL_LOAD = 3;// 查询详细新闻信息
	public static final int TS_EXAM_NEWS_FAVOURITE = 4; // 收藏新闻

	public static final int TS_EXAM_SEARCH_MOOD = 5; // 查询心情
	public static final int TS_EXAM_SEARCH_MOOD_MORE = 6; // 查询更多心情
	public static final int TS_EXAM_SEND_MOOD = 7; // 发表心情
	public static final int TS_EXAM_PRAISE_MOOD = 8; // 顶一下心情
	public static final int TS_EXAM_BELITTLE_MOOD = 9; // 踩一下心情
	public static final int TS_EXAM_SEARCH_USER_MOOD = 10; // 查询用户心情
	public static final int TS_EXAM_SEARCH_USER_MOOD_MORE = 11; // 查询更多用户心情
	public static final int TS_EXAM_SEARCH_USER_FAVOURITE = 12; // 查询用户收藏的新闻
	public static final int TS_EXAM_SEARCH_USER_FAVOURITE_MORE = 13; // 查询更多用户收藏的新闻

	public static final int TS_EXAM_LOGIN = 14; // 用户登录
	public static final int TS_EXAM_USER_INFO = 15; // 获得用户信息
	public static final int TS_EXAM_UPDATEUSERINFO = 16; // 更新用户信息
	public static final int TS_EXAM_CHANGEUSEREMAIL = 17; // 更新用户email
	public static final int TS_EXAM_CHANGEUSERSUMMARY = 18; // 更新用户简介
	public static final int TS_EXAM_CHANGEQQ = 19; // 更新用户QQ
	public static final int TS_EXAM_UPDATEUSERINFO_SUCCESS = 20; // 更新用户信息成功
	public static final int TS_EXAM_REGUSER = 21; // 注册用户
    public static final int TS_EXAM_CHECKUSER = 22; // 检查用户注册邮箱
    public static final int TS_EXAM_SENDFEEDBACK = 23; // 发送用户反馈信息

    public static final int TS_EXAM_SEARCH_POP_MOOD = 24; // 查询热门心情
    public static final int TS_EXAM_SEARCH_POP_MOOD_MORE = 25; // 查询更多热门心情
    public static final int TS_EXAM_SEARCH_POP_FAVOURITE = 26; // 查询热门收藏
    public static final int TS_EXAM_SEARCH_POP_FAVOURITE_MORE = 27; // 查询更多热门收藏

	public static final int TS_EXAM_GETINITIALIZEDATA = 50; // 闪屏界面时，获取assset下面的初始化数据

}
