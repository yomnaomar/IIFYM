package com.karimchehab.IIFYM.ViewComponents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.karimchehab.IIFYM.Activities.Settings.fragmentGoals;
import com.karimchehab.IIFYM.Activities.Settings.fragmentPreferences;
import com.karimchehab.IIFYM.Activities.Settings.fragmentProfile;

/**
 * Created by Yomna on 5/26/2017.
 */

public class tabsAdapter extends FragmentPagerAdapter {

    public tabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new fragmentGoals();
            case 1:
                return new fragmentProfile();
            case 2:
                return new fragmentPreferences();
        }
        return null;
    }

    @Override public int getCount() {
        return 3;
    }
}