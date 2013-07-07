package com.ideacode.news.bean;

public class NewslistUrl extends Entity{

	private int newsListId;
    private int newsTypeId;
    private String newsListUrl;
    private int currentPage;

    public NewslistUrl(){}

	public int getNewsListId() {
		return newsListId;
	}

	public void setNewsListId(int newsListId) {
		this.newsListId = newsListId;
	}

	public int getNewsTypeId() {
		return newsTypeId;
	}

	public void setNewsTypeId(int newsTypeId) {
		this.newsTypeId = newsTypeId;
	}

	public String getNewsListUrl() {
		return newsListUrl;
	}

	public void setNewsListUrl(String newsListUrl) {
		this.newsListUrl = newsListUrl;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
