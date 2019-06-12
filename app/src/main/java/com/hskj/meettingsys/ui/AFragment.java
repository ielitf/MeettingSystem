package com.hskj.meettingsys.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.adapter.MeetingAdapterA;
import com.hskj.meettingsys.adapter.WeatherAdapter;
import com.hskj.meettingsys.bean.MeetingData;
import com.hskj.meettingsys.bean.MeetingItemBean;
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.bean.JiaWeatherBean;
import com.hskj.meettingsys.bean.WeatherBean;
import com.hskj.meettingsys.bean.WeatherData;
import com.hskj.meettingsys.listener.FragmentCallBack;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.utils.DateTimeUtil;
import com.hskj.meettingsys.utils.IPAddressUtils;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.TimeThread;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

public class AFragment extends Fragment implements OnGetCurrentDateTimeListener, FragmentCallBack {
    private GridView gridView;
    private WeatherAdapter weatherAdapter;
    private Context context;
    private ListView meeting_listView;
    private List<JiaWeatherBean> jiaWeatherList = new ArrayList<>();
    private List<WeatherBean> weatherList = new ArrayList<>();
    private List<MeetingItemBean> jiaMeetingList = new ArrayList<>();
    private List<MqttMeetingListBean> myMeetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private MeetingAdapter adapter = null;
    private MeetingAdapterA jiaAdapter = null;
    private TextView timeTv, dataTv, roomName, meetingName, meetingTime, meeting_bumen;
    private DateTimeUtil dateTimeUtil;
    private TimeThread timeThread;
    private long delayTime = 3000;//listView列表比较多时，自动滚动的时间间隔
    private long weathetUpdataTime = 3600*1000;//天气定时更新
    private Timer timer;
    private MyWeatherTask task;
    private String ip;
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
                            meeting_bumen.setText(myCurMeetingList.get(0).getDepartment());
                        } else {
                            meetingName.setText("未公开");
                            meeting_bumen.setText("");
                        }
                    } else {
                        roomName.setText("会议室");
                        meetingName.setText("当前无会议");
                        meetingTime.setText("");
                        meeting_bumen.setText("");
                    }
                    break;
                case 2:
                    if (myMeetingList.size() > 0) {
                        if (adapter == null) {
                            adapter = new MeetingAdapter(context, myMeetingList);
                            meeting_listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case 3:
                    loadWeatherData();
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable run_scroll_up = new Runnable() {
        @Override
        public void run() {
            meeting_listView.smoothScrollBy(30, 1000);
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
        initViews(convertView);
        dateTimeUtil = DateTimeUtil.getInstance();
        timeThread = new TimeThread(AFragment.this);
        timeThread.start();
        loadWeatherData();
        MainActivity.setFragmentCallBack(this);
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
//        initData();
//        initWeatherData();
        return convertView;
    }

    private void loadWeatherData() {
        timingAgain();
        ip = IPAddressUtils.getAndroidIp(context);
        LogUtil.i("===", ip);
        OkGo.get("https://www.tianqiapi.com/api/?")
                .params("version", "v1")
//                .params("cityid","101090201")
//                .params("city","北京")
                .params("ip", ip)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String content = jsonObject.getString("data");
                                weatherList.clear();
                                weatherList.addAll(JSON.parseArray(content, WeatherBean.class));
                                weatherAdapter = new WeatherAdapter(context, weatherList);
                                gridView.setAdapter(weatherAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 添加假数据
     */
    private void initData() {
        for (int i = 0; i < MeetingData.meeting_title.length; i++) {
            jiaMeetingList.add(new MeetingItemBean(MeetingData.meeting_data_day[i], MeetingData.meeting_data_hour[i], MeetingData.meeting_title[i], MeetingData.meeting_order[i]));
        }
        jiaAdapter = new MeetingAdapterA(context, jiaMeetingList);
        meeting_listView.setAdapter(jiaAdapter);
    }

    @Override
    public void TransData(String topic, String strJsonMessage) {
        LogUtil.w("========AFragment", "topic:" + topic + ";----MqttService.TOPIC_MEETING_CUR:" + MqttService.TOPIC_MEETING_CUR + ";----strMessage:" + strJsonMessage);

        if (topic.equals(MqttService.TOPIC_MEETING_CUR)) {//当前会议
            LogUtil.w("=======", "当前会议");
            if (strJsonMessage != null) {
                myCurMeetingList = JSON.parseArray(strJsonMessage, MqttMeetingCurrentBean.class);
                LogUtil.w("*****CurMeeting.size()", myCurMeetingList.size() + "");
                if (myCurMeetingList.size() > 0) {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        }

        if (topic.equals(MqttService.TOPIC_MEETING_LIST)) {//今日会议
            LogUtil.w("=======", "今日会议");
            if (strJsonMessage != null) {
                myMeetingList = JSON.parseArray(strJsonMessage, MqttMeetingListBean.class);
                LogUtil.w("*****MeetingList.size", myMeetingList.size() + "");
                if (myMeetingList.size() > 0) {
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private void initViews(View view) {
        meeting_listView = view.findViewById(R.id.meeting_list_a);
        timeTv = view.findViewById(R.id.timea);
        dataTv = view.findViewById(R.id.dataa);
        roomName = view.findViewById(R.id.current_room_name_a);
        meetingName = view.findViewById(R.id.current_meeting_name_a);
        meetingTime = view.findViewById(R.id.current_meeting_time_a);
        meeting_bumen = view.findViewById(R.id.current_meeting_bm_a);
        gridView = view.findViewById(R.id.weather_a);
    }

    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDateYYMMDD() + "\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }

    /**
     * 定时更新天气，暂定1小时更新一次
     */
    class MyWeatherTask extends TimerTask {

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
    public void timingAgain() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        task = new MyWeatherTask();
        timer.schedule(task, weathetUpdataTime);
        LogUtil.i("===", "重新计时");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }
}
