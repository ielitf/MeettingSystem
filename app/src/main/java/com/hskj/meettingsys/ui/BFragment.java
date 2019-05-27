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
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.TimeThread;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.utils.DateTimeUtil;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BFragment extends Fragment implements OnGetCurrentDateTimeListener {
    private static final String ARG_PARAM1 = "topic";
    private static final String ARG_PARAM2 = "strCurrentMeeting";
    private static final String ARG_PARAM3 = "strMeetingList";
    private String topic;
    private String strCurrentMeeting, strMeetingList;
    private View convertView;
    private Context context;
    private ListView meeting_listView;
    private List<MqttMeetingListBean> meetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> curMeetingList = new ArrayList<>();
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
            Log.i("=====B模板", "topic:" + topic + "；strCurrentMeeting:" + strCurrentMeeting + "；strMeetingList:" + strMeetingList);
            if(MqttService.TOPIC_MEETING_CUR.equals(topic)){//当前会议数据
                if(strCurrentMeeting != null){
                    curMeetingList = JSON.parseArray(strCurrentMeeting, MqttMeetingCurrentBean.class);
                    Log.i("====BcurMeetingList==", curMeetingList.toString());
                }
            }
            if(MqttService.TOPIC_MEETING_LIST.equals(topic)){//会议列表
                if(strMeetingList != null){
                    meetingList = JSON.parseArray(strMeetingList, MqttMeetingListBean.class);
                }
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

        if(meetingList.size()>0){
            adapter = new MeetingAdapter(context, meetingList);
            meeting_listView.setAdapter(adapter);
        }

        return convertView;
    }

    private void initViews(View view) {
        meeting_listView = view.findViewById(R.id.meeting_listb);
        timeTv = view.findViewById(R.id.timeb);
        dataTv = view.findViewById(R.id.datab);
        imageView = view.findViewById(R.id.action_image);
        roomName = view.findViewById(R.id.current_room_name_b);
        meetingName = view.findViewById(R.id.current_meeting_name_b);
        meetingTime = view.findViewById(R.id.current_meeting_time_b);
        meeting_bumen = view.findViewById(R.id.current_meeting_bm_b);

        if(curMeetingList.size() > 0){
            //设置当前会议数据
            roomName.setText(curMeetingList.get(0).getRoomName());
            meetingName.setText(curMeetingList.get(0).getMeetingName());
            String startTime = DateTimeUtil.getInstance().transTimeToHHMM(curMeetingList.get(0).getStartDate());
            String endTime = DateTimeUtil.getInstance().transTimeToHHMM(curMeetingList.get(0).getEndDate());
            meetingTime.setText(startTime+"-"+endTime);
            meeting_bumen.setText(curMeetingList.get(0).getDepartment());
        }else {
            roomName.setText("会议室");
            meetingName.setText("");
            meetingTime.setText("");
            meeting_bumen.setText("");
        }
    }

    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDateYYMMDD() + "\t\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
