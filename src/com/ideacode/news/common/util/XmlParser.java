package com.ideacode.news.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import android.util.Xml;

import com.ideacode.news.bean.CityEntity;
import com.ideacode.news.bean.ProvinceEntity;

final public class XmlParser {

    private static final String LOG_TAG = "SurePass2_News";

    /**
     * 加载全部的省份、城市信息
     * @param file
     * @return
     */
    public static List<ProvinceEntity> getProvinces(InputStream in) throws ParseException,
    SAXException, IOException {
        final ProvinceParser provinceParser = new ProvinceParser();
        try {
            android.util.Xml.parse(in, Xml.Encoding.UTF_8, provinceParser);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error of Parser : " + e.getMessage());
            throw new ParseException(e.getMessage());
        }
        return provinceParser.getProvinces();
    }

    /**
     * 执行Provinces解析的类 SAX引擎对XML文件解析时，采用这种方式进行解析：遇到标签时就回调startElement()方法;
     * 当遇到标签结束时，就回调endElement()方法。
     * @author Administrator
     *
     */
    final static private class ProvinceParser extends DefaultHandler {
        private List<ProvinceEntity> plist = null;
        private ProvinceEntity tempProvincetRel = null;
        private List<CityEntity> clist = null;
        private CityEntity tempCityRel = null;
        private final StringBuffer buffer = new StringBuffer();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (localName.equals("provinces")) {
                plist = new ArrayList<ProvinceEntity>();
            }
            if (localName.equals("province")) {
                tempProvincetRel = new ProvinceEntity();
            } else if (localName.equals("cities")) {
                clist = new ArrayList<CityEntity>();
            } else if (localName.equals("city")) {
                tempCityRel = new CityEntity();
            }
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            buffer.append(ch, start, length);
            super.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            boolean cleanBuffer = true;
            if (localName.equals("province")) {
                plist.add(tempProvincetRel);
                cleanBuffer = false;
            } else if (localName.equals("pid")) {
                tempProvincetRel.setId(Integer.parseInt(buffer.toString().trim()));
            } else if (localName.equals("pname")) {
                tempProvincetRel.setName(buffer.toString().trim());
            } else if (localName.equals("cities")) {
                tempProvincetRel.setCities(clist);
            } else if (localName.equals("city")) {
                clist.add(tempCityRel);
            } else if (localName.equals("cid")) {
                tempCityRel.setId(Integer.parseInt(buffer.toString().trim()));
            } else if (localName.equals("cname")) {
                tempCityRel.setName(buffer.toString().trim());
            }
            if (cleanBuffer)
                buffer.setLength(0);
            super.endElement(uri, localName, qName);
        }

        public List<ProvinceEntity> getProvinces() {
            return plist;
        }
    }
}
