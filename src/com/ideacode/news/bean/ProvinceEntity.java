package com.ideacode.news.bean;

import java.util.List;

/**
 * <p>FileName: ProvinceEntity.java</p>
 * <p>Description: 省份对象bean<p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>@author Vic Su</p>
 * <p>@content andyliu900@gmail.com</p>
 * <p>@version 1.0</p>
 * <p>CreatDate: 2012-12-22 下午9:34:40</p>
 * <p>
 * Modification History
 */
public class ProvinceEntity {
    private int id;
    private String name;
    private List<CityEntity> cities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityEntity> getCities() {
        return cities;
    }

    public void setCities(List<CityEntity> cities) {
        this.cities = cities;
    }
}
