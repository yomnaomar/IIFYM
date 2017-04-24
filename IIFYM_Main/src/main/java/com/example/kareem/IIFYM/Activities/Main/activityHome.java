package com.example.kareem.IIFYM.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kareem.IIFYM.Activities.Settings.activitySettings;
import com.example.kareem.IIFYM.Activities.UserLoginAuthentification.activityLogin;
import com.example.kareem.IIFYM.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM.Models.DateHelper;
import com.example.kareem.IIFYM.R;
import com.google.firebase.auth.FirebaseAuth;

public class activityHome extends AppCompatActivity {
    // GUI
    private ViewPager viewPager;
    private InfinitePagerAdapter viewPagerAdapter;
    private View fabAddDailyItem;

    // Variables
    private Context context;
    private int selectedRelativeDay = 0; // relative to today in days

    // Database
    private SharedPreferenceHelper myPrefs;
    private FirebaseAuth firebaseAuth;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.content_home);

        Toolbar toolbarMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarMain);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        fabAddDailyItem = (FloatingActionButton) findViewById(R.id.fabAddDailyItem);
        fabAddDailyItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToAddDailyItem();
            }
        });

        // Database
        myPrefs = new SharedPreferenceHelper(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void goToAddDailyItem() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,activitySelectDailyItem.class);
        startActivity(intent);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case (R.id.actionNutritionSettings):
                intent = new Intent(context,activitySettings.class );
                startActivity(intent);
                return true;
            case (R.id.actionFoodManager):
                intent = new Intent(context, activityFoodManager.class);
                startActivity(intent);
                return true;
            case (R.id.menuLogout):
                signOut();
                intent = new Intent(context, activityLogin.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void signOut() {
        myPrefs.addPreference("session_uid", "");
        firebaseAuth.signOut();
    }

    /**
     * Creates the pages and tabs of Today, Yesterday, and Tomorrow.
     * @param viewPager
     */
    private void setupViewPager(final ViewPager viewPager) {
        final fragmentDay[] fragments = new fragmentDay[] {
                createFragment(selectedRelativeDay - 1),
                createFragment(selectedRelativeDay),
                createFragment(selectedRelativeDay + 1)
        };
        viewPagerAdapter = new InfinitePagerAdapter(getSupportFragmentManager(), fragments);
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
    private fragmentDay createFragment(int relative) {
        fragmentDay f = new fragmentDay();
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
}
