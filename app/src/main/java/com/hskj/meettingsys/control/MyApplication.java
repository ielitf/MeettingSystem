package com.hskj.meettingsys.control;

import android.app.Application;

import com.hskj.meettingsys.utils.SDCardUtils;
import com.hskj.meettingsys.utils.SharePreferenceManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {
	private static final String SHARED_PREFERENCE_NAME = "MeetingSystem_sp";
	@Override
	public void onCreate() {
		super.onCreate();
		SharePreferenceManager.init(getApplicationContext(), SHARED_PREFERENCE_NAME);
		if(SharePreferenceManager.getIsFirstUse()){
			SDCardUtils.writeTxt("001",CodeConstants.ROOM_NUMBER);
			SDCardUtils.writeTxt("192.168.10.2:1883",CodeConstants.IP_HOST);
			SharePreferenceManager.setIsFirstUse(false);
		}
		//全局初始化
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("meee");
		//日志的打印范围
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
		//在logcat中的颜色
		loggingInterceptor.setColorLevel(Level.INFO);
		//默认是Debug日志类型
		builder.addInterceptor(loggingInterceptor);

		//设置请求超时时间,默认60秒
		builder.readTimeout(30000, TimeUnit.MILLISECONDS);      //读取超时时间
		builder.writeTimeout(30000, TimeUnit.MILLISECONDS);     //写入超时时间
		builder.connectTimeout(30000, TimeUnit.MILLISECONDS);   //连接超时时间

		//okhttp默认不保存cookes/session信息,需要自己的设置
		//builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
		builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保存cookie,退出后失效

		OkGo.getInstance().init(this);
		OkGo.getInstance()
				.setOkHttpClient(builder.build())
//				.setConnectTimeout(3000)
//				.setReadTimeOut(3000)
//				.setWriteTimeOut(3000)
//				.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
//				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
				.setRetryCount(3);
	}


}
