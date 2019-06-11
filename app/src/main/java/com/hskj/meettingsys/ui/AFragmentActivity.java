package com.hskj.meettingsys.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.bean.MqttMeetingCurrentBean;
import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.listener.FragmentCallBack;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.utils.DateTimeUtil;
import com.hskj.meettingsys.utils.MqttService;
import com.hskj.meettingsys.utils.TimeThread;

import java.util.ArrayList;
import java.util.List;

public class AFragmentActivity extends AppCompatActivity  implements OnGetCurrentDateTimeListener, FragmentCallBack {
    private ListView meeting_listView;
    private List<MqttMeetingListBean> myMeetingList = new ArrayList<>();
    private List<MqttMeetingCurrentBean> myCurMeetingList = new ArrayList<>();
    private MeetingAdapter adapter;
    private TextView timeTv, dataTv, roomName, meetingName, meetingTime, meeting_bumen;
    private DateTimeUtil dateTimeUtil;
    private TimeThread timeThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_form_a);
        initViews();
        dateTimeUtil = DateTimeUtil.getInstance();
        timeThread = new TimeThread(this);
        timeThread.start();
        MainActivity.setFragmentCallBack(this);
    }
    @Override
    public void TransData(String topic, String strJsonMessage) {
//        Toast.makeText(this,"有消息来啦",Toast.LENGTH_SHORT).show();
        Log.w("========AFragment", "topic:" + topic +";----MqttService.TOPIC_MEETING_CUR:" + MqttService.TOPIC_MEETING_CUR +";----strMessage:" + strJsonMessage);

        if (topic.equals(MqttService.TOPIC_MEETING_CUR)) {//当前会议
            Log.w("=======", "当前会议");
            if (strJsonMessage != null) {
                myCurMeetingList = JSON.parseArray(strJsonMessage, MqttMeetingCurrentBean.class);
                Log.w("*****CurMeeting.size()", myCurMeetingList.size()+"");
                if (myCurMeetingList.size() > 0) {
                    //设置当前会议数据
                    roomName.setText(myCurMeetingList.get(0).getRoomName());
                    meetingName.setText(myCurMeetingList.get(0).getMeetingName());
                    String startTime = DateTimeUtil.getInstance().transTimeToHHMM(myCurMeetingList.get(0).getStartDate());
                    String endTime = DateTimeUtil.getInstance().transTimeToHHMM(myCurMeetingList.get(0).getEndDate());
                    meetingTime.setText(startTime + "-" + endTime);
                    meeting_bumen.setText(myCurMeetingList.get(0).getDepartment());
                } else {
                    roomName.setText("XX会议室");
                    meetingName.setText("当前无会议");
                    meetingTime.setText("");
                    meeting_bumen.setText("");
                }
            }
        }

        if (topic.equals(MqttService.TOPIC_MEETING_LIST)) {//今日会议
            Log.w("=======", "今日会议");
            if (strJsonMessage != null) {
                myMeetingList = JSON.parseArray(strJsonMessage, MqttMeetingListBean.class);
                Log.w("*****MeetingList.size", myMeetingList.size()+"");
                if (myMeetingList.size() > 0) {
//                    if (adapter == null) {
                    adapter = new MeetingAdapter(this, myMeetingList);
                    meeting_listView.setAdapter(adapter);
//                    } else {
//                        adapter.notifyDataSetChanged();
//                    }
                }
            }
        }
    }
    private void initViews() {
        meeting_listView =findViewById(R.id.meeting_list_a);
        timeTv = findViewById(R.id.timea);
        dataTv = findViewById(R.id.dataa);
        roomName = findViewById(R.id.current_room_name_a);
        meetingName =findViewById(R.id.current_meeting_name_a);
        meetingTime = findViewById(R.id.current_meeting_time_a);
        meeting_bumen = findViewById(R.id.current_meeting_bm_a);
    }
    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDateYYMMDD() + "\t\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }
}
