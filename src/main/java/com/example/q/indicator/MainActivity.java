package com.example.q.indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Indicator indicator;
    private List<String> strings = Arrays.asList("短信","收藏","推荐","1","2","3","4","5","6");
    private List<MyFragment> list=new ArrayList<>();
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        indicator= (Indicator) findViewById(R.id.indicator);
        initDate();
    }

    private void initDate() {
        for (String title:strings){
            MyFragment fragment=MyFragment.newInstance(title);
            list.add(fragment);
        }

        indicator.setTabItemTitles(strings);
        indicator.setViewPager(viewPager,0);
        adapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        };
        viewPager.setAdapter(adapter);

    }
}
