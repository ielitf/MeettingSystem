package com.hskj.meettingsys.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.hskj.meettingsys.control.CodeConstants;
import com.hskj.meettingsys.greendao.DaoMaster;
import com.hskj.meettingsys.greendao.DaoSession;
import com.hskj.meettingsys.greendao.MqttMeetingListBeanDao;
import com.hskj.meettingsys.listener.CurMeetingCallBack;
import com.hskj.meettingsys.listener.FragmentCallBackA;
import com.hskj.meettingsys.listener.FragmentCallBackB;
import com.hskj.meettingsys.listener.TodayMeetingCallBack;
import com.hskj.meettingsys.utils.ApkUtils;
import com.hskj.meettingsys.utils.DateTimeUtil;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.RequestApi;
import com.hskj.meettingsys.utils.SDCardUtils;
import com.hskj.meettingsys.utils.SharePreferenceManager;
import com.hskj.meettingsys.utils.SharedPreferenceTools;
import com.hskj.meettingsys.utils.ToastUtils;
import com.hskj.meettingsys.utils.Utils;
import com.lzy.okgo.OkGo;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CurMeetingCallBack, TodayMeetingCallBack, View.OnClickListener {
    private static FragmentCallBackA fragmentCallBackA;
    private static FragmentCallBackB fragmentCallBackB;
    private List<Fragment> frags = new ArrayList<>();
    private AFragment aFragment = new AFragment();
    private BFragment bFragment = new BFragment();
    private ViewPager viewPager;
    private MyViewPagerAdapter pagerAdapter;
    private TextView room, cur, today, news_cur, news_today, download;
    private EditText editText;
    private int templateId;// 0 代表模板A   1代表模板2
    private List<MqttMeetingListBean> meetingListQuery = new ArrayList<>();
    private List<MqttMeetingListBean> meetingListReceive = new ArrayList<>();
    private List<MqttMeetingCurrentBean> curMeeting = new ArrayList<>();
    private MeetingAdapter adapter = null;
    private static String versionCodeOnLine, appUrl,meetingRoomName;
    private int versionCodeLocal;
    private Intent intent;
    private DateTimeUtil dateTimeUtil;
    private static String roomNum;//会议室编号
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MqttMeetingListBeanDao meetingListBeanDao;
    private MqttMeetingListBean mqttMeetingListBean;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1:
                    viewPager.setCurrentItem(0);
                    break;
                case 0x2:
                    viewPager.setCurrentItem(1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除通知栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dateTimeUtil = DateTimeUtil.getInstance();
        templateId = SharePreferenceManager.getMeetingMuBanType();//获取存储的磨板类型，默认值：“1”
        getStuDao();
        initViews();
        frags.add(aFragment);
        frags.add(bFragment);
        pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        if (templateId == 1) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }

        //删除数据库中今天之前的会议信息
        List<MqttMeetingListBean> userList = meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.StartDate.lt(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"))).build().list();
        for (MqttMeetingListBean user : userList) {
            meetingListBeanDao.delete(user);
        }
        checkVersion();

        MqttService.setCurMeetingCallBack(this);
        MqttService.setTodayMeetingCallBack(this);
        //开启服务
        if (!isServiceRunning(String.valueOf(MqttService.class))) {
            intent = new Intent(this, MqttService.class);
            startService(intent);
            LogUtil.e("====Main", "service is started");
        } else {
            LogUtil.i("===服务正在运行", "return");
            return;
        }
    }

    /**
     * 获取StudentDao
     */
    private void getStuDao() {
        // 创建数据
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "meetingList.db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        meetingListBeanDao = daoSession.getMqttMeetingListBeanDao();
    }

    private void initViews() {
        room = findViewById(R.id.room);
        room.setOnClickListener(this);
        cur = findViewById(R.id.cur);
        cur.setOnClickListener(this);
        today = findViewById(R.id.today);
        today.setOnClickListener(this);
        editText = findViewById(R.id.edit_query);
        editText.setText(SDCardUtils.readTxt("roomName"));
        viewPager = findViewById(R.id.viewPager);

        news_cur = findViewById(R.id.cur_news);
        news_today = findViewById(R.id.today_news);
        news_cur.setOnClickListener(this);
        news_today.setOnClickListener(this);
        download = findViewById(R.id.download);
        download.setOnClickListener(this);

    }

    @Override
    public void setDataCur(String topic, String strMessage) {
        LogUtil.w("===Main", "topic:" + topic + ";----strMessage:" + strMessage);
        if (!"".equals(strMessage) && !"[]".equals(strMessage) && strMessage != null && !TextUtils.isEmpty(strMessage)) {
            templateId = SharePreferenceManager.getMeetingMuBanType();//读取存储的模板类型
            curMeeting.clear();
            curMeeting.addAll(JSON.parseArray(strMessage, MqttMeetingCurrentBean.class));
            LogUtil.w("===curMeeting", "topic:" + topic + ";----curMeeting:" + curMeeting.toString());
            fragmentCallBackA.TransDataA(topic, curMeeting);
            fragmentCallBackB.TransDataB(topic, curMeeting);
        }
    }

    @Override
    public void setDataToday(String topic, String strMessage) {
        LogUtil.w("===Main", "topic:" + topic + ";----strMessage:" + strMessage);
        if (!"".equals(strMessage) && !"[]".equals(strMessage) && strMessage != null && !TextUtils.isEmpty(strMessage)) {
            meetingListReceive.clear();
            meetingListReceive.addAll(JSON.parseArray(strMessage, MqttMeetingListBean.class));
            LogUtil.w("===meetingList", "topic:" + topic + ";----meetingList:" + meetingListReceive.toString());
            templateId = meetingListReceive.get(0).getTemplateId();
            meetingRoomName = meetingListReceive.get(0).getRoomName();
            SharedPreferenceTools.putValuetoSP(this, CodeConstants.MEETING_ROOM_NAME,meetingRoomName);
            SharePreferenceManager.setMeetingMuBanType(templateId);//将模板类型存到本地缓存中
            roomNum = (String) SharedPreferenceTools.getValueofSP(this, "DeviceNum", "");//获取会议室编号

            switch (meetingListReceive.get(0).getSign()) {
                case "insert":
                    try {
                        mqttMeetingListBean = new MqttMeetingListBean(null,
                                meetingListReceive.get(0).getId(),
                                roomNum,
                                meetingListReceive.get(0).getRoomName(),
                                meetingListReceive.get(0).getName(),
                                meetingListReceive.get(0).getIsOpen(),
                                meetingListReceive.get(0).getEndDate(),
                                meetingListReceive.get(0).getStartDate(),
                                meetingListReceive.get(0).getTemplateId(),
                                meetingListReceive.get(0).getBookPerson(),
                                meetingListReceive.get(0).getSign())
                        ;
                        meetingListBeanDao.insert(mqttMeetingListBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.w("===Exception", "插入失败" + e.getMessage());
                    }
                    break;
                case "delete":
                    mqttMeetingListBean = meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.Id.eq(meetingListReceive.get(0).getId())).build().unique();
                    if (mqttMeetingListBean != null) {
                        meetingListBeanDao.delete(mqttMeetingListBean);
                    }
                    break;
                case "update":
                    mqttMeetingListBean = meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.Id.eq(meetingListReceive.get(0).getId())).build().unique();
                    if (mqttMeetingListBean != null) {
                        meetingListBeanDao.update(mqttMeetingListBean);
                    }
                    break;
            }
            meetingListQuery.clear();
            meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum), MqttMeetingListBeanDao.Properties.StartDate
                    .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                    .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                    .build().list());
            fragmentCallBackA.TransDataA(topic, meetingListQuery);
            fragmentCallBackB.TransDataB(topic, meetingListQuery);

            Message msg = new Message();
            if (templateId == 1) {//模板
//                viewPager.setCurrentItem(0);
                msg.what = 0x1;
            } else {//模板
//                viewPager.setCurrentItem(1);
                msg.what = 0x2;
            }
            handler.sendMessage(msg);
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
    int i = 0;

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
            case R.id.today_news://删除
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
                List<MqttMeetingListBean> userList = meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.StartDate.lt(System.currentTimeMillis())).build().list();
                for (MqttMeetingListBean user : userList) {
                    meetingListBeanDao.delete(user);
                }
                break;
            case R.id.room:
                Toast.makeText(this, "会议室编号切换为：" + editText.getText(), Toast.LENGTH_SHORT).show();
                SharedPreferenceTools.putValuetoSP(MainActivity.this, "DeviceNum", "");
                MqttService.TOPIC_MEETING_LIST = editText.getText() + "_meetList";
                MqttService.TOPIC_MEETING_CUR = editText.getText() + "_currtMeet";
                break;
            case R.id.cur://查询
                List<MqttMeetingListBean> list = meetingListBeanDao.queryBuilder()
