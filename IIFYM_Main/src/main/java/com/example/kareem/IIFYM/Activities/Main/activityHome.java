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

import java.util.ArrayList;
import java.util.List;

public class activityHome extends AppCompatActivity {
    // GUI
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View fabAddDailyItem;

    // Variables
    private Context context;

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

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(createFragmentDay(-1), "Yesterday");
        adapter.addFragment(createFragmentDay(0), "Today");
        adapter.addFragment(createFragmentDay(1), "Tomorrow");
        viewPager.setAdapter(adapter);
    }

    private fragmentDay createFragmentDay(int relativeDay) {
        fragmentDay frag = new fragmentDay();
        frag.setDate(DateHelper.getDateRelativeToToday(relativeDay));
        return frag;
    }

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
