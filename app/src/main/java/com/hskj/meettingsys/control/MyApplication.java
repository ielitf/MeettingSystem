package com.hskj.meettingsys.control;

import android.app.Application;

import com.hskj.meettingsys.utils.SDCardUtils;
import com.hskj.meettingsys.utils.SharePreferenceManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

public class MyApplication extends Application {
	private static final String SHARED_PREFERENCE_NAME = "MeetingSystem_sp";
	@Override
	public void onCreate() {
		super.onCreate();
		SDCardUtils.writeTxt("001");
		SharePreferenceManager.init(getApplicationContext(), SHARED_PREFERENCE_NAME);
		//全局初始化
		OkGo.init(this);
		OkGo.getInstance().setConnectTimeout(3000)
				.setReadTimeOut(3000)
				.setWriteTimeOut(3000)
//				.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
//				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
				.setRetryCount(3);
	}
}
