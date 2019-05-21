package com.hskj.meettingsys;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hskj.meettingsys.ui.AFragment;
import com.hskj.meettingsys.ui.BFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity implements  IGetMessageCallBack {
    private List<Fragment> frags;
    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private MqttManager mqttManager;
    private MyMessageTask myMessageTask;
    private static  boolean getMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        viewPager = findViewById(R.id.viewPager);
        frags = new ArrayList<Fragment>();
        frags.add(new AFragment());
        frags.add(new BFragment());

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setCurrentItem(0);

        mqttManager = new MqttManager(this);
        mqttManager.setIGetMessageCallBack(this);

        myMessageTask = new MyMessageTask();
        myMessageTask.execute();
    }

    @Override
    public void setMessage(String message) {
        Log.i("=====收到的message11：", "MainActivity11+setMessage:" + message);
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
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return frags == null ? 0 : frags.size();
        }

        @Override
        public Fragment getItem(int postion) {
            return frags.get(postion);
        }
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
}
