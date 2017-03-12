package com.example.kareem.IIFYM.Activities.Settings;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.kareem.IIFYM.Database.SQLiteConnector;
import com.example.kareem.IIFYM.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM.Models.User;
import com.example.kareem.IIFYM.R;

import java.util.ArrayList;
import java.util.List;

public class activitySettings extends AppCompatActivity implements fragmentNutrition.OnFragmentInteractionListener, fragmentProfile.OnFragmentInteractionListener, fragmentPreferences.OnFragmentInteractionListener {

    // GUI
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // Database
    private SQLiteConnector DB_SQLite;
    private SharedPreferenceHelper myPrefs;

    // Variables
    private Context         context;
    private User            user;
    private String          uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);
        myPrefs = new SharedPreferenceHelper(context);
        uid = myPrefs.getStringValue("session_uid");
        user = DB_SQLite.retrieveUser(uid);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_macro_settings);
        if (user.getGender() == 0) // Male
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_male_user);
        else
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_female_user);
//        tabLayout.getTabAt(2).setIcon(R.drawable.;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragmentNutrition(), "Goals");
        adapter.addFragment(new fragmentProfile(), "Profile");
        adapter.addFragment(new fragmentPreferences(), "Preferences");
        viewPager.setAdapter(adapter);
    }

    @Override public void onFragmentInteraction(Uri uri) {}

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
