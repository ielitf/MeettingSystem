package com.hskj.meettingsys.utils;

/**
 * 网络请求的url
 */
public class RequestApi {
    public static String  getUpdataAppUrl(){
        return BaseConfig.ServiceIp+"/app/uploadVersionInfo";
    }

}
