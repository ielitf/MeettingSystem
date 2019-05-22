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
import com.hskj.meettingsys.adapter.MeetingAdapterA;
import com.hskj.meettingsys.listener.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.utils.TimeThread;
import com.hskj.meettingsys.adapter.MeetingAdapter;
import com.hskj.meettingsys.bean.MeetingData;
import com.hskj.meettingsys.bean.MeetingItemBean;
import com.hskj.meettingsys.utils.DateTimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AFragment extends Fragment implements OnGetCurrentDateTimeListener{
    private static final String ARG_PARAM1 = "topic";
    private static final String ARG_PARAM2 = "jsonStr";

    private String topic;
    private String jsonStr;
    private View convertView;
    private Context context;
    private ListView meeting_list;
    private List<MeetingItemBean> list = new ArrayList<>();
    private MeetingAdapterA adapter;
    private TextView timeTv, dataTv;
    private DateTimeUtil dateTimeUtil;

    private TimeThread timeThreadUtil;

    public AFragment() {

    }
    public static AFragment newInstance(String topic, String jsonStr) {
        AFragment fragment = new AFragment();
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
            Log.i("=====AFragment收到的", "topic:" + topic +"；jsonStr:"+jsonStr);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        convertView = inflater.inflate(R.layout.fragment_form_a, container, false);
        context = getActivity();
        initViews(convertView);
        initdata();
        dateTimeUtil = DateTimeUtil.getInstance();
        timeThreadUtil = new TimeThread(AFragment.this);
        timeThreadUtil.start();

        return convertView;
    }
    private void initViews(View view) {
        meeting_list = view.findViewById(R.id.meeting_lista);
        timeTv =view. findViewById(R.id.timea);
        dataTv = view.findViewById(R.id.dataa);
    }

    private void initdata() {
        for (int i = 0; i < MeetingData.meeting_data_day.length; i++) {
            list.add(new MeetingItemBean(MeetingData.meeting_data_day[i], MeetingData.meeting_data_hour[i], MeetingData.meeting_title[i], MeetingData.meeting_order[i]));
        }
        adapter = new MeetingAdapterA(context, list);
        meeting_list.setAdapter(adapter);
    }

    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDate() + "\t\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }

    @Override
    public void onDestroy() {
        Log.i("=====onDestroyaaaaaaa", "onDestroyaaaaaaa");
        super.onDestroy();
    }
}
