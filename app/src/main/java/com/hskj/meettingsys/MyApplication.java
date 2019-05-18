package com.hskj.meettingsys;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//全局初始化
		OkGo.init(this);
		OkGo.getInstance().setConnectTimeout(3000)
				.setReadTimeOut(3000)
				.setWriteTimeOut(3000)
				.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
				.setRetryCount(3);
	}
}
