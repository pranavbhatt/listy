package com.project.pbhatt.listy.activities;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import com.project.pbhatt.listy.R;
import com.project.pbhatt.listy.adapters.ListyFragementPagerAdapter;
import com.project.pbhatt.listy.adapters.TodoItemsAdapter;


public class MainActivity extends AppCompatActivity implements TodoItemsAdapter.StatusChangeListener {
    ListyFragementPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        ViewPager viewPager = findViewById(R.id.viewpager);
        mPagerAdapter = new ListyFragementPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        mTabLayout = findViewById(R.id.tlSlidingTabs);
        mTabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStatusChange() {
        mPagerAdapter.notifyDataSetChanged();
    }
}


