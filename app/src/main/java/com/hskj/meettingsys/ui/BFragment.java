package com.hskj.meettingsys.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hskj.meettingsys.IGetMessageCallBack;
import com.hskj.meettingsys.MqttManager;
import com.hskj.meettingsys.OnGetCurrentDateTimeListener;
import com.hskj.meettingsys.OnGetMQTTMessageListener;
import com.hskj.meettingsys.R;
import com.hskj.meettingsys.TimeThread;

import java.util.ArrayList;

public class BFragment extends Fragment implements OnGetCurrentDateTimeListener, OnGetMQTTMessageListener, IGetMessageCallBack {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View convertView;
    private Context context;

    private ListView meeting_list;
    private ArrayList<MeetingItemBean> list = new ArrayList<>();
    private MeetingAdapter adapter;
    private TextView timeTv, dataTv;
    private DateTimeUtil dateTimeUtil;
    private MqttManager mqttManager;
    private ImageView imageView;

    private TimeThread timeThreadUtil;
    private MyMessageTask myMessageTask;
    private static  boolean getMessage;

    public BFragment() {

    }

    public static AFragment newInstance(String param1, String param2) {
        AFragment fragment = new AFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        convertView = inflater.inflate(R.layout.fragment_form_b, container, false);
        context = getActivity();
        getMessage = true;
        Log.i("=====onCreateViewBBB", "onCreateViewBBB");
        initViews(convertView);
        initdata();
        dateTimeUtil = DateTimeUtil.getInstance();
        timeThreadUtil = new TimeThread(BFragment.this);
        timeThreadUtil.start();

        mqttManager = new MqttManager(context);
        mqttManager.setIGetMessageCallBack(BFragment.this);

        myMessageTask = new MyMessageTask();
        myMessageTask.execute();

        return convertView;
    }
    private void initViews(View view) {
        meeting_list = view.findViewById(R.id.meeting_list);
        timeTv =view. findViewById(R.id.time);
        dataTv = view.findViewById(R.id.data);
        imageView = view.findViewById(R.id.action_image);
    }

    private void initdata() {
        for (int i = 0; i < MeetingData.meeting_data_day.length; i++) {
            list.add(new MeetingItemBean(MeetingData.meeting_data_day[i], MeetingData.meeting_data_hour[i], MeetingData.meeting_title[i], MeetingData.meeting_order[i]));
        }
        adapter = new MeetingAdapter(context, list);
        meeting_list.setAdapter(adapter);
//        getWheatherData();
    }

    @Override
    public void setMessage(String message) {
        Log.i("=====收到的message11：", "BFragment+setMessage:" + message);
    }

    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDate() + "\t\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }

    @Override
    public void onGetMQTTMessage() {

    }

    private class MyMessageTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (getMessage) {
                        try {
                            mqttManager.connect();
                            mqttManager.subscribe("ZhangHaoTopic_ggggqqq", 0);
                            mqttManager.publish("ZhangHaoTopic_ggggqqq", "hello mqtt111111111111", false, 0);
                            Thread.sleep(20000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }



    @Override
    public void onDestroy() {
        Log.i("=====onDestroybbbbbbb", "onDestroybbbbbbbbb");
        myMessageTask.cancel(true);
        myMessageTask = null;
        getMessage = false;
        super.onDestroy();
    }
}
