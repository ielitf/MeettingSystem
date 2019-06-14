package com.hskj.meettingsys.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.listener.CallBack;
import com.hskj.meettingsys.listener.FragmentCallBackA;
import com.hskj.meettingsys.listener.FragmentCallBackB;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.SDCardUtils;
import com.hskj.meettingsys.utils.SharePreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CallBack, View.OnClickListener {
    private static FragmentCallBackA fragmentCallBackA;
    private static FragmentCallBackB fragmentCallBackB;
    private Fragment mContent = null;
    private List<Fragment> frags = new ArrayList<>();
    private AFragment aFragment = new AFragment();
    private BFragment bFragment = new BFragment();
    private ViewPager viewPager;
    private  MyViewPagerAdapter pagerAdapter;
    private FragmentManager manager;
    private TextView room,cur,today;
    private EditText editText;
    private int kk;
    private boolean aBoolean = true;
    private  String topic,strMessage;
    private int templateId;// 0 代表模板A   1代表模板2
    private MqttService mqttService = new MqttService();
    private boolean isFirst = true;
    private List<MqttMeetingListBean> meetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> curMeeting = new ArrayList<>();
    private MeetingAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除通知栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        templateId = SharePreferenceManager.getMeetingMuBanType();//获取存储的磨板类型，默认值：“1”
        if (!isServiceRunning(String.valueOf(MqttService.class))) {
            startService(new Intent(this, MqttService.class));
        } else {
            LogUtil.i("===服务正在运行", "return");
            return;
        }
        MqttService.setCallBack(this);
        initViews();
        frags.add(aFragment);
        frags.add(bFragment);
        pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        if (templateId ==1 ) {
            viewPager.setCurrentItem(0);
        }else {
            viewPager.setCurrentItem(1);
        }
    }

    private void initViews() {
        room = findViewById(R.id.room);
        room.setOnClickListener(this);
        cur = findViewById(R.id.cur);
        cur.setOnClickListener(this);
        today = findViewById(R.id.today);
        today.setOnClickListener(this);
        editText = findViewById(R.id.edit_query);
        editText.setText(SDCardUtils.readTxt());
        viewPager = findViewById(R.id.viewPager);
    }

    @Override
    public void setData(String topic, String strMessage) {
        this.topic = topic;
        this.strMessage = strMessage;
        LogUtil.w("===Main", "topic:" + topic + ";----strMessage:" + strMessage);
        if (MqttService.TOPIC_MEETING_CUR.equals(topic)) {
            //todo   当前会议
            if (!"[]".equals(strMessage) && strMessage !=null && !TextUtils.isEmpty(strMessage)) {
                templateId = SharePreferenceManager.getMeetingMuBanType();//读取存储的模板类型
                curMeeting = JSON.parseArray(strMessage, MqttMeetingCurrentBean.class);
                if (templateId == 2) {//模板类型B
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(0);
                }
                fragmentCallBackA.TransDataA(topic,curMeeting);
                fragmentCallBackB.TransDataB(topic,curMeeting);
            }
        }

        if (MqttService.TOPIC_MEETING_LIST.equals(topic)) {
            //todo   会议列表
            if (!"[]".equals(strMessage) && strMessage!=null&& !TextUtils.isEmpty(strMessage)) {
                meetingList = JSON.parseArray(strMessage, MqttMeetingListBean.class);
                templateId = meetingList.get(0).getTemplateId();
                SharePreferenceManager.setMeetingMuBanType(templateId);//将模板类型存到本地缓存中

                if (templateId == 2) {//模板B
                    viewPager.setCurrentItem(1);
                } else {//模板a
                    viewPager.setCurrentItem(0);
                }
                fragmentCallBackA.TransDataA(topic,meetingList);
                fragmentCallBackB.TransDataB(topic,meetingList);
            }
        }
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
    public static void setFragmentCallBackA(FragmentCallBackA callBack){
        fragmentCallBackA = callBack;
    }
    public static void setFragmentCallBackB(FragmentCallBackB callBack){
        fragmentCallBackB = callBack;
    }
    private  class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return frags.get(i);
        }

        @Override
        public int getCount() {
            return frags == null ? 0:frags.size();
        }
    }
}
