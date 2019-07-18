package com.hskj.meettingsys.K780;

/**
 * K780数据操作类
 */
public class K780Utils {
	public static final String host = "http://api.k780.com/?app=weather.today";
	public static final String weaid = "1";
	public static final String APPKEY = "42583";
	public static final String SIGN = "7cb438dc230ebed6e14d34b9a5f0cfaa";
	public static final String WEATHER_URL = host
			+ "&weaid="+ weaid
			+ "&appkey=" + APPKEY
			+ "&sign=" + SIGN
			+ "&format=json";

}
