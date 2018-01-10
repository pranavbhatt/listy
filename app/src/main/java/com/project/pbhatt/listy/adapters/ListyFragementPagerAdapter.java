package com.project.pbhatt.listy.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.project.pbhatt.listy.fragments.CompletedFragment;
import com.project.pbhatt.listy.R;
import com.project.pbhatt.listy.fragments.TodoFragment;

/**
 * Created by pbhatt on 12/2/17.
 */

public class ListyFragementPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private Context mContext;

    public ListyFragementPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new CompletedFragment();
        }
        return new TodoFragment();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    String[] titles = { "Tasks", "Completed"};

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
