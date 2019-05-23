package com.hskj.meettingsys.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.control.CodeConstants;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.utils.TimeThread;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class BFragment extends Fragment implements OnGetCurrentDateTimeListener {
    private static final String ARG_PARAM1 = "topic";
    private static final String ARG_PARAM2 = "strCurrentMeeting";
    private static final String ARG_PARAM3 = "strMeetingList";
String gggggg = "{\"success\":\"1\",\"result\":{\"weaid\":\"1\",\"days\":\"2019-05-23\",\"week\":\"星期四\",\"cityno\":\"beijing\",\"citynm\":\"北京\",\"cityid\":\"101010100\",\"temperature\":\"37℃/21℃\",\"temperature_curr\":\"26℃\",\"humidity\":\"34%\",\"aqi\":\"51\",\"weather\":\"晴\",\"weather_curr\":\"晴\",\"weather_icon\":\"http://api.k780.com/upload/weather/d/0.gif\",\"weather_icon1\":\"\",\"wind\":\"东风\",\"winp\":\"2级\",\"temp_high\":\"37\",\"temp_low\":\"21\",\"temp_curr\":\"26\",\"humi_high\":\"0\",\"humi_low\":\"0\",\"weatid\":\"1\",\"weatid1\":\"\",\"windid\":\"2\",\"winpid\":\"2\",\"weather_iconid\":\"0\"}}";
    private String topic;
    private String strCurrentMeeting, strMeetingList;
    private View convertView;
    private Context context;
    private ListView meeting_list;
    private List<MqttMeetingListBean> list = new ArrayList<>();
    private MqttMeetingCurrentBean currentBean = new MqttMeetingCurrentBean();
    private MeetingAdapter adapter;
    private TextView timeTv, dataTv, roomName, meetingName, meetingTime, meeting_bumen;
    private DateTimeUtil dateTimeUtil;
    private ImageView imageView;
    private TimeThread timeThread;
    private JSONObject jsonObject;

    public BFragment() {

    }

    public static BFragment newInstance(String topic, String strCurrentMeeting, String strMeetingList) {
        BFragment fragment = new BFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, topic);
        args.putString(ARG_PARAM2, strCurrentMeeting);
        args.putString(ARG_PARAM3, strMeetingList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topic = getArguments().getString(ARG_PARAM1);
            strCurrentMeeting = getArguments().getString(ARG_PARAM2);
            strMeetingList = getArguments().getString(ARG_PARAM3);
            Log.i("=====BFragment收到的", "topic:" + topic + "；strCurrentMeeting:" + strCurrentMeeting + "；strMeetingList:" + strMeetingList);
            switch (topic) {
                case ""://当前会议数据
                    jsonObject = JSON.parseObject(strCurrentMeeting);
                    break;
                case "002_meetList"://会议列表
                    list = JSON.parseArray(strMeetingList, MqttMeetingListBean.class);
//                    list = JSON.parseArray(gggggg, MqttMeetingListBean.class);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        convertView = inflater.inflate(R.layout.fragment_form_b, container, false);
        context = getActivity();
        initViews(convertView);

        dateTimeUtil = DateTimeUtil.getInstance();
        timeThread = new TimeThread(BFragment.this);
        timeThread.start();

        adapter = new MeetingAdapter(context, list);
        meeting_list.setAdapter(adapter);

        return convertView;
    }

    private void initViews(View view) {
        meeting_list = view.findViewById(R.id.meeting_listb);
        timeTv = view.findViewById(R.id.timeb);
        dataTv = view.findViewById(R.id.datab);
        imageView = view.findViewById(R.id.action_image);
        roomName = view.findViewById(R.id.current_room_name_b);
        meetingName = view.findViewById(R.id.current_meeting_name_b);
        meetingTime = view.findViewById(R.id.current_meeting_time_b);
        meeting_bumen = view.findViewById(R.id.current_meeting_bm_b);
        if(jsonObject != null && !"".equals(jsonObject)){
            roomName.setText(jsonObject.getString("name"));
            String startTime = DateTimeUtil.getInstance().transTimeToHHMM(Long.parseLong(jsonObject.getString("startTime")));
            String endTime = DateTimeUtil.getInstance().transTimeToHHMM(Long.parseLong(jsonObject.getString("endTime")));
            meetingTime.setText(startTime+"-"+endTime);
            meeting_bumen.setText(jsonObject.getString("department"));
        }
    }

    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDate() + "\t\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
