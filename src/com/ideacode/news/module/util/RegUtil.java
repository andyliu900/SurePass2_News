package com.ideacode.news.module.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.net.SoapWebServiceUtil;

public class RegUtil {

	/**
	 * 校验邮箱是否已经被注册
	 * 
	 * @param email
	 * @return
	 */
    public static int checkUser(String name, String email) throws AppException {
		HashMap paramsMap = new HashMap();
        paramsMap.put("name", name);
		paramsMap.put("email", email);
        SoapWebServiceUtil regSoap = new SoapWebServiceUtil("Member", "checkUser", paramsMap);
		SoapObject soapObject = regSoap.getRespondData();
		int code = Integer.parseInt(soapObject.getProperty("code").toString());

		return code;
	}

	/**
	 * 向后台保存user对象
	 * 
	 * @param tbUser
	 * @return
	 */
	public static Map regUser(TbUser tbUser) throws AppException {
		JSONObject json = convertUser2JSONObject(tbUser);
		HashMap paramsMap = new HashMap();
		paramsMap.put("tbUser", json.toString());

		SoapWebServiceUtil regSoap = new SoapWebServiceUtil("Member", "regUser", paramsMap);
		SoapObject soapObject = regSoap.getRespondData();
		String code = soapObject.getProperty("code").toString();
		String userId = soapObject.getProperty("userId").toString();

		Map returnMap = new HashMap();
		returnMap.put("code", code);
		returnMap.put("userId", userId);

		return returnMap;
	}

	/**
	 * 将tbUser对象转换成json对象
	 * 
	 * @param tbUser
	 * @return
	 */
	private static JSONObject convertUser2JSONObject(TbUser tbUser) {
		JSONObject json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String result = objectMapper.writeValueAsString(tbUser);
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
