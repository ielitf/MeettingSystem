package com.hskj.meettingsys.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.hskj.meettingsys.bean.ResponseAppVersion;
import com.hskj.meettingsys.control.CodeConstants;
import com.hskj.meettingsys.listener.CallBack;
import com.hskj.meettingsys.listener.FragmentCallBackA;
import com.hskj.meettingsys.listener.FragmentCallBackACur;
import com.hskj.meettingsys.listener.FragmentCallBackB;
import com.hskj.meettingsys.listener.FragmentCallBackBCur;
import com.hskj.meettingsys.utils.ApkUtils;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.SDCardUtils;
import com.hskj.meettingsys.utils.SharePreferenceManager;
import com.hskj.meettingsys.utils.ToastUtils;
import com.hskj.meettingsys.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements CallBack, View.OnClickListener {
    private static final int DOWNLOAD_STATUS_NEED_LOAD = 1;
    private static final int DOWNLOAD_STATUS_RUNNING = 2;
    private static final int DOWNLOAD_STATUS_LOADED = 3;
    private static final int RC_WRITE_EXTERNAL_PERM = 122;
    private ResponseAppVersion checkAPPVersion = null;
    private static FragmentCallBackA fragmentCallBackA;
    private static FragmentCallBackB fragmentCallBackB;
    private static FragmentCallBackACur fragmentCallBackACur;
    private static FragmentCallBackBCur fragmentCallBackBCur;
    private Fragment mContent = null;
    private List<Fragment> frags = new ArrayList<>();
    private AFragment aFragment = new AFragment();
    private BFragment bFragment = new BFragment();
    private ViewPager viewPager;
    private MyViewPagerAdapter pagerAdapter;
    private FragmentManager manager;
    private TextView room, cur, today, news_cur, news_today,download;
    private EditText editText;
    private int kk;
    private boolean aBoolean = true;
    private String topic, strMessage;
    private int templateId;// 0 代表模板A   1代表模板2
    private MqttService mqttService = new MqttService();
    private boolean isFirst = true;
    private List<MqttMeetingListBean> meetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> curMeeting = new ArrayList<>();
    private MeetingAdapter adapter = null;
    private String JsonStringCurMeet;
    private int i = 1;
    private TextView currentVersion, newVersion;
    private static String versionCodeOnLine,appUrl;
    private int versionCodeLocal;
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
        if (templateId == 1) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }
        checkVersion();
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

        news_cur = findViewById(R.id.cur_news);
        news_today = findViewById(R.id.today_news);
        news_cur.setOnClickListener(this);
        news_today.setOnClickListener(this);
        download = findViewById(R.id.download);
        download.setOnClickListener(this);

    }

    @Override
    public void setData(String topic, String strMessage) {
        this.topic = topic;
        this.strMessage = strMessage;
        LogUtil.w("===Main", "topic:" + topic + ";----strMessage:" + strMessage);
        if (MqttService.TOPIC_MEETING_CUR.equals(topic)) {
            //todo   当前会议
            if (!"".equals(strMessage) && !"[]".equals(strMessage) && strMessage != null && !TextUtils.isEmpty(strMessage)) {
                templateId = SharePreferenceManager.getMeetingMuBanType();//读取存储的模板类型
                curMeeting.clear();
                curMeeting.addAll(JSON.parseArray(strMessage, MqttMeetingCurrentBean.class));
                LogUtil.w("===curMeeting", "topic:" + topic + ";----curMeeting:" + curMeeting.toString());
//                JsonStringCurMeet = strMessage;
                if (templateId == 2) {//模板类型B
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(0);
                }
//                fragmentCallBackACur.TransDataACur(topic, JsonStringCurMeet);
//                fragmentCallBackBCur.TransDataBCur(topic, JsonStringCurMeet);
                fragmentCallBackA.TransDataA(topic, curMeeting);
                fragmentCallBackB.TransDataB(topic, curMeeting);
            }
        }

        if (MqttService.TOPIC_MEETING_LIST.equals(topic)) {
            //todo   会议列表
            if (!"".equals(strMessage) &&!"[]".equals(strMessage) && strMessage != null && !TextUtils.isEmpty(strMessage)) {
                meetingList.clear();
                meetingList.addAll(JSON.parseArray(strMessage, MqttMeetingListBean.class));
                LogUtil.w("===meetingList", "topic:" + topic + ";----meetingList:" + meetingList.toString());
                templateId = meetingList.get(0).getTemplateId();
                SharePreferenceManager.setMeetingMuBanType(templateId);//将模板类型存到本地缓存中

                if (templateId == 2) {//模板B
                    viewPager.setCurrentItem(1);
                } else {//模板a
                    viewPager.setCurrentItem(0);
                }
                fragmentCallBackA.TransDataA(topic, meetingList);
                fragmentCallBackB.TransDataB(topic, meetingList);
            }
        }
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
    Boolean ceshi = true;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cur_news:
//                if(ceshi){
//                    strMessage = "{\"endDate\":1560760200000,\"isOpen\":\"1\",\"meetingId\":576,\"meetingName\":\"zhanghao11\",\"roomName\":\"慧视科技会议室1\",\"startDate\":1560735900000}";
//                    fragmentCallBackACur.TransDataACur(topic, strMessage);
//                    fragmentCallBackBCur.TransDataBCur(topic, strMessage);
//                    ceshi = false;
//                }else{
//                    strMessage = "{\"endDate\":1560760200000,\"isOpen\":\"1\",\"meetingId\":576,\"meetingName\":\"zhanghao22\",\"roomName\":\"慧视科技会议室2\",\"startDate\":1560735900000}";
//                    fragmentCallBackACur.TransDataACur(topic, strMessage);
//                    fragmentCallBackBCur.TransDataBCur(topic, strMessage);
//                    ceshi = true;
//                }
                break;
            case R.id.today_news:

//                if(ceshi){
//                    meetingList.clear();
//                    strMessage = "[{\"bookPerson\":\"zhanghao11\",\"endDate\":1560743100000,\"id\":581,\"isOpen\":\"1\",\"name\":\"kkkk11\",\"roomName\":\"慧视科技会议室\",\"startDate\":1560742200000,\"templateId\":2},{\"bookPerson\":\"zhangsan11\",\"endDate\":1560744900000,\"id\":583,\"isOpen\":\"1\",\"name\":\"1231111\",\"roomName\":\"慧视科技会议室\",\"startDate\":1560744000000,\"templateId\":2}]";
//                    meetingList.addAll(JSON.parseArray(strMessage, MqttMeetingListBean.class));
//                    fragmentCallBackA.TransDataA("001_meetList", meetingList);
//                    fragmentCallBackB.TransDataB("001_meetList", meetingList);
//                    ceshi = false;
//                }else{
//                    meetingList.clear();
//                    strMessage = "[{\"bookPerson\":\"zhanghao22\",\"endDate\":1560743100000,\"id\":581,\"isOpen\":\"1\",\"name\":\"kkkk22\",\"roomName\":\"慧视科技会议室\",\"startDate\":1560742200000,\"templateId\":2},{\"bookPerson\":\"zhangsan22\",\"endDate\":1560744900000,\"id\":583,\"isOpen\":\"1\",\"name\":\"1232222\",\"roomName\":\"慧视科技会议室\",\"startDate\":1560744000000,\"templateId\":2}]";
//                    meetingList.addAll(JSON.parseArray(strMessage, MqttMeetingListBean.class));
//                    fragmentCallBackA.TransDataA("001_meetList", meetingList);
//                    fragmentCallBackB.TransDataB("001_meetList", meetingList);
//                    ceshi = true;
//                }
                break;
            case R.id.room:
                Toast.makeText(this, "会议室编号切换为：" + editText.getText(), Toast.LENGTH_SHORT).show();
                SDCardUtils.writeTxt(editText.getText() + "");
                MqttService.TOPIC_MEETING_LIST = editText.getText() + "_meetList";
                MqttService.TOPIC_MEETING_CUR = editText.getText() + "_currtMeet";
                break;
            case R.id.cur:
//                mqttService.publish(MqttService.TOPIC_MEETING_CUR,CodeConstants.MEETING_CUR_DATA,0);
                break;
            case R.id.today:
//                mqttService.publish(MqttService.TOPIC_MEETING_LIST,CodeConstants.MEETING_LIST_DATA,1);
                break;
            case R.id.download:
                loadFile2(appUrl);
                break;
        }
    }

    public static void setFragmentCallBackA(FragmentCallBackA callBack) {
        fragmentCallBackA = callBack;
    }

    public static void setFragmentCallBackB(FragmentCallBackB callBack) {
        fragmentCallBackB = callBack;
    }

    public static void setFragmentCallBackACur(FragmentCallBackACur callBack) {
        fragmentCallBackACur = callBack;
    }

    public static void setFragmentCallBackBCur(FragmentCallBackBCur callBack) {
        fragmentCallBackBCur = callBack;
    }

    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return frags.get(i);
        }

        @Override
        public int getCount() {
            return frags == null ? 0 : frags.size();
        }
    }

    private void loadFile() {
//        String path= Environment.getExternalStorageState();
//        String[] asd= FileUtil.getExtSDCardPath(context);
//        String asdad=asd[0];//内置
//        final String path2=asdad+"/"+fileName;
        //新建文件夹
        String folderName = "huishikeji";
        File sdCardDir2 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), folderName);
        if (!sdCardDir2.exists()) {
            if (!sdCardDir2.mkdirs()) {
                try {
                    sdCardDir2.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String destFileDir = sdCardDir2.getAbsolutePath();
        // todo 检查版本更新信息
        OkGo.<File>get("").execute(new FileCallback(destFileDir,"kkk") {
            @Override
            public void onSuccess(Response<File> response) {

            }
        });
    }
    private void loadFile2(String url){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download";
        GetRequest<File> request = OkGo.<File>get(url);
        DownloadTask task = OkDownload.request("taskTag",request)
                .save()
//                .folder(path)
                .register(new DownloadListener("taskTag") {
            @Override
            public void onStart(Progress progress) {
                LogUtil.d("apk","onStart");
            }

            @Override
            public void onProgress(Progress progress) {
                LogUtil.d("apk","onProgress");
            }

            @Override
            public void onError(Progress progress) {
                LogUtil.d("apk","onError");
            }

            @Override
            public void onFinish(File file, Progress progress) {
                LogUtil.d("apk",file.getAbsolutePath());
                ApkUtils.install(MainActivity.this,file);
            }

            @Override
            public void onRemove(Progress progress) {
                LogUtil.d("apk","onRemove");
            }
        });
//        task.start();//开始或者继续下载
//        task.pause();//暂停下载
//        task.remove();//删除下载，只删除记录，不删除文件
//        task.remove(true);//删除下载，同时删除记录和文件
        task.restart();//重新下载
    }
    public void checkVersion() {

        OkGo.<String>get("http://192.168.10.120:8080/app/uploadVersionInfo")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.d("===",response.body());
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            versionCodeOnLine = jsonObject.getString("code");
                            appUrl = jsonObject.getString("url");
                            versionCodeLocal = Utils.getVersionCode(MainActivity.this);
                            if(versionCodeOnLine != null  && appUrl != null){
                                if(Integer.parseInt(versionCodeOnLine) >versionCodeLocal ){
                                    loadFile2(appUrl);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