//                        .where(MqttMeetingListBeanDao.Properties.StartDate
////                        .eq(DateTimeUtil.getInstance().getCurrentDateMMDD()))
//                        .eq("06-27"))
                        .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                        .build().list();

                for (int i = 0; i < list.size(); i++) {
                    Log.d("====", "query: " + list.get(i).toString());
                }
                break;
            case R.id.today://插入
                try {
                    mqttMeetingListBean = new MqttMeetingListBean(null,
                            ++i,
                            roomNum,
                            "会议室" + i,
                            "主题" + i,
                            "1",
                            System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            2,
                            "张三" + i,
                            "insert");
                    meetingListBeanDao.insert(mqttMeetingListBean);

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.w("===Exception", "插入失败" + e.getMessage());
                }
                break;
            case R.id.download:
                loadFile(appUrl);
                break;
        }
        //MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum)
        meetingListQuery.clear();
        meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum), MqttMeetingListBeanDao.Properties.StartDate
                .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                .build().list());
        fragmentCallBackA.TransDataA(MqttService.TOPIC_MEETING_LIST, meetingListQuery);
        fragmentCallBackB.TransDataB(MqttService.TOPIC_MEETING_LIST, meetingListQuery);
    }

    public static void setFragmentCallBackA(FragmentCallBackA callBack) {
        fragmentCallBackA = callBack;
    }

    public static void setFragmentCallBackB(FragmentCallBackB callBack) {
        fragmentCallBackB = callBack;
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

    private void loadFile(String url) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download2";
        GetRequest<File> request = OkGo.<File>get(url);
        DownloadTask task = OkDownload.request("taskTag", request)
                .save()
                .folder(path)
                .register(new DownloadListener("taskTag") {
                              @Override
                              public void onStart(Progress progress) {
                                  LogUtil.d("apk", "onStart");
                              }

                              @Override
                              public void onProgress(Progress progress) {

                                  LogUtil.d("apk", "onProgress");
                              }

                              @Override
                              public void onError(Progress progress) {
                                  LogUtil.d("apk", "onError");
                              }

                              @Override
                              public void onFinish(File file, Progress progress) {
                                  LogUtil.d("apk", file.getAbsolutePath());
                                  ApkUtils.install(MainActivity.this, file);
                              }

                              @Override
                              public void onRemove(Progress progress) {
                                  LogUtil.d("apk", "onRemove");
                              }
                          }
                );
//        task.start();//开始或者继续下载
//        task.pause();//暂停下载
//        task.remove();//删除下载，只删除记录，不删除文件
//        task.remove(true);//删除下载，同时删除记录和文件
        task.restart();//重新下载
    }

    public void checkVersion() {
        versionCodeLocal = Utils.getVersionCode(MainActivity.this);
        ToastUtils.showToast(MainActivity.this, "当前版本：" + versionCodeLocal);
        LogUtil.d("===", "开始检查版本更新");
//        OkGo.<String>get("http://192.168.10.120:8080/app/uploadVersionInfo")
        OkGo.<String>get(RequestApi.getUpdataAppUrl())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.d("===", response.body());
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            versionCodeOnLine = jsonObject.getString("code");
                            appUrl = jsonObject.getString("url");
                            String downUrl = (String) SharedPreferenceTools.getValueofSP(MainActivity.this, "ServiceIp", "");
                            if (downUrl.equals("")) {
                                ToastUtils.showToast(MainActivity.this, " ServcerIp设置有误！");
                                return;
                            } else {
                                appUrl = downUrl + appUrl;
                            }
                            if (versionCodeOnLine != null && appUrl != null) {
                                if (versionCodeOnLine != null) {
                                    if (Integer.parseInt(versionCodeOnLine) > versionCodeLocal) {
                                        ToastUtils.showToast(MainActivity.this, "发现新版本：" + versionCodeOnLine);
                                        ToastUtils.showToast(MainActivity.this, "开始自动更新...");
                                        loadFile(appUrl);
                                    }
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("====Main", "onDestroy is started");
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    long onclickfirst = 0;
    int onclick = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getEventTime() - onclickfirst < 500) {
                onclick++;
                onclickfirst = event.getEventTime();
                if (onclick == 4) {
                    //连续点击5次成功转到设置页面
//                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    CustomEidtDialog customEidtDialog = new CustomEidtDialog(MainActivity.this, intent);
                    customEidtDialog.show();
                    return false;
                }
            } else {
                onclickfirst = event.getEventTime();
                onclick = 0;
            }
        }
        return true;
    }
}
