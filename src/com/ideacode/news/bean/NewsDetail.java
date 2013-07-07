package com.ideacode.news.bean;

/**
 * NewsDetail ÊµÌå
 * @author Simon Xu
 *
 * 2013-4-19ÏÂÎç2:44:13
 */
public class NewsDetail extends Entity{

    private String newsDetailsTitle;
    private String newsDetailsUrl;
    private String newsDetailsBody;
    private String newsDetailsAuthor;
    private String newsDetailsCreateDate;
    private int newsType;

    public NewsDetail(){}

    public String getNewsDetailsTitle() {
        return newsDetailsTitle;
    }

    public void setNewsDetailsTitle(String newsDetailsTitle) {
        this.newsDetailsTitle = newsDetailsTitle;
    }

    public String getNewsDetailsUrl() {
        return newsDetailsUrl;
    }

    public void setNewsDetailsUrl(String newsDetailsUrl) {
        this.newsDetailsUrl = newsDetailsUrl;
    }

    public String getNewsDetailsBody() {
        return newsDetailsBody;
    }

    public void setNewsDetailsBody(String newsDetailsBody) {
        this.newsDetailsBody = newsDetailsBody;
    }

    public String getNewsDetailsAuthor() {
        return newsDetailsAuthor;
    }

    public void setNewsDetailsAuthor(String newsDetailsAuthor) {
        this.newsDetailsAuthor = newsDetailsAuthor;
    }

    public String getNewsDetailsCreateDate() {
        return newsDetailsCreateDate;
    }

    public void setNewsDetailsCreateDate(String newsDetailsCreateDate) {
        this.newsDetailsCreateDate = newsDetailsCreateDate;
    }

    public int getNewsType() {
        return newsType;
    }

    public void setNewsType(int newsType) {
        this.newsType = newsType;
    }
}
