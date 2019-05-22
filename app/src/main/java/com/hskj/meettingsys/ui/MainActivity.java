package com.hskj.meettingsys.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hskj.meettingsys.K780.K780Utils;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.listener.CallBack;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.SharePreferenceManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements CallBack {
    private List<Fragment> frags;
    private AFragment aFragment = new AFragment();
    private BFragment bFragment =  new BFragment();
    private LinearLayout viewPager;
    private FragmentManager manager;
    private TextView ceshi;
    private int kk;
    private boolean aBoolean = false;
    private List<MqttMeetingListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isServiceRunning(String.valueOf(MqttService.class))) {
            startService(new Intent(this, MqttService.class));
        }else{
            Log.i("服务正在运行","return");
            return;
        }
        MqttService.setCallBack(this);

        ceshi = findViewById(R.id.ceshi);
        viewPager = findViewById(R.id.viewPager);

        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.viewPager,aFragment).commit();

        ceshi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = manager.beginTransaction();
                if(aBoolean){
                    transaction.replace(R.id.viewPager,BFragment.newInstance("B这是主题B","B这是消息内容B")).commit();
                    aBoolean = true;
                }else{
                    transaction.replace(R.id.viewPager,AFragment.newInstance("A这是主题A","A这是消息内容A")).commit();
                    aBoolean = false;
                }
                kk = kk +1;
                ceshi.setText("ceshi"+kk);
            }
        });
    }
    @Override
    public void setData(String topic,String strMessage) {
        Log.i("============","topic:"+topic+"----strMessage:"+strMessage);
        FragmentTransaction transaction = manager.beginTransaction();
        if("002_meetList".equals(topic)){
            //todo   会议列表
            SharePreferenceManager.setMeetingTodayData(strMessage);
        }
        if("".equals(topic)){
            //todo   根据模板id显示相应的模板
            SharePreferenceManager.setMeetingCurrentData(strMessage);
        }
        list = JSON.parseArray(strMessage, MqttMeetingListBean.class);
        transaction.replace(R.id.viewPager,BFragment.newInstance("B这是主题B","B这是消息内容B")).commit();

    }
    private void getWheatherData() {
        OkGo.get(K780Utils.WEATHER_URL).cacheKey(K780Utils.WEATHER_URL).cacheMode(CacheMode.DEFAULT).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("=====response", response.toString());
                Log.i("=====s", s);
                if (response.code() == 200) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject result = obj.getJSONObject("result");

                        String citynm = result.getString("citynm");
                        String temperature = result.getString("temperature");
                        String temperature_curr = result.getString("temperature_curr");
                        String weather_curr = result.getString("weather_curr");
                        String weather_icon = result.getString("weather_icon");
                        Log.i("=====result", citynm);
                        Log.i("=====result", temperature);
                        Log.i("=====result", temperature_curr);
                        Log.i("=====result", weather_curr);
                        Log.i("=====result", weather_icon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.i("=====天气获取失败", e.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 判断服务是否运行
     */
    private boolean isServiceRunning(final String className) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) return true;
        }
        return false;
    }

////    private class MyTask extends AsyncTask<Integer, Void, Weather> {
////
////        // 进度条对话框
////        ProgressDialog dialog;
////
////        @Override
////        protected void onPreExecute() {
//////            dialog = new ProgressDialog(MainActivity.this);
//////            dialog.setTitle("天气数据");
//////            dialog.setMessage("正在下载...");
//////            // 显示对话框
//////            dialog.show();
////        }
////
////        @Override
////        protected Weather doInBackground(Integer... params) {
////            return K780Utils.getOneDayWeather(params[0]);
////        }
////
////        @Override
////        protected void onPostExecute(Weather result) {
////            if(result == null) {
////                Toast.makeText(MainActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
////            } else {
////
////                Log.i("=====result",result.toString());
////
////            }
////            // 关闭对话框
//////            dialog.cancel();
////        }
////
////    }
}
