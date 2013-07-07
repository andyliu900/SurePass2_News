package com.ideacode.news.module.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.NewsDetail;
import com.ideacode.news.net.SoapWebServiceUtil;
import com.ideacode.news.net.Tools;

public class NewsUtil {
	private static final String TAG = "NewsUtil";

	/**
	 * 获得新闻列表
	 * 
	 * @param context
	 * @param newsType
	 * @param currentpage
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<Map<String, Object>> getNewsForList(Context context, int newsType, int currentpage, boolean isRefresh)
			throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String key = "newslist_" + newsType + "_" + currentpage;
		AppContext appContext = (AppContext) context.getApplicationContext();
		if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
			HashMap params = new HashMap();
			params.put("newstype", newsType);
			params.put("currentpage", currentpage);

			SoapWebServiceUtil announceSoap = new SoapWebServiceUtil("News", "getNewsListUrl", params);
			Object object = announceSoap.getObjectRespondData();
			Log.i(TAG, object.toString());
			if (!object.toString().equals("anyType{}")) {
				switch (newsType) {
				case AppContext.NEWSTYPE_FOCUS:
					list = parseForFocus(newsType, object.toString(), AppContext.GB2312);
					break;
				case AppContext.NEWSTYPE_CIVIL:
					list = parseForCivil(newsType, object.toString(), AppContext.UTF_8);
					break;
				case AppContext.NEWSTYPE_WORK:
					list = parseForWork(newsType, object.toString(), AppContext.GB2312);
					break;
				case AppContext.NEWSTYPE_COLLEGEEXAM:
					list = parseForCollegeExam(newsType, object.toString(), AppContext.GB2312);
					break;
				case AppContext.NEWSTYPE_GRADUATE:
					list = parseForGraduteExam(newsType, object.toString(), AppContext.GB2312);
					break;
				case AppContext.NEWSTYPE_CHINA:
					list = parseForChina(newsType, object.toString(), AppContext.GB2312);
					break;
				case AppContext.NEWSTYPE_WORLD:
					list = parseForWorld(newsType, object.toString(), AppContext.GB2312);
					break;
				}
				appContext.saveObject(list, key);
			}
		} else {
			list = (ArrayList<Map<String, Object>>) appContext.readObject(key);
			if (list == null)
				list = new ArrayList<Map<String, Object>>();
		}

		return list;
	}

	/**
	 * 解析焦点新闻标题html
	 * 
	 * @param newsUrl
	 * @param enCodeType
	 * @return
	 */
	private static ArrayList<Map<String, Object>> parseForFocus(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Elements uls = doc.select("ul[class=list_f14d]");
		for (Element ul : uls) {
			Elements lis = ul.select("li");
			for (Element li : lis) {
				Map<String, Object> newsTitle = new HashMap<String, Object>();
				Element span = li.select("span[class=right]").first();
				newsTitle.put("date", span.text() + ":00");
				Element a = li.select("a[href]").first();
				newsTitle.put("url", a.attr("href"));
				newsTitle.put("title", a.text());
				newsTitle.put("newsType", newsType);

				list.add(newsTitle);
			}
		}
		return list;
	}

	/**
	 * 解析公务员新闻标题html
	 * 
	 * @param newsType
	 * @param newsUrl
	 * @param enCodeType
	 * @return
	 */
	private static ArrayList<Map<String, Object>> parseForCivil(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element ul = doc.select("ul[class=list01]").first();
		Elements lis = ul.select("li");
		for (Element li : lis) {
			Map<String, Object> newsTitle = new HashMap<String, Object>();
			Element span = li.select("span").first();
			if (span != null) {
				newsTitle.put("date", span.text() + " 00:00:00");
				Element a = li.select("a[target]").first();
				newsTitle.put("url", a.attr("href"));
				newsTitle.put("title", a.text());
				newsTitle.put("newsType", newsType);

				list.add(newsTitle);
			}
		}
		return list;
	}

