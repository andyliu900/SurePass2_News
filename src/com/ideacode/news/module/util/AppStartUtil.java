package com.ideacode.news.module.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xml.sax.SAXException;

import android.content.Context;

import com.ideacode.news.bean.CityEntity;
import com.ideacode.news.bean.ProvinceEntity;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.ParseException;
import com.ideacode.news.common.util.XmlParser;

public class AppStartUtil {
    private static final String TAG = "AppStartUtil";

    /**
     * 获取省份、城市
     * @return
     * @throws ParseException 
     */
    public static void getProvinces(Context context) throws SAXException, IOException,
    ParseException {
        InputStream in = context.getResources().getAssets().open("provinces.xml");
        List<ProvinceEntity> provincesList = XmlParser.getProvinces(in);
        ProvinceEntity[] provinces = new ProvinceEntity[provincesList.size()];
        CityEntity[][] main_cities = new CityEntity[provincesList.size()][]; // 可先指定行，不指定列
        for (ProvinceEntity province : provincesList) {
            provinces[provincesList.indexOf(province)] = province;
            CityEntity[] cities = new CityEntity[province.getCities().size()];
            for (CityEntity city : province.getCities()) {
                cities[province.getCities().indexOf(city)] = city;
            }
            main_cities[provincesList.indexOf(province)] = cities;
        }

        CommonSetting.provinces = provinces;
        CommonSetting.cities = main_cities;
    }
}
