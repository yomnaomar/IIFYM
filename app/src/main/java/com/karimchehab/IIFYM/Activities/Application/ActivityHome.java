package com.karimchehab.IIFYM.Activities.Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.karimchehab.IIFYM.Activities.Settings.ActivitySettings;
import com.karimchehab.IIFYM.Activities.Authentication.ActivitySelectAuthentication;
import com.karimchehab.IIFYM.Activities.Static.ActivityAbout;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.Models.DateHelper;
import com.karimchehab.IIFYM.R;

public class ActivityHome extends AppCompatActivity implements View.OnClickListener{
    // GUI
    private TextView                lblSelectedDate;
    private ViewPager               viewPager;
    private InfinitePagerAdapter    viewPagerAdapter;
    private FloatingActionButton    fabAddDailyItem;
    private ImageButton             imagebtn_next, imagebtn_prev;

    // Variables
    private Context             context;
    private int                 selectedRelativeDay = 0; // relative to today in days
    private FragmentDay[]       fragments;

    // Database
    private SharedPreferenceHelper myPrefs;
    private FirebaseAuth firebaseAuth;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        myPrefs = new SharedPreferenceHelper(context);
        String session_uid = myPrefs.getStringValue("session_uid");

        if(session_uid.isEmpty()){
            // Go to activityLogin
            Intent intent = new Intent();
            intent.setClass(context, ActivitySelectAuthentication.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.content_home);

        Toolbar toolbarMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarMain);

        lblSelectedDate = (TextView) findViewById(R.id.lblSelectedDate);
        imagebtn_next = (ImageButton) findViewById(R.id.imagebtn_next);
        imagebtn_next.setOnClickListener(this);

        imagebtn_prev = (ImageButton) findViewById(R.id.imagebtn_prev);
        imagebtn_prev.setOnClickListener(this);

        fabAddDailyItem = (FloatingActionButton) findViewById(R.id.fabAddDailyItem);
        fabAddDailyItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToFoodSearch();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Database
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Called by clicking FAB (declared in OnCreate)
    private void goToFoodSearch() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ActivityFoodSearch.class);
        startActivity(intent);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case (R.id.actionToday):
                goToToday();
                return true;
            case (R.id.actionNutritionSettings):
                intent = new Intent(context, ActivitySettings.class );
                startActivity(intent);
                return true;
            case (R.id.actionFoodManager):
                intent = new Intent(context, ActivityFoodManager.class);
                startActivity(intent);
                return true;
            case (R.id.menuLogout):
                signOut();
                intent = new Intent(context, ActivitySelectAuthentication.class);
                startActivity(intent);
                finish();
                return true;
            case (R.id.menuAbout):
                intent = new Intent(context, ActivityAbout.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * Creates the pages and tabs of Today, Yesterday, and Tomorrow.
     * @param viewPager
     */
    private void setupViewPager(final ViewPager viewPager) {
       fragments = new FragmentDay[] {
                createFragment(selectedRelativeDay - 1),
                createFragment(selectedRelativeDay),
                createFragment(selectedRelativeDay + 1)
        };
        viewPagerAdapter = new InfinitePagerAdapter(getSupportFragmentManager(), fragments);

        lblSelectedDate.setText(DateHelper.getDateRelativeToToday(selectedRelativeDay).text);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int currPage = viewPager.getCurrentItem();
                    if (currPage < 1) {
                        selectedRelativeDay -= 1;
                    } else if (currPage > 1) {
                        selectedRelativeDay += 1;
                    }
                    fragments[0].setDate(DateHelper.getDateRelativeToToday(selectedRelativeDay - 1));
                    fragments[0].render();
                    fragments[1].setDate(DateHelper.getDateRelativeToToday(selectedRelativeDay));
                    fragments[1].render();
                    fragments[2].setDate(DateHelper.getDateRelativeToToday(selectedRelativeDay + 1));
                    fragments[2].render();

                    viewPager.setCurrentItem(1, false);
                }
                if (state == ViewPager.SCROLL_STATE_SETTLING){
                    int currPage = viewPager.getCurrentItem();
                    if (currPage < 1) {
                        currPage = selectedRelativeDay - 1;
                    } else if (currPage > 1) {
                        currPage = selectedRelativeDay + 1;
                    }
                    lblSelectedDate.setText(DateHelper.getDateRelativeToToday(currPage).text);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageSelected(int arg0) {}
        });

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
    }

    /**
     * Returns a new Fragment page `relative` to today.
     * @param relative 0 today, negative before today, positive after today.
     * @return
     */
    private FragmentDay createFragment(int relative) {
        FragmentDay f = new FragmentDay();
        DateHelper.StringDate day = DateHelper.getDateRelativeToToday(relative);
        f.setDate(day);
        return f;
    }

    class InfinitePagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments;

        public InfinitePagerAdapter(FragmentManager manager, Fragment[] fragments) {
            super(manager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebtn_next:
                goToNext();
                break;
            case R.id.imagebtn_prev:
                goToPrev();
                break;
        }
    }

    /**
     * Passes page index to go to
     */
    private void goToNext() {
        viewPager.setCurrentItem(2);
    }

    /**
     * Passes page index to go to
     */
    private void goToPrev() {
        viewPager.setCurrentItem(0);
    }

    /**
     * Will call onPageSelected() in setUpViewPager()
     */
    private void goToToday() {
        selectedRelativeDay = 0;

        fragments[0].setDate(DateHelper.getDateRelativeToToday(selectedRelativeDay - 1));
        fragments[0].render();
        fragments[1].setDate(DateHelper.getDateRelativeToToday(selectedRelativeDay));
        fragments[1].render();
        fragments[2].setDate(DateHelper.getDateRelativeToToday(selectedRelativeDay + 1));
        fragments[2].render();

        viewPager.setCurrentItem(1, false);
        lblSelectedDate.setText(DateHelper.getDateRelativeToToday(0).text);

        Toast.makeText(this,"Today selected",Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        myPrefs.addPreference("session_uid", "");
        firebaseAuth.signOut();
        Intent intent = new Intent();
        intent.setClass(context, ActivitySelectAuthentication.class);
        startActivity(intent);
    }
}