package com.ideacode.news.bean;

public class Favourite {

    private int favouritelistId;
    private long favouritelistUserid;
    private int favouritelistNewstype;
    private String favouritelistTitle;
    private String favouritelistAuthor;
    private String favouritelistDate;
    private String favouritelistUrl;
    private boolean heatFlag;

    public Favourite() {

    }

    public int getFavouritelistId() {
        return favouritelistId;
    }

    public long getFavouritelistUserid() {
        return favouritelistUserid;
    }

    public int getFavouritelistNewstype() {
        return favouritelistNewstype;
    }

    public String getFavouritelistTitle() {
        return favouritelistTitle;
    }

    public String getFavouritelistAuthor() {
        return favouritelistAuthor;
    }

    public String getFavouritelistDate() {
        return favouritelistDate;
    }

    public String getFavouritelistUrl() {
        return favouritelistUrl;
    }

    public void setFavouritelistId(int favouritelistId) {
        this.favouritelistId = favouritelistId;
    }

    public void setFavouritelistUserid(long favouritelistUserid) {
        this.favouritelistUserid = favouritelistUserid;
    }

    public void setFavouritelistNewstype(int favouritelistNewstype) {
        this.favouritelistNewstype = favouritelistNewstype;
    }

    public void setFavouritelistTitle(String favouritelistTitle) {
        this.favouritelistTitle = favouritelistTitle;
    }

    public void setFavouritelistAuthor(String favouritelistAuthor) {
        this.favouritelistAuthor = favouritelistAuthor;
    }

    public void setFavouritelistDate(String favouritelistDate) {
        this.favouritelistDate = favouritelistDate;
    }

    public void setFavouritelistUrl(String favouritelistUrl) {
        this.favouritelistUrl = favouritelistUrl;
    }

    public boolean isHeatFlag() {
        return heatFlag;
    }

    public void setHeatFlag(boolean heatFlag) {
        this.heatFlag = heatFlag;
    }
}
