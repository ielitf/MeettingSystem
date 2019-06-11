package com.hskj.meettingsys.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hskj.meettingsys.K780.K780Utils;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.control.CodeConstants;
import com.hskj.meettingsys.listener.CallBack;
import com.hskj.meettingsys.listener.FragmentCallBack;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.SDCardUtils;
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

public class MainActivity extends AppCompatActivity implements CallBack, View.OnClickListener {
    private static FragmentCallBack fragmentCallBack;
    private Fragment mContent = null;
    private List<Fragment> frags;
    private AFragment aFragment = new AFragment();
    private BFragment bFragment = new BFragment();
    private LinearLayout viewPager;
    private FragmentManager manager;
    private TextView ceshi, room,cur,today;
    private EditText editText;
    private int kk;
    private boolean aBoolean = true;
    private List<MqttMeetingListBean> meetingList = new ArrayList<>();
    private static String topic,strMessage;
    private String templateId;// 0 代表模板A   1代表模板2
    private MqttService mqttService = new MqttService();
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除通知栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"欢迎使用",Toast.LENGTH_SHORT).show();
        templateId = SharePreferenceManager.getMeetingMuBanType();//获取存储的磨板类型，默认值：“1”
        if (!isServiceRunning(String.valueOf(MqttService.class))) {
            startService(new Intent(this, MqttService.class));
        } else {
            LogUtil.i("===服务正在运行", "return");
            return;
        }
        MqttService.setCallBack(this);

        ceshi = findViewById(R.id.ceshi);
        ceshi.setOnClickListener(this);
        room = findViewById(R.id.room);
        room.setOnClickListener(this);
        cur = findViewById(R.id.cur);
        cur.setOnClickListener(this);
        today = findViewById(R.id.today);
        today.setOnClickListener(this);
        editText = findViewById(R.id.edit_query);
        editText.setText(SDCardUtils.readTxt());
        viewPager = findViewById(R.id.viewPager);

        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (templateId.equals("1")) {
            mContent = aFragment;
        }else {
            mContent = bFragment;
        }
        transaction.add(R.id.viewPager, mContent).commit();
    }

    @Override
    public void setData(String topic, String strMessage) {
        LogUtil.w("============Main", "topic:" + topic + ";----strMessage:" + strMessage);
        FragmentTransaction transaction = manager.beginTransaction();
        if (MqttService.TOPIC_MEETING_CUR.equals(topic)) {
            //todo   当前会议
            if (!"[]".equals(strMessage) && strMessage !=null && !TextUtils.isEmpty(strMessage)) {
                SharePreferenceManager.setMeetingCurrentData(strMessage);//存储当前会议，当只收到今日会议列表时，用于从缓存中读取当前会议
//                SharePreferenceManager.setMeetingTodayData("[{\"bookPerson\":\"zhangsan\",\"endDate\":1559024251000,\"id\":161,\"isOpen\":\"2\",\"name\":\"慧电科技会议室\",\"startDate\":1559001600000,\"templateId\":2}]");//存储当前会议，当只收到今日会议列表时，用于从缓存中读取当前会议
                templateId = SharePreferenceManager.getMeetingMuBanType();//读取存储的模板类型
                if (templateId.equals("2")) {//模板类型B
//                    transaction.replace(R.id.viewPager, BFragment.newInstance(topic, strMessage, SharePreferenceManager.getMeetingTodayData())).commit();
                    switchContent(bFragment);
                    fragmentCallBack.TransData(topic,strMessage);
                } else {
//                    transaction.replace(R.id.viewPager,AFragment.newInstance(topic,strMessage,SharePreferenceManager.getMeetingTodayData())).commit();
                    switchContent(aFragment);
                    fragmentCallBack.TransData(topic,strMessage);
                }
            }
        }

        if (MqttService.TOPIC_MEETING_LIST.equals(topic)) {
            //todo   会议列表
            if (!"[]".equals(strMessage) && strMessage!=null&& !TextUtils.isEmpty(strMessage)) {
                SharePreferenceManager.setMeetingTodayData(strMessage);//存储今日会议列表，当只收到当前会议时，用于从缓存中读取今日列表
                meetingList = JSON.parseArray(strMessage, MqttMeetingListBean.class);
                templateId = meetingList.get(0).getTemplateId();
                SharePreferenceManager.setMeetingMuBanType(templateId);//将模板类型存到本地缓存中
                if (templateId.equals("2")) {//模板B
                    switchContent(bFragment);
                    fragmentCallBack.TransData(topic,strMessage);
                } else {//模板a
                    switchContent(aFragment);
                    fragmentCallBack.TransData(topic,strMessage);
                }
            }
        }
    }

    private void getWheatherData() {
        OkGo.get(K780Utils.WEATHER_URL).cacheKey(K780Utils.WEATHER_URL).cacheMode(CacheMode.DEFAULT).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                LogUtil.i("=====response", response.toString());
                LogUtil.i("=====s", s);
                if (response.code() == 200) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject result = obj.getJSONObject("result");

                        String citynm = result.getString("citynm");
                        String temperature = result.getString("temperature");
                        String temperature_curr = result.getString("temperature_curr");
                        String weather_curr = result.getString("weather_curr");
                        String weather_icon = result.getString("weather_icon");
                        LogUtil.i("=====result", citynm);
                        LogUtil.i("=====result", temperature);
                        LogUtil.i("=====result", temperature_curr);
                        LogUtil.i("=====result", weather_curr);
                        LogUtil.i("=====result", weather_icon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                LogUtil.i("=====天气获取失败", e.toString());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ceshi:
                FragmentTransaction transaction = manager.beginTransaction();
                if (aBoolean) {//  002_currtMeet   002_meetList
                    switchContent(bFragment);
                    aBoolean = false;
                } else {
                    switchContent(aFragment);
                    aBoolean = true;
                }
                kk = kk + 1;
                ceshi.setText("ceshi" + kk);
                break;
            case R.id.room:
                Toast.makeText(this,"会议室编号切换为："+editText.getText(),Toast.LENGTH_SHORT).show();
                SDCardUtils.writeTxt(editText.getText()+"");
                MqttService.TOPIC_MEETING_LIST = editText.getText()+ "_meetList";
                MqttService.TOPIC_MEETING_CUR = editText.getText()+ "_currtMeet";;
                break;
            case R.id.cur:
//                mqttService.publish(MqttService.TOPIC_MEETING_CUR,CodeConstants.MEETING_CUR_DATA,0);
                break;
            case R.id.today:
//                mqttService.publish(MqttService.TOPIC_MEETING_LIST,CodeConstants.MEETING_LIST_DATA,1);
                break;
        }
    }
    private void switchContent(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (!to.isAdded()) { // 判断是否被add过
                // 隐藏当前的fragment，将 下一个fragment 添加进去
                transaction.hide(mContent).add(R.id.viewPager, to).commit();
            } else {
                // 隐藏当前的fragment，显示下一个fragment
                transaction.hide(mContent).show(to).commit();
            }
            mContent = to;
        }
    }
    public static void setFragmentCallBack(FragmentCallBack callBack){
        fragmentCallBack = callBack;
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
////                LogUtil.i("=====result",result.toString());
////
////            }
////            // 关闭对话框
//////            dialog.cancel();
////        }
////
////    }
}
