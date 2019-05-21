package com.hskj.meettingsys.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceManager {
	    static SharedPreferences sp;
 
	    public static void init(Context context, String name) {
	        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	    }
	    
        private static final String CACHED_HOMEPAGE_DATA="cached_homepage_data";

        public static void setCachedHomepageData(String jsonStr){
        	if (null != sp) {
	            sp.edit().putString(CACHED_HOMEPAGE_DATA, jsonStr).commit();
	        }
        }
        public static String getCachedHomepageData(){
        	 if (null != sp) {
 	            return sp.getString(CACHED_HOMEPAGE_DATA, null);
 	        }
 	        return null;
        }
        
//
	    private static final String IS_FIRST_USE="Is_app_first_use";

	    public static void setIsFirstUse(boolean value){
	    	 if (null != sp) {
		            sp.edit().putBoolean(IS_FIRST_USE, value).commit();
		        }
	    }
	    public static boolean getIsFirstUse(){
	    	if (null != sp) {
	            return sp.getBoolean(IS_FIRST_USE, true);
	        }
	        return true;
	    }
}
