package com.hskj.meettingsys;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hskj.meettingsys.K780.K780Utils;
import com.hskj.meettingsys.K780.Weather;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnGetCurrentDateTimeListener{
    private ListView meeting_list;
    private ArrayList<MeetingItemBean> list = new ArrayList<>();
    private MeetingAdapter adapter ;
    private TextView timeTv,dataTv;
    private DateTimeUtil dateTimeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meeting_list = findViewById(R.id.meeting_list);
        initdata();
        adapter = new MeetingAdapter(this,list);
        meeting_list.setAdapter(adapter);

        timeTv = findViewById(R.id.time);
        dataTv = findViewById(R.id.data);

        dateTimeUtil = DateTimeUtil.getInstance();
        new TimeThreadUtil(this).start();

//        new MyTask().execute(weaid);
        OkGo.get(K780Utils.WEATHER_URL).cacheKey(K780Utils.WEATHER_URL).cacheMode(CacheMode.DEFAULT).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (response.code() == 200) {
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONObject result = obj.getJSONObject("result");

                    String citynm = result.getString("citynm");
                    String temperature = result.getString("temperature");
                    String temperature_curr = result.getString("temperature_curr");
                    String weather_curr = result.getString("weather_curr");
                    String weather_icon = result.getString("weather_icon");
                    Log.i("=====result",citynm);
                    Log.i("=====result",temperature);
                    Log.i("=====result",temperature_curr);
                    Log.i("=====result",weather_curr);
                    Log.i("=====result",weather_icon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }
        });

    }

    private void initdata() {
        for(int i = 0;i< MeetingData.meeting_data_day.length;i ++){
            list.add(new MeetingItemBean(MeetingData.meeting_data_day[i],MeetingData.meeting_data_hour[i],MeetingData.meeting_title[i],MeetingData.meeting_order[i]));
        }
    }

    @Override
    public void onGetDateTime() {
        timeTv.setText(dateTimeUtil.getCurrentTime());//显示时间
        dataTv.setText(dateTimeUtil.getCurrentDate()+ "\t\t" +dateTimeUtil.getCurrentWeekDay(0));//显示年月日
    }

    private class MyTask extends AsyncTask<Integer, Void, Weather> {

        // 进度条对话框
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
//            dialog = new ProgressDialog(MainActivity.this);
//            dialog.setTitle("天气数据");
//            dialog.setMessage("正在下载...");
//            // 显示对话框
//            dialog.show();
        }

        @Override
        protected Weather doInBackground(Integer... params) {
            return K780Utils.getOneDayWeather(params[0]);
        }

        @Override
        protected void onPostExecute(Weather result) {
            if(result == null) {
                Toast.makeText(MainActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
            } else {

                Log.i("=====result",result.toString());

            }
            // 关闭对话框
//            dialog.cancel();
        }

    }
}
