package com.hskj.meettingsys.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.hskj.meettingsys.R;
import com.hskj.meettingsys.utils.LogUtil;
import com.hskj.meettingsys.utils.SDCardUtils;
import com.hskj.meettingsys.utils.SharePreferenceManager;

import java.io.File;
import java.util.Date;

public class LauncherActivity extends AppCompatActivity {
    private Context mContext;
    private float mDensity;
    private int mDensityDpi;
    private int mAvatarSize;
    private int mWidth;
    private int mHeight;
    private Handler handler = new Handler();
    private String TAG = getClass().getSimpleName();
    private Spinner roomSpinner;
    private Button get_in;
    private ArrayAdapter<CharSequence> dataAdapter = null;
    private String data[] = {"001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018", "019"};

    private static final int DELAY_TIME = 0;
    Runnable r = new Runnable() {

        @Override
        public void run() {

            if (isFirstUse()) {
//                handler.sendEmptyMessage(FIRST_USE);
//                intent2Activity(LauncherViewPagerActivity.class);
//                finish();
            } else {
//                handler.sendEmptyMessage(GOTO_MAIN);
//                startActivity(new Intent(mContext,MainActivity.class));
//                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mContext = LauncherActivity.this;

        get_in = findViewById(R.id.get_in);
        roomSpinner = findViewById(R.id.room_spinner);
        dataAdapter = new ArrayAdapter<CharSequence>(mContext,
                android.R.layout.simple_spinner_dropdown_item, data);
        roomSpinner.setAdapter(dataAdapter);

        roomSpinner.setOnItemSelectedListener(new spinnerSelectedListenner());//绑定事件监听

        get_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharePreferenceManager.setIsFirstUse(false);
//                startActivity(new Intent(mContext,MainActivity.class));
//                finish();
            }


        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        LogUtil.i(TAG, "mDensity=" + mDensity);
        LogUtil.i(TAG, "mDensityDpi=" + mDensityDpi);
        LogUtil.i(TAG, "mScaledDensityDpi" + dm.scaledDensity);
        LogUtil.i(TAG, "mWidth=" + mWidth);
        LogUtil.i(TAG, "mHeight=" + mHeight);
        Configuration config = getResources().getConfiguration();
        int smallestScreenWidth = config.smallestScreenWidthDp;
        LogUtil.w(TAG, "smallest width : " + smallestScreenWidth);
        handler.postDelayed(r, DELAY_TIME);

    }

    private boolean isFirstUse() {
        boolean isFirstUse = SharePreferenceManager.getIsFirstUse();
        if (isFirstUse) {
//            SharePreferenceManager.setIsFirstUse(false);
            return true;
        }
        return false;
    }

    private class spinnerSelectedListenner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {         //望文生义，当列表项被选择时
            String select = parent.getItemAtPosition(position).toString();//取得被选中的列表项的文字
            Toast.makeText(mContext, select, Toast.LENGTH_SHORT).show();
//            SharePreferenceManager.setMeetingRoomNum(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    }

}