	/**
	 * 解析就业新闻标题html
	 * 
	 * @param newsType
	 * @param newsUrl
	 * @param enCodeType
	 * @return
	 * @throws AppException
	 */
	private static ArrayList<Map<String, Object>> parseForWork(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div = doc.getElementById("sksb");
		Elements lis = div.select("li");
		for (Element li : lis) {
			Map<String, Object> newsTitle = new HashMap<String, Object>();
			Element a = li.select("a").first();
			newsTitle.put("title", a.text());
			newsTitle.put("url", a.attr("href"));
			Element span = li.select("span").first();
			newsTitle.put("date", "2013-" + span.text() + " 00:00:00");
			newsTitle.put("newsType", newsType);
			list.add(newsTitle);
		}
		return list;
	}

	/**
	 * 解析高考新闻标题html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static ArrayList<Map<String, Object>> parseForCollegeExam(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div = doc.getElementById("sksb");
		Elements lis = div.select("li");
		for (Element li : lis) {
			Map<String, Object> newsTitle = new HashMap<String, Object>();
			Element a = li.select("a").first();
			newsTitle.put("title", a.text());
			newsTitle.put("url", a.attr("href"));
			Element span = li.select("span").first();
			newsTitle.put("date", span.text().replace("(", "").replace(")", "").replace(":", "-") + " 00:00:00");
			newsTitle.put("newsType", newsType);
			list.add(newsTitle);
		}
		return list;
	}

	/**
	 * 解析考研新闻标题html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static ArrayList<Map<String, Object>> parseForGraduteExam(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div = doc.getElementById("sksb");
		Elements lis = div.select("li");
		for (Element li : lis) {
			Map<String, Object> newsTitle = new HashMap<String, Object>();
			Element a = li.select("a").first();
			newsTitle.put("title", a.text());
			newsTitle.put("url", a.attr("href"));
			Element span = li.select("span").first();
			newsTitle.put("date", span.text() + " 00:00:00");
			newsTitle.put("newsType", newsType);
			list.add(newsTitle);
		}
		return list;
	}

	/**
	 * 解析国内新闻标题html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static ArrayList<Map<String, Object>> parseForChina(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div = doc.getElementById("sksb");
		Elements lis = div.select("li");
		for (Element li : lis) {
			Map<String, Object> newsTitle = new HashMap<String, Object>();
			Element a = li.select("a").first();
			newsTitle.put("title", a.text());
			newsTitle.put("url", a.attr("href"));
			Element span = li.select("span").first();
			newsTitle.put("date", "2013-" + span.text() + " 00:00:00");
			newsTitle.put("newsType", newsType);
			list.add(newsTitle);
		}
		return list;
	}

	private static ArrayList<Map<String, Object>> parseForWorld(int newsType, String newsUrl, String enCodeType) throws AppException {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String html_Data = "";
		html_Data = Tools.getHtmlData(newsUrl, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div = doc.getElementById("sksb");
		Elements lis = div.select("li");
		for (Element li : lis) {
			Map<String, Object> newsTitle = new HashMap<String, Object>();
			Element a = li.select("a").first();
			newsTitle.put("title", a.text());
			newsTitle.put("url", a.attr("href"));
			Element span = li.select("span").first();
			newsTitle.put("date", span.text() + " 00:00:00");
			newsTitle.put("newsType", newsType);
			list.add(newsTitle);
		}
		return list;
	}

	/**
	 * 获得新闻详细新闻内容
	 * 
	 * @param context
	 * @param newsType
	 * @param newsDetail_url
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public static NewsDetail getNewsDetailByUrl(Context context, int newsType, String newsDetail_url, boolean isRefresh) throws AppException {
		Log.i(TAG, newsDetail_url);
		NewsDetail newsDetail = null;
		try {
			String newsId = newsDetail_url.substring(newsDetail_url.lastIndexOf("/") + 1, newsDetail_url.lastIndexOf("."));
			String key = "newsdetail_" + newsType + "_" + newsId;
			AppContext appContext = (AppContext) context.getApplicationContext();
			if (appContext.isNetworkConnected() && (!appContext.isReadDataCache(key) || isRefresh)) {
				if (!newsDetail_url.equals("")) {
					switch (newsType) {
					case AppContext.NEWSTYPE_FOCUS:
						newsDetail = parseForFocusDetail(newsDetail_url, AppContext.GB2312, newsType);
						break;
					case AppContext.NEWSTYPE_CIVIL:
						newsDetail = parseForCivilDetail(newsDetail_url, AppContext.UTF_8, newsType);
						break;
					case AppContext.NEWSTYPE_WORK:
						newsDetail = parseForWorkDetail(newsDetail_url, AppContext.GB2312, newsType);
						break;
					case AppContext.NEWSTYPE_COLLEGEEXAM:
						newsDetail = parseForCollageExamDetail(newsDetail_url, AppContext.GB2312, newsType);
						break;
					case AppContext.NEWSTYPE_GRADUATE:
						newsDetail = parseForGraduateDetail(newsDetail_url, AppContext.GB2312, newsType);
						break;
					case AppContext.NEWSTYPE_CHINA:
						newsDetail = parseForChinaDetail(newsDetail_url, AppContext.GB2312, newsType);
						break;
					case AppContext.NEWSTYPE_WORLD:
						newsDetail = parseForWorldDetail(newsDetail_url, AppContext.GB2312, newsType);
						break;
					}
					appContext.saveObject(newsDetail, key);
				}
			} else {
				newsDetail = (NewsDetail) appContext.readObject(key);
				if (newsDetail == null)
					newsDetail = new NewsDetail();
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw AppException.StringIndexOutOfBoundsException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析焦点新闻详细新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForFocusDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(newsDetail_url);
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(newsDetail_url, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		try {
			Element h1 = doc.getElementById("h1title");
			newsDetail.setNewsDetailsTitle(h1.text());

			Element div = doc.select("div[class=ep-info cDGray]").first();
			Element span = doc.select("span[class=info]").first();
			if (div == null) {
				newsDetail.setNewsDetailsCreateDate(span.text().substring(0, 19));
				Element a = span.select("a").first();
				newsDetail.setNewsDetailsAuthor(a.text());
			} else {
				newsDetail.setNewsDetailsCreateDate(div.text().substring(0, 19));
				Element a = div.select("a").first();
				newsDetail.setNewsDetailsAuthor(a.text());
			}

			StringBuffer endText = new StringBuffer();
			Element content = doc.getElementById("endText");
			Elements ps = content.select("p");
			for (Element p : ps) {
				Elements imgs = p.select("img");
				if (!imgs.isEmpty()) {
					for (Element img : imgs) {
						endText.append("<p>");
						endText.append("<img border=\"0\" ");
						endText.append("src=\"" + img.attr("src") + "\" />");
						endText.append("</p>");
					}
				}
				endText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				endText.append(p.text());
				endText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(endText.toString());

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析公务员新闻详细新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForCivilDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(newsDetail_url);
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(newsDetail_url, enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div;
		try {
			div = doc.select("div[class=c_l_c]").first();
			Element title = div.select("div[class=c_l_c_2]").first();
			newsDetail.setNewsDetailsTitle(title.text());
			Element div2 = div.select("div[class=c_l_c_3]").first();
			String dateAndAuthor = div2.text();
			newsDetail.setNewsDetailsCreateDate(dateAndAuthor.substring(dateAndAuthor.indexOf("2"), dateAndAuthor.indexOf("2") + 10).trim()
					+ " 00:00:00");
			newsDetail.setNewsDetailsAuthor(dateAndAuthor.substring(dateAndAuthor.indexOf("源") + 2, dateAndAuthor.length()));

			StringBuffer contentText = new StringBuffer();
			Element content = doc.getElementById("Zoom");
			Elements ps = content.select("p");
			for (Element p : ps) {
				contentText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				contentText.append(p.text());
				contentText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(contentText.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析就业新闻详细新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForWorkDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(AppContext.JOB_JYB_CN + newsDetail_url.substring(1));
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(AppContext.JOB_JYB_CN + newsDetail_url.substring(1), enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div;
		try {
			div = doc.getElementById("body");
			Element h1 = div.select("h1").first();
			newsDetail.setNewsDetailsTitle(h1.text());
			Element h2 = div.select("h2").first();
			String h2text = h2.text();
			String tempdate = h2text.substring(h2text.indexOf("2"), h2text.indexOf("2") + 10);
			tempdate = tempdate.replace("年", "-").replace("月", "-").replace("日", "") + " 00:00:00";
			newsDetail.setNewsDetailsCreateDate(tempdate);
			newsDetail.setNewsDetailsAuthor(h2text.substring(h2text.indexOf("源") + 2, h2text.length()));

			StringBuffer contentText = new StringBuffer();
			Elements ps = div.select("p");
			for (Element p : ps) {
				Elements imgs = p.select("img");
				if (!imgs.isEmpty()) {
					for (Element img : imgs) {
						contentText.append("<p>");
						contentText.append("<img border=\"0\" ");
						contentText.append("src=\"" + newsDetail_url.substring(0, newsDetail_url.lastIndexOf("/")) + img.attr("src").substring(2)
								+ "\" />");
						contentText.append("</p>");
					}
				}
				contentText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				contentText.append(p.text());
				contentText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(contentText.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析高考新闻详细新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForCollageExamDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(AppContext.GAOKAO_JYB_CN + newsDetail_url.substring(1));
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(AppContext.GAOKAO_JYB_CN + newsDetail_url.substring(1), enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div;
		try {
			div = doc.getElementById("body");
			Element h1 = div.select("h1").first();
			newsDetail.setNewsDetailsTitle(h1.text());
			Element h2 = div.select("h2").first();
			String h2text = h2.text();
			String tempdate = h2text.substring(h2text.indexOf("2"), h2text.indexOf("2") + 10);
			tempdate = tempdate.replace("年", "-").replace("月", "-").replace("日", "") + " 00:00:00";
			newsDetail.setNewsDetailsCreateDate(tempdate);
			newsDetail.setNewsDetailsAuthor(h2text.substring(h2text.indexOf("源") + 2, h2text.length()));

			StringBuffer contentText = new StringBuffer();
			Elements ps = div.select("p");
			for (Element p : ps) {
				Elements imgs = p.select("img");
				if (!imgs.isEmpty()) {
					for (Element img : imgs) {
						contentText.append("<p>");
						contentText.append("<img border=\"0\" ");
						contentText.append("src=\"" + newsDetail_url.substring(0, newsDetail_url.lastIndexOf("/")) + img.attr("src").substring(2)
								+ "\" />");
						contentText.append("</p>");
					}
				}
				contentText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				contentText.append(p.text());
				contentText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(contentText.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析考研新闻详细新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForGraduateDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(AppContext.KAOYAN_JYB_CN + newsDetail_url.substring(1));
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(AppContext.KAOYAN_JYB_CN + newsDetail_url.substring(1), enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div;
		try {
			div = doc.getElementById("body");
			Element h1 = div.select("h1").first();
			newsDetail.setNewsDetailsTitle(h1.text());
			Element h2 = div.select("h2").first();
			String h2text = h2.text();
			String tempdate = h2text.substring(h2text.indexOf("2"), h2text.indexOf("2") + 10);
			tempdate = tempdate.replace("年", "-").replace("月", "-").replace("日", "") + " 00:00:00";
			newsDetail.setNewsDetailsCreateDate(tempdate);
			newsDetail.setNewsDetailsAuthor(h2text.substring(h2text.indexOf("源") + 2, h2text.length()));

			StringBuffer contentText = new StringBuffer();
			Elements ps = div.select("p");
			for (Element p : ps) {
				Elements imgs = p.select("img");
				if (!imgs.isEmpty()) {
					for (Element img : imgs) {
						contentText.append("<p>");
						contentText.append("<img border=\"0\" ");
						contentText.append("src=\"" + AppContext.KAOYAN_JYB_CN + newsDetail_url.substring(1, newsDetail_url.lastIndexOf("/"))
								+ img.attr("src").substring(1) + "\" />");
						contentText.append("</p>");
					}
				}
				contentText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				contentText.append(p.text());
				contentText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(contentText.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析国内新闻详情新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForChinaDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(AppContext.CHINA_JYB_CN + newsDetail_url.substring(2));
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(AppContext.CHINA_JYB_CN + newsDetail_url.substring(2), enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div;
		try {
			div = doc.getElementById("body");
			Element h1 = div.select("h1").first();
			newsDetail.setNewsDetailsTitle(h1.text());
			Element h2 = div.select("h2").first();
			String h2text = h2.text();
			String tempdate = h2text.substring(h2text.indexOf("2"), h2text.indexOf("2") + 10);
			tempdate = tempdate.replace("年", "-").replace("月", "-").replace("日", "") + " 00:00:00";

			newsDetail.setNewsDetailsCreateDate(tempdate);
			newsDetail.setNewsDetailsAuthor(h2text.substring(h2text.indexOf("源") + 2, h2text.length()));

			StringBuffer contentText = new StringBuffer();
			Elements ps = div.select("p");
			for (Element p : ps) {
				Elements imgs = p.select("img");
				if (!imgs.isEmpty()) {
					for (Element img : imgs) {
						contentText.append("<p>");
						contentText.append("<img border=\"0\" ");
						contentText.append("src=\"" + AppContext.CHINA_JYB_CN + newsDetail_url.substring(1, newsDetail_url.lastIndexOf("/"))
								+ img.attr("src").substring(1) + "\" />");
						contentText.append("</p>");
					}
				}
				contentText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				contentText.append(p.text());
				contentText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(contentText.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 解析国际新闻详情新闻内容html
	 * 
	 * @param newsDetail_url
	 * @param enCodeType
	 * @param newsType
	 * @return
	 * @throws AppException
	 */
	private static NewsDetail parseForWorldDetail(String newsDetail_url, String enCodeType, int newsType) throws AppException {
		NewsDetail newsDetail = new NewsDetail();
		String html_Data = "";
		newsDetail.setNewsDetailsUrl(AppContext.WORLD_JYB_CN + newsDetail_url.substring(1));
		newsDetail.setNewsType(newsType);
		html_Data = Tools.getHtmlData(AppContext.WORLD_JYB_CN + newsDetail_url.substring(1), enCodeType);
		Log.i(TAG, html_Data);

		Document doc = Jsoup.parse(html_Data);
		Element div;
		try {
			div = doc.getElementById("body");
			Element h1 = div.select("h1").first();
			newsDetail.setNewsDetailsTitle(h1.text());
			Element h2 = div.select("h2").first();
			String h2text = h2.text();
			String tempdate = h2text.substring(h2text.indexOf("2"), h2text.indexOf("2") + 10);
			tempdate = tempdate.replace("年", "-").replace("月", "-").replace("日", "") + " 00:00:00";

			newsDetail.setNewsDetailsCreateDate(tempdate);
			newsDetail.setNewsDetailsAuthor(h2text.substring(h2text.indexOf("源") + 2, h2text.length()));

			StringBuffer contentText = new StringBuffer();
			Elements ps = div.select("p");
			for (Element p : ps) {
				Elements imgs = p.select("img");
				if (!imgs.isEmpty()) {
					for (Element img : imgs) {
						contentText.append("<p>");
						contentText.append("<img border=\"0\" ");
						contentText.append("src=\"" + AppContext.WORLD_JYB_CN + newsDetail_url.substring(1, newsDetail_url.lastIndexOf("/"))
								+ img.attr("src").substring(1) + "\" />");
						contentText.append("</p>");
					}
				}
				contentText.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;");
				contentText.append(p.text());
				contentText.append("</p>");
			}
			newsDetail.setNewsDetailsBody(contentText.toString());
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw AppException.nullPointerException(e);
		}
		return newsDetail;
	}

	/**
	 * 添加新闻值用户收藏夹
	 * 
	 * @param uid
	 * @param newsDetail
	 * @return
	 * @throws AppException
	 */
	public static int addFavouriteNews(long uid, NewsDetail newsDetail) throws AppException {
		JSONObject newsDetailJson = convertNewsDetail2JSONObject(newsDetail);
		HashMap params = new HashMap();
		params.put("uid", uid);
		params.put("newsDetailJson", newsDetailJson.toString());

		SoapWebServiceUtil memberSoap = new SoapWebServiceUtil("News", "addFavouriteNews", params);
		Object soapObject = memberSoap.getObjectRespondData();
		int code = Integer.parseInt(soapObject.toString());

		return code;
	}

	/**
	 * 将NewsDetail对象转换成json对象
	 * 
	 * @param newsDetail
	 * @return
	 */
	private static JSONObject convertNewsDetail2JSONObject(NewsDetail newsDetail) {
		JSONObject json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String result = objectMapper.writeValueAsString(newsDetail);
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
