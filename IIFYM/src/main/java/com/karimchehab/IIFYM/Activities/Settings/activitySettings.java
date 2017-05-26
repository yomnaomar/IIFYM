package com.karimchehab.IIFYM.Activities.Settings;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.Models.User;
import com.karimchehab.IIFYM.R;

public class activitySettings extends ActionBarActivity implements fragmentGoals.OnFragmentInteractionListener, fragmentProfile.OnFragmentInteractionListener, fragmentPreferences.OnFragmentInteractionListener, ActionBar.TabListener {

    // GUI
    private ViewPager viewPager;
    private tabsAdapter myTabAdapter;
    private ActionBar actionBar;

    // Tab Titles & Icons
    private String[] tabs = {"Goals", "Profile", "Preferences"};
    private int[] maleIcons = {R.drawable.ic_macro_settings, R.drawable.ic_male_user, R.drawable.ic_preferences};
    private int[] femaleIcons = {R.drawable.ic_macro_settings, R.drawable.ic_female_user, R.drawable.ic_preferences};

    // Database
    private SQLiteConnector DB_SQLite;
    private SharedPreferenceHelper myPrefs;

    // Variables
    private Context context;
    private User    user;
    private String  uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        DB_SQLite = new SQLiteConnector(context);
        uid = myPrefs.getStringValue("session_uid");
        user = DB_SQLite.retrieveUser(uid);

        // Tab Initialization
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        actionBar = getSupportActionBar();
        myTabAdapter = new tabsAdapter(getSupportFragmentManager());

        viewPager.setAdapter(myTabAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (int i = 0; i < tabs.length; i++) {
            if (user.getGender() == 0)
                actionBar.addTab(actionBar.newTab().setText(tabs[i]).setIcon(maleIcons[i]).setTabListener(this));
            else
                actionBar.addTab(actionBar.newTab().setText(tabs[i]).setIcon(femaleIcons[i]).setTabListener(this));
        }

        // Swiping Tabs
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override public void onPageSelected(int position) {
                // On changing the tab, make the respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // On tab selected, show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override  public void onFragmentInteraction(Uri uri) {}
}
