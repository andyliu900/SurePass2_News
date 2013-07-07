package com.ideacode.news.bean;

import java.text.SimpleDateFormat;

public abstract class Entity extends Base {

    public final static SimpleDateFormat SDF_IN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat SDF_OUT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    protected int id;

    public int getId() {
        return id;
    }

    protected String cacheKey;

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
}
