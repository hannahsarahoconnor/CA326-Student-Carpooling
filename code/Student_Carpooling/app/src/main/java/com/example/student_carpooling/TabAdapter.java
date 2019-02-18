package com.example.student_carpooling;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import afu.org.checkerframework.checker.nullness.qual.Nullable;

public class TabAdapter extends FragmentPagerAdapter {

    private final List<Fragment> FragmentList = new ArrayList<>();
    private final List<String> FragmentTitleList = new ArrayList<>();

    public TabAdapter(FragmentManager fm, int numTabs) {
        super(fm);

    }

    public void addFragment(Fragment fragment, String title) {
        FragmentList.add(fragment);
        FragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentTitleList.get(position);
    }


    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);

    }

    @Override
    public int getCount() {
        return FragmentTitleList.size();
    }
}
