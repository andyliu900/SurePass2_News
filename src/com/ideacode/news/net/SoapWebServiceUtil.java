package com.ideacode.news.net;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.ideacode.news.app.AppException;
import com.ideacode.news.common.util.CommonSetting;

/**
 * <p>
 * FileName: SoapWebServiceUtil.java
 * </p>
 * <p>
 * Description: cxf形式的webservice数据传输工具类
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 * @author Vic Su
 * </p>
 * <p>
 * @content andyliu900@gmail.com
 * </p>
 * <p>
 * @version 1.0
 * </p>
 * <p>
 * CreatDate: 2012-10-25 下午3:51:42
 * </p>
 * <p>
 * Modification History
 * </p>
 */
@SuppressWarnings("all")
public class SoapWebServiceUtil { 
    static private Log log = LogFactory.getLog(SoapWebServiceUtil.class.getName());

    /** 服务的命名空间 */
    private static String NAMESPACE = "http://webservice.cxf.msg.net.cn/";
    /** 服务名称 */
    private static String SERVICE_NAME = null;
    private static String METHOD_NAME = null;
    private static HashMap PROPERTYS = null;

    public SoapWebServiceUtil(String service_name, String method_name, HashMap propertys) {
        this.SERVICE_NAME = service_name;
        this.METHOD_NAME = method_name;
        this.PROPERTYS = propertys;
    }

    public SoapObject getRespondData() throws AppException{
        SoapObject result = null;
        try {
            // 调用的方法
            String methodName = METHOD_NAME;
            // 创建httpTransportSE传输对象
            HttpTransportSE ht = new HttpTransportSE(CommonSetting.WebServiceUrl + SERVICE_NAME);
            ht.debug = true;
            // 使用soap1.1协议创建Envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // 实例化SoapObject对象
            SoapObject request = new SoapObject(NAMESPACE, methodName);
            if (PROPERTYS != null) {
                Object[] keySets = PROPERTYS.keySet().toArray(); // 获得传进来的HashMap的key值
                for (int i = 0; i < keySets.length; i++) {
                    request.addProperty(keySets[i].toString(), PROPERTYS.get(keySets[i].toString()));
                }
            }
            // 将SoapObject对象设置为SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = request;
            // 调用webService
            ht.call(null, envelope);
            if (envelope.getResponse() != null) {
                result = (SoapObject) envelope.getResponse();
            }
        } catch (SoapFault e) {
            e.printStackTrace();
            throw AppException.soap(e);            
        } catch (IOException e) {
            e.printStackTrace();
            throw AppException.io(e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            throw AppException.xml(e);
        }
        return result;

    }
    
    /**
     * 兼容服务器端返回非byte[]类型数据，如String、int等
     * @return
     */
    public Object getObjectRespondData() throws AppException{
    	Object result = null;
        try {
            // 调用的方法
            String methodName = METHOD_NAME;
            // 创建httpTransportSE传输对象
            HttpTransportSE ht = new HttpTransportSE(CommonSetting.WebServiceUrl + SERVICE_NAME);
            ht.debug = true;
            // 使用soap1.1协议创建Envelop对象
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // 实例化SoapObject对象
            SoapObject request = new SoapObject(NAMESPACE, methodName);
            if (PROPERTYS != null) {
                Object[] keySets = PROPERTYS.keySet().toArray(); // 获得传进来的HashMap的key值
                for (int i = 0; i < keySets.length; i++) {
                    request.addProperty(keySets[i].toString(), PROPERTYS.get(keySets[i].toString()));
                }
            }
            // 将SoapObject对象设置为SoapSerializationEnvelope对象的传出SOAP消息
            envelope.bodyOut = request;
            // 调用webService
            ht.call(null, envelope);
            if (envelope.getResponse() != null) {
                result = (Object) envelope.getResponse();
            }
        } catch (SoapFault e) {
            e.printStackTrace();
            throw AppException.soap(e);            
        } catch (IOException e) {
            e.printStackTrace();
            throw AppException.io(e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            throw AppException.xml(e);
        }
        return result;
    }
}
