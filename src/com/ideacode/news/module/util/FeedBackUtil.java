package com.ideacode.news.module.util;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.TbFeedBack;
import com.ideacode.news.net.SoapWebServiceUtil;

public class FeedBackUtil {

    /**
     * 发送用户反馈信息方法
     * 
     * @param tbFeedBack
     * @return
     */
    public static void sendFeedBackInfo(TbFeedBack tbFeedBack) throws AppException {
        JSONObject json = convertFeedBack2JSONObject(tbFeedBack);
        HashMap map = new HashMap();
        map.put("feedBack", json.toString());

        SoapWebServiceUtil stockSoap = new SoapWebServiceUtil("FeedBack", "sendFeedBackInfo", map);
        stockSoap.getRespondData();
    }

    /**
     * 将tbFeedBack对象转换成json对象
     * 
     * @param tbUser
     * @return
     */
    private static JSONObject convertFeedBack2JSONObject(TbFeedBack tbFeedBack) {
        JSONObject json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(tbFeedBack);
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
