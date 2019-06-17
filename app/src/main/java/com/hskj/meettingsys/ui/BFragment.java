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
import com.hskj.meettingsys.bean.JiaWeatherBean;
import com.hskj.meettingsys.bean.MeetingData;
import com.hskj.meettingsys.bean.MeetingItemBean;
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.bean.WeatherBean;
import com.hskj.meettingsys.bean.WeatherData;
import com.hskj.meettingsys.listener.FragmentCallBackA;
import com.hskj.meettingsys.listener.FragmentCallBackB;
import com.hskj.meettingsys.listener.FragmentCallBackBCur;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.utils.DateTimeUtil;
import com.hskj.meettingsys.utils.IPAddressUtils;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.SDCardUtils;
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

public class BFragment extends Fragment implements OnGetCurrentDateTimeListener, FragmentCallBackB, FragmentCallBackBCur {
    private GridView gridView;
    private WeatherAdapter weatherAdapter;
    private Context context;
    private ListView meeting_listView;
    private List<JiaWeatherBean> jiaWeatherList = new ArrayList<>();
    private List<WeatherBean> weatherList = new ArrayList<>();
    private List<MeetingItemBean> jiaMeetingList = new ArrayList<>();
    private MeetingAdapterA jiaAdapter = null;
    private List<MqttMeetingListBean> myMeetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private MeetingAdapter adapter = null;
    private TextView timeTv, dataTv, roomName, meetingName, meetingTime, meeting_bumen, room_num;
    private DateTimeUtil dateTimeUtil;
    private TimeThread timeThread;
    private long delayTime = 3000;//listView列表比较多时，自动滚动的时间间隔
    private long weathetUpdataTime = 3600 * 1000;//天气定时更新
    private Timer timer;
    private MyWeatherTask task;
    private String ip;
    private static String JsonStringCurMeet;
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
                    } else {
                        roomName.setText("会议室");
                        meetingName.setText("当前无会议");
                        meetingTime.setText("");
                        meeting_bumen.setText("");
                    }
//                    try {
//                        LogUtil.w("========BFragment",  ";----JsonStringCurMeet:" + JsonStringCurMeet);
//                        JSONObject jsonObject = new JSONObject(JsonStringCurMeet);
//                        roomName.setText(jsonObject.getString("roomName"));
//                        String startTime = DateTimeUtil.getInstance().transTimeToHHMM(jsonObject.getLong("startDate"));
//                        String endTime = DateTimeUtil.getInstance().transTimeToHHMM(jsonObject.getLong("endDate"));
//                        meetingTime.setText(startTime + "-" + endTime);
//                        if (jsonObject.getString("isOpen").equals("1")) {
//                            meetingName.setText(jsonObject.getString("meetingName"));
//                        } else {
//                            meetingName.setText("未公开");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
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

    public BFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_form_b, container, false);
        context = getActivity();
        initViews(convertView);
        dateTimeUtil = DateTimeUtil.getInstance();
        timeThread = new TimeThread(BFragment.this);
        timeThread.start();
        loadWeatherData();
        MainActivity.setFragmentCallBackB(this);
        MainActivity.setFragmentCallBackBCur(this);
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
//        initWeatherData()
        return convertView;
    }

    private void loadWeatherData() {
        timingAgain();
        ip = IPAddressUtils.getAndroidIp(context);
        LogUtil.i("===", ip);
        OkGo.get("http://api.k780.com:88/?")
                .params("app", "weather.future")
                .params("weaid", ip)
                .params("appkey", K780Utils.APPKEY)
                .params("sign", K780Utils.SIGN)
                .params("format", "json")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogUtil.w("=========天气", s);
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String content = jsonObject.getString("result");
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
    public void TransDataBCur(String topic, String jsonStr) {
        LogUtil.w("========BFragment", "topic:" + topic + ";----jsonStr:" + jsonStr);
        JsonStringCurMeet = jsonStr;
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    @Override
    public void TransDataB(String topic, List mList) {
        LogUtil.w("========BFragment", "topic:" + topic + ";----mList:" + mList.toString());
        if (topic.equals(MqttService.TOPIC_MEETING_CUR)) {//当前会议
            myCurMeetingList.clear();
            myCurMeetingList.addAll(mList);
            if (myCurMeetingList.size() > 0) {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
        if (topic.equals(MqttService.TOPIC_MEETING_LIST)) {//今日会议
            myMeetingList.clear();
            myMeetingList.addAll(mList);
            if (myMeetingList.size() > 0) {
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }
    }

    private void initViews(View view) {
        room_num = view.findViewById(R.id.room_num);
        room_num.setText("当前会议室编号：" + SDCardUtils.readTxt() + "");
        meeting_listView = view.findViewById(R.id.meeting_listb);
        timeTv = view.findViewById(R.id.timeb);
        dataTv = view.findViewById(R.id.datab);
        roomName = view.findViewById(R.id.current_room_name_b);
        meetingName = view.findViewById(R.id.current_meeting_name_b);
        meetingTime = view.findViewById(R.id.current_meeting_time_b);
        meeting_bumen = view.findViewById(R.id.current_meeting_bm_b);
        gridView = view.findViewById(R.id.weather_b);
    }

    private void initWeatherData() {
        for (int i = 0; i < WeatherData.weather_day.length; i++) {
            jiaWeatherList.add(new JiaWeatherBean(WeatherData.weather_day[i], WeatherData.weather_icon[i], WeatherData.weather_tem_h[i], WeatherData.weather_tem_l[i]));
        }
//        weatherAdapter = new WeatherAdapter(context,jiaWeatherList);
//        gridView.setAdapter(weatherAdapter);
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
