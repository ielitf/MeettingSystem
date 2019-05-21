package com.hskj.meettingsys;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hskj.meettingsys.ui.AFragment;
import com.hskj.meettingsys.ui.BFragment;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    private List<Fragment> frags;
    private ViewPager viewPager;
    private MyPagerAdapter adapter;

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
}
