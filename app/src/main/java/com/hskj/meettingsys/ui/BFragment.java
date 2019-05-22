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

import com.hskj.meettingsys.bean.MqttMeetingListBean;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.utils.TimeThread;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.utils.DateTimeUtil;

import java.util.ArrayList;

public class BFragment extends Fragment implements OnGetCurrentDateTimeListener{
    private static final String ARG_PARAM1 = "topic";
    private static final String ARG_PARAM2 = "jsonStr";

    private String topic;
    private String jsonStr;
    private View convertView;
    private Context context;
    private ListView meeting_list;
    private ArrayList<MqttMeetingListBean> list = new ArrayList<>();
    private MeetingAdapter adapter;
    private TextView timeTv, dataTv,roomName,meetingName,meetingTimeBm;
    private DateTimeUtil dateTimeUtil;
    private ImageView imageView;
    private TimeThread timeThread;

    public BFragment() {

    }

    public static BFragment newInstance(String topic, String jsonStr) {
        BFragment fragment = new BFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, topic);
        args.putString(ARG_PARAM2, jsonStr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topic = getArguments().getString(ARG_PARAM1);
            jsonStr = getArguments().getString(ARG_PARAM2);
            Log.i("=====BFragment收到的", "topic:" + topic +"；jsonStr:"+jsonStr);
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

        return convertView;
    }
    private void initViews(View view) {
        meeting_list = view.findViewById(R.id.meeting_listb);
        timeTv =view. findViewById(R.id.timeb);
        dataTv = view.findViewById(R.id.datab);
        imageView = view.findViewById(R.id.action_image);
        roomName = view.findViewById(R.id.current_room_name_b);
        meetingName = view.findViewById(R.id.current_meeting_name_b);
        meetingTimeBm = view.findViewById(R.id.current_meeting_time_bm_b);
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
