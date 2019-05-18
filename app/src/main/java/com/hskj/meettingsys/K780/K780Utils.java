package com.hskj.meettingsys.K780;

import com.hskj.meettingsys.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * K780数据操作类
 */
public class K780Utils {

	public static final String weaid = "1";
	public static final String APPKEY = "42583";
	public static final String SIGN = "7cb438dc230ebed6e14d34b9a5f0cfaa";
	public static final String WEATHER_URL ="http://api.k780.com:88/?app=weather.today"
			+ "&weaid="+ weaid
			+ "&appkey=" + APPKEY
			+ "&sign=" + SIGN
			+ "&format=json";

	/**
	 * 获取实时天气数据
	 * http://api.k780.com/?app=weather.today&weaid=1&appkey=42583&sign=7cb438dc230ebed6e14d34b9a5f0cfaa&format=json
	 * weaid = 1，北京
	 */
	public static Weather getOneDayWeather(int weaid) {
		String spec = "http://api.k780.com:88/?app=weather.today"
				+ "&weaid="+ weaid 
				+ "&appkey=" + APPKEY
				+ "&sign=" + SIGN
				+ "&format=json";
		
		byte[] data = HttpUtils.doGet(spec);
		// 如果数据下载失败，就返回null
		if(data == null) {
			return null;
		}
		String json = new String(data);
		// 解析json数据
		try {
			JSONObject jsonObj = new JSONObject(json);
			JSONObject result = jsonObj.getJSONObject("result");
			String citynm = result.getString("citynm");
			String temperature = result.getString("temperature");
			String temperature_curr = result.getString("temperature_curr");
			String weather_curr = result.getString("weather_curr");
			String weather_icon = result.getString("weather_icon");
			
			Weather weatherObj = new Weather(citynm, temperature, temperature_curr, weather_curr, weather_icon);
			return weatherObj;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	/**
//	 * 获取为来5-7天的天气数据
//	 */
//	public static List<Weather> getSevenDaysWeather(int weaid) {
//		String spec = "http://api.k780.com:88/?app=weather.future"
//				+ "&weaid="+weaid
//				+"&&appkey="+APPKEY
//				+ "&sign="+SIGN
//				+ "&format=json";
//		byte[] data = HttpUtils.doGet(spec);
//		if(data == null) {
//			return null;
//		}
//
//		// 存放天气对象的集合
//		List<Weather> list = new ArrayList<Weather>();
//		String json = new String(data);
//		try {
//			JSONObject jsonObj = new JSONObject(json);
//			JSONArray result = jsonObj.getJSONArray("result");
//			for (int i = 0; i < result.length(); i++) {
//				JSONObject temp = result.getJSONObject(i);
//				String days = temp.getString("days");
//				String week = temp.getString("week");
//				String citynm = temp.getString("citynm");
//				String temperature = temp.getString("temperature");
//				String weather = temp.getString("weather");
//				list.add(new Weather(days, week, citynm, temperature, weather));
//			}
//			return list;
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}

}
