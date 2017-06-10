package com.karimchehab.IIFYM.Views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.karimchehab.IIFYM.Activities.Settings.FragmentGoals;
import com.karimchehab.IIFYM.Activities.Settings.FragmentPreferences;
import com.karimchehab.IIFYM.Activities.Settings.FragmentProfile;

public class AdapterSettingsTabs extends FragmentPagerAdapter {

    public AdapterSettingsTabs(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentGoals();
            case 1:
                return new FragmentProfile();
            case 2:
                return new FragmentPreferences();
        }
        return null;
    }

    @Override public int getCount() {
        return 3;
    }
}