package com.ideacode.news.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ideacode.news.R;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.widget.adapter.AbstractWheelTextAdapter;

public class ProvinceAdapter extends AbstractWheelTextAdapter {

    // private final String provinces[] = new String[] { "北京", "天津", "河北", "山西", "内蒙古", "广东", "河南",
    // "重庆" };

    /**
     * Constructor
     */
    public ProvinceAdapter(Context context) {
        super(context, R.layout.province_layout, NO_RESOURCE);
        setItemTextResource(R.id.country_name);
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        return view;
    }

    @Override
    public int getItemsCount() {
        return CommonSetting.provinces.length;
    }

    @Override
    protected CharSequence getItemText(int index) {
        return CommonSetting.provinces[index].getName();
    }

}
