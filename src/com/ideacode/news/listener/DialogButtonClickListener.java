package com.ideacode.news.listener;

import android.content.DialogInterface;

/**
 * <p>FileName: DialogButtonClickListener.java</p>
 * <p>Description: 自定义对话框按钮点击监听类<p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>@author Vic Su</p>
 * <p>@content andyliu900@gmail.com</p>
 * <p>@version 1.0</p>
 * <p>CreatDate: 2012-12-26 上午12:10:59</p>
 * <p>
 * Modification History
 */
public class DialogButtonClickListener implements DialogInterface.OnClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub

    }

    int newProvinceId, newCityId;

    public void onClick(DialogInterface dialog, int which, int newProvinceId, int newCityId) {
        // TODO Auto-generated method stub
        this.newProvinceId = newProvinceId;
        this.newCityId = newCityId;
    }

    public Integer[] getIds() {
        Integer[] ids = new Integer[] { newProvinceId, newCityId };
        return ids;
    }
}
