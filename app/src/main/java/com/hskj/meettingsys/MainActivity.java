package com.hskj.meettingsys;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hskj.meettingsys.K780.K780Utils;
import com.hskj.meettingsys.ui.DateTimeUtil;
import com.hskj.meettingsys.ui.MeetingAdapter;
import com.hskj.meettingsys.ui.MeetingData;
import com.hskj.meettingsys.ui.MeetingItemBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnGetCurrentDateTimeListener, OnGetMQTTMessageListener, IGetMessageCallBack {
    private ListView meeting_list;
    private ArrayList<MeetingItemBean> list = new ArrayList<>();
    private MeetingAdapter adapter;
    private TextView timeTv, dataTv;
    private DateTimeUtil dateTimeUtil;
    private MqttManager mqttManager;
    private ImageView imageView;

    private MyServiceConnection serviceConnection;
    private MQTTService mqttService;
    private TimeThread timeThreadUtil;
    private MyMessageTask myMessageTask;
    private static  boolean getMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMessage = true;
        Log.i("=====onCreate1", "onCreate1");
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainActivity.this);
        initViews();
        initdata();
        dateTimeUtil = DateTimeUtil.getInstance();
        timeThreadUtil = new TimeThread(this);
        timeThreadUtil.start();

        mqttManager = new MqttManager(this);
        mqttManager.setIGetMessageCallBack(this);

        myMessageTask = new MyMessageTask();
        myMessageTask.execute();
    }

    private void initViews() {
        meeting_list = findViewById(R.id.meeting_list);
        timeTv = findViewById(R.id.time);
        dataTv = findViewById(R.id.data);
        imageView = findViewById(R.id.action_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });
    }

    private void initdata() {
        for (int i = 0; i < MeetingData.meeting_data_day.length; i++) {
            list.add(new MeetingItemBean(MeetingData.meeting_data_day[i], MeetingData.meeting_data_hour[i], MeetingData.meeting_title[i], MeetingData.meeting_order[i]));
        }
        adapter = new MeetingAdapter(this, list);
        meeting_list.setAdapter(adapter);
//        getWheatherData();
    }

    private void getWheatherData() {
        OkGo.get(K780Utils.WEATHER_URL).cacheKey(K780Utils.WEATHER_URL).cacheMode(CacheMode.DEFAULT).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("=====response", response.toString());
                Log.i("=====s", s);
                if (response.code() == 200) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        JSONObject result = obj.getJSONObject("result");

                        String citynm = result.getString("citynm");
                        String temperature = result.getString("temperature");
                        String temperature_curr = result.getString("temperature_curr");
                        String weather_curr = result.getString("weather_curr");
                        String weather_icon = result.getString("weather_icon");
                        Log.i("=====result", citynm);
                        Log.i("=====result", temperature);
                        Log.i("=====result", temperature_curr);
                        Log.i("=====result", weather_curr);
                        Log.i("=====result", weather_icon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.i("=====天气获取失败", e.toString());
            }
        });
    }


    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDate() + "\t\t" + dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }


    @Override
    protected void onDestroy() {
        Log.i("=====onDestroy1", "onDestroy1");
//        unbindService(serviceConnection);
        myMessageTask.cancel(true);
        myMessageTask = null;
        getMessage = false;
        super.onDestroy();
    }

    @Override
    public void setMessage(String message) {
        Log.i("=====收到的message11：", "MainActivity11+setMessage:" + message);
        mqttService = serviceConnection.getMqttService();
        mqttService.toCreateNotification(message);

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

//    @Override
//    public void setMessage(String message) {
//        Log.i("=====收到的message：", message);
//        mqttService = serviceConnection.getMqttService();
//        mqttService.toCreateNotification(message);
//
//
//    }
////    private class MyTask extends AsyncTask<Integer, Void, Weather> {
////
////        // 进度条对话框
////        ProgressDialog dialog;
////
////        @Override
////        protected void onPreExecute() {
//////            dialog = new ProgressDialog(MainActivity.this);
//////            dialog.setTitle("天气数据");
//////            dialog.setMessage("正在下载...");
//////            // 显示对话框
//////            dialog.show();
////        }
////
////        @Override
////        protected Weather doInBackground(Integer... params) {
////            return K780Utils.getOneDayWeather(params[0]);
////        }
////
////        @Override
////        protected void onPostExecute(Weather result) {
////            if(result == null) {
////                Toast.makeText(MainActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
////            } else {
////
////                Log.i("=====result",result.toString());
////
////            }
////            // 关闭对话框
//////            dialog.cancel();
////        }
////
////    }
}
