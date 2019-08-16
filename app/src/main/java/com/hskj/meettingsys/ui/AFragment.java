package com.hskj.meettingsys.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hskj.meettingsys.K780.K780Utils;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.adapter.MeetingAdapterA;
import com.hskj.meettingsys.adapter.WeatherAdapter;
import com.hskj.meettingsys.bean.MeetingData;
import com.hskj.meettingsys.bean.MeetingItemBean;
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.bean.WeatherBean;
import com.hskj.meettingsys.control.CodeConstants;
import com.hskj.meettingsys.greendao.DaoMaster;
import com.hskj.meettingsys.greendao.DaoSession;
import com.hskj.meettingsys.greendao.MqttMeetingListBeanDao;
import com.hskj.meettingsys.listener.DataBaseQueryListenerA;
import com.hskj.meettingsys.listener.FragmentCallBackA;
import com.hskj.meettingsys.utils.DateTimeUtil;
import com.hskj.meettingsys.utils.IPAddressUtils;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.SharedPreferenceTools;
import com.hskj.meettingsys.utils.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AFragment extends Fragment implements FragmentCallBackA , DataBaseQueryListenerA {
    private GridView gridView;
    private DaoMaster daoMaster;
    private WeatherAdapter weatherAdapter;
    private Context context;
    private ListView meeting_listView;
    private List<MqttMeetingListBean> meetingListQuery = new ArrayList<>();
    private List<WeatherBean> weatherList = new ArrayList<>();
    private List<MeetingItemBean> jiaMeetingList = new ArrayList<>();
    private List<MqttMeetingListBean> myMeetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private MeetingAdapter adapter = null;
    private MeetingAdapterA jiaAdapter = null;
    private TextView timeTv, dataTv, roomName, meetingName, meetingTime, meeting_bumen, room_num,versionTV;
    private DateTimeUtil dateTimeUtil;
    private long delayTime = 3000;//listView列表比较多时，自动滚动的时间间隔
    private long weathetUpdataTime = 3600 * 1000;//天气定时更新
    private Timer checkWeaherTimer, checkCurMeetingTime;
    private CheckCurMeetingTask checkCurMeetingTask;
    private static long durationTime;//当前会议剩余持续时间
    private MyWeatherTask checkWeatherTask;
    private DaoSession daoSession;
    private String ip;
    private MqttMeetingListBeanDao meetingListBeanDao;
    private static String JsonStringCurMeet;
    private float f_density;
    private int int_density;
    private static String roomNum;//会议室编号
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (myCurMeetingList.size() > 0) {
                        //设置当前会议数据
                        roomName.setText(myCurMeetingList.get(0).getRoomName());
                        String startTime = DateTimeUtil.getInstance().transTimeToHHMM(myCurMeetingList.get(0).getStartDate());
                        String endTime = DateTimeUtil.getInstance().transTimeToHHMM(myCurMeetingList.get(0).getEndDate());
                        meetingTime.setText(startTime + "-" + endTime);
                        if (myCurMeetingList.get(0).getIsOpen().equals("1")) {
                            meetingName.setText(myCurMeetingList.get(0).getMeetingName());
                            meeting_bumen.setText("");
                        } else {
                            meetingName.setText("未公开");
                            meeting_bumen.setText("");
                        }

                        if (checkCurMeetingTask != null) {
                            checkCurMeetingTask.cancel();
                            checkCurMeetingTask = null;
                        }
                        if (checkCurMeetingTime != null) {
                            checkCurMeetingTime.purge();
                            checkCurMeetingTime.cancel();
                            checkCurMeetingTime = null;
                        }
                        checkCurMeetingTime = new Timer();
                        checkCurMeetingTask = new CheckCurMeetingTask();
                        LogUtil.d("===", "开始计时：当前会议准备清零");
                        checkCurMeetingTime.schedule(checkCurMeetingTask, durationTime);//在会议结束后，显示当前无会议

                    } else {
                        roomName.setText("会议室");
                        meetingName.setText("当前无会议");
                        meetingTime.setText("");
                        meeting_bumen.setText("");
                    }
                    break;
                case 2:
                    roomName.setText((CharSequence) SharedPreferenceTools.getValueofSP(context, CodeConstants.MEETING_ROOM_NAME,"会议室"));
                    if (adapter == null) {
                        adapter = new MeetingAdapter(context, myMeetingList);
                        meeting_listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 3:
                    LogUtil.d("===", "清零");
                    meetingName.setText("当前无会议");
                    meetingTime.setText("");
                    meeting_bumen.setText("");
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable run_scroll_up = new Runnable() {
        @Override
        public void run() {
            meeting_listView.smoothScrollBy(int_density, 1000);
            handler.postDelayed(run_scroll_up, delayTime);
        }
    };

    private Runnable startFromTop = new Runnable() {
        @Override
        public void run() {
            meeting_listView.smoothScrollToPosition(0);
        }
    };

    /**
     * 向上滚动
     */
    public void listScrollUp() {
        handler.removeCallbacks(run_scroll_up);
        handler.postDelayed(run_scroll_up, delayTime);
    }

    public AFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_form_a, container, false);
        context = getActivity();
        f_density = context.getResources().getDisplayMetrics().density;
        int_density = (int) (f_density * 40);
        initViews(convertView);
        getStuDao();//初始化数据库
        dateTimeUtil = DateTimeUtil.getInstance();

        //下载天气数据，并且1小时更新一次
//        loadWeatherData();
//        checkWeaherTimer = new Timer();
//        checkWeatherTask = new MyWeatherTask();
//        checkWeaherTimer.schedule(checkWeatherTask,0, 1 * 3600 * 1000);
        CustomEidtDialog.setOnDataBaseQueryListenerA(this);
        MainActivity.setFragmentCallBackA(this);
        listScrollUp();

        meeting_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = meeting_listView.getChildAt(meeting_listView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == meeting_listView.getHeight()) {
                        handler.postDelayed(startFromTop, delayTime);
                    }
                }
            }
        });
        LogUtil.d("===", "模板A准备就绪");
        inintData();//从数据库中查询今日会议数据，并更新天气
        return convertView;
    }

    /**
     * 从数据库中查询今日会议数据和更新天气
     */
    private void inintData() {
        roomNum= (String) SharedPreferenceTools.getValueofSP(context,"DeviceNum","");//获取会议室编号
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                meetingListQuery.clear();
                meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum),MqttMeetingListBeanDao.Properties.StartDate
                        .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                        .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                        .build().list());
                if (meetingListQuery.size() >= 0) {
//                    TransDataA(MqttService.TOPIC_MEETING_LIST, meetingListQuery);
                    myMeetingList.clear();
                    myMeetingList.addAll(meetingListQuery);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
                //更新天气
                loadWeatherData();
            }
        }, 60, 60 * 1000 * 15);
    }

    private void loadWeatherData() {
        ip = IPAddressUtils.getAndroidIp(context);
        LogUtil.i("===设备IP：", ip);
        OkGo.<String>get("http://api.k780.com:88/?")
                .params("app", "weather.future")
//                .params("weaid", ip)
                .params("weaid", 2277)//涞水
                .params("appkey", K780Utils.APPKEY)
                .params("sign", K780Utils.SIGN)
                .params("format", "json")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        LogUtil.w("=========天气", response.body());
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                String content = jsonObject.getString("result");
                                weatherList.clear();
                                weatherList.addAll(JSON.parseArray(content, WeatherBean.class));
                                if (weatherAdapter == null) {
                                    weatherAdapter = new WeatherAdapter(context, weatherList);
                                    gridView.setAdapter(weatherAdapter);
                                } else {
                                    weatherAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 当切换会议室编号后，通知系统重新查询数据库，并更新页面
     * @param roomNum
     */
    @Override
    public void onDataBaseQueryListenerA(String roomNum) {
        room_num.setText("会议室编号：" + roomNum);
        //设置当前会议
        if (checkCurMeetingTask != null) {
            checkCurMeetingTask.cancel();
            checkCurMeetingTask = null;
        }
        if (checkCurMeetingTime != null) {
            checkCurMeetingTime.purge();
            checkCurMeetingTime.cancel();
            checkCurMeetingTime = null;
        }
        checkCurMeetingTime = new Timer();
        checkCurMeetingTask = new CheckCurMeetingTask();
        checkCurMeetingTime.schedule(checkCurMeetingTask, 0);//在会议结束后，显示当前无会议

        //查询今日会议
        meetingListQuery.clear();
        meetingListQuery.addAll(meetingListBeanDao.queryBuilder().where(MqttMeetingListBeanDao.Properties.RoomNum.eq(roomNum),MqttMeetingListBeanDao.Properties.StartDate
                .between(dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 00:00:00"), dateTimeUtil.transDataToTime(dateTimeUtil.getCurrentDateYYMMDD() + " 23:59:59")))
                .orderAsc(MqttMeetingListBeanDao.Properties.EndDate)
                .build().list());
            TransDataA(MqttService.TOPIC_MEETING_LIST, meetingListQuery);
    }

    @Override
    public void TransDataA(String topic, List mList) {
        LogUtil.w("========AFragment", "topic:" + topic + ";----mList:" + mList.toString());
        if (topic.equals(MqttService.TOPIC_MEETING_CUR)) {//当前会议
            myCurMeetingList.clear();
            myCurMeetingList.addAll(mList);
            if (myCurMeetingList.size() > 0) {
                durationTime = myCurMeetingList.get(0).getEndDate() - System.currentTimeMillis();
                if(durationTime<0){
                    durationTime = 0;
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
        if (topic.equals(MqttService.TOPIC_MEETING_LIST)) {//今日会议
            myMeetingList.clear();
            myMeetingList.addAll(mList);
            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }

    private void initViews(View view) {
        room_num = view.findViewById(R.id.room_num);
        String DeviceNum = (String) SharedPreferenceTools.getValueofSP(getActivity(), "DeviceNum", "");
        room_num.setText("当前会议室编号：" + DeviceNum);
        meeting_listView = view.findViewById(R.id.meeting_list_a);
        timeTv = view.findViewById(R.id.timea);
        dataTv = view.findViewById(R.id.dataa);
        roomName = view.findViewById(R.id.current_room_name_a);
        roomName.setText((CharSequence) SharedPreferenceTools.getValueofSP(context, CodeConstants.MEETING_ROOM_NAME,"会议室"));
        meetingName = view.findViewById(R.id.current_meeting_name_a);
        meetingTime = view.findViewById(R.id.current_meeting_time_a);
        meeting_bumen = view.findViewById(R.id.current_meeting_bm_a);
        gridView = view.findViewById(R.id.weather_a);
        versionTV = view.findViewById(R.id.version_tv_a);
        versionTV.setText("v_" + Utils.getAppVersionName(context));
//        ViewTreeObserver observer = meetingName.getViewTreeObserver(); // textAbstract为TextView控件
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                ViewTreeObserver obs = meetingName.getViewTreeObserver();
//                obs.removeGlobalOnLayoutListener(this);
//                if (meetingName.getLineCount() >= 2) {
//                    meetingName.setTextSize(25);
//                }
//            }
//        });
    }

    private void getStuDao() {
        // 创建数据
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity(), "meetingList.db", null);
        daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
        meetingListBeanDao = daoSession.getMqttMeetingListBeanDao();
    }


    /**
     * 当前会议：会议结束后，若当前无会议，，显示无会议
     */
    class CheckCurMeetingTask extends TimerTask {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 3;
            handler.sendMessage(msg);
        }
    }

    /**
     * 定时更新天气，暂定1小时更新一次
     */
    class MyWeatherTask extends TimerTask {

        @Override
        public void run() {
            loadWeatherData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkWeatherTask != null) {
            checkWeatherTask.cancel();
            checkWeatherTask = null;
        }
        if (checkWeaherTimer != null) {
            checkWeaherTimer.purge();
            checkWeaherTimer.cancel();
            checkWeaherTimer = null;
        }
        if (checkCurMeetingTask != null) {
            checkCurMeetingTask.cancel();
            checkCurMeetingTask = null;
        }
        if (checkCurMeetingTime != null) {
            checkCurMeetingTime.purge();
            checkCurMeetingTime.cancel();
            checkCurMeetingTime = null;
        }
    }
}
