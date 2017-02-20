package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.MacroSettings;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.UserProfile_Mina;
import com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification.activityLogin;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.User;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.OnListItemDeletedListener;
import com.example.kareem.IIFYM_Tracker.ViewComponents.adapterDailyItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class activityMain extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnListItemDeletedListener {

    // GUI
    private TextView lblCaloriesCurrent, lblCarbsCurrent, lblProteinCurrent, lblFatCurrent;
    private TextView lblCaloriesLeft,    lblCarbsLeft,    lblProteinLeft,    lblFatLeft;
    private TextView lblCaloriesGoal,    lblCarbsGoal,    lblProteinGoal,    lblFatGoal;

    private ArrayList<DailyItem>    arrDailyItems;
    private adapterDailyItem        adapterDailyItems;
    private ListView                listViewDailyItems;
    private RoundCornerProgressBar  progressBarCalories, progressBarCarbs, progressBarProtein, progressBarFat;
    private Animation               mEnterAnimation, mExitAnimation;

    // Variables
    private Context context;
    public boolean  isPercent;
    public int      caloriesCurrent = 0,    carbsCurrent = 0,   proteinCurrent = 0, fatCurrent = 0;
    public int      caloriesLeft = 0,       carbsLeft = 0,      proteinLeft = 0,    fatLeft = 0;
    public int      caloriesGoal,           carbsGoal,          proteinGoal,        fatGoal;

    // Storage
    private SharedPreferenceHelper          myPrefs;
    private SQLiteConnector                 DB_SQLite;
    private FirebaseAuth                    firebaseAuth;
    private User                            currentUser;
    private String                          uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        // Storage
        myPrefs = new SharedPreferenceHelper(context);
        DB_SQLite = new SQLiteConnector(context);
        firebaseAuth = FirebaseAuth.getInstance();

        // GUI
        initializeGUI();
        
        // Initialize User
        initializeUser();
    }

    @Override public void onClick(View v) {
        switch (v.getId()){

        }
    }

    // TODO Implement
    private void AddItem() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,acitivityAddSavedFood.class);
        startActivity(intent);
    }

    @Override public void onItemDeleted() {
        UpdateArrayList();
        UpdateMacros();
    }

    // TODO Implement
    @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        /*DailyItem dailyItem = (DailyItem) parent.getItemAtPosition(position);
        int dailyItem_id = dailyItem.getFood().getId();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("id", dailyItem_id);
        intent.putExtra("position", position);
        intent.putExtra("isDaily", true);
        startActivity(intent);*/
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_MealSettings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.example.kareem.IIFYM_Tracker.Activities.Main.ViewSavedMealsActivity");
            startActivity(intent);
            return true;
        }
        if(id==R.id.logout_menu_btn)
        {
            signOut();
            Intent in = new Intent(getApplicationContext(), activityLogin.class);
            startActivity(in);
            finish();
            return true;
        }
        if(id==R.id.profile_menu_btn)
        {
            Intent intent = new Intent(getApplicationContext(),UserProfile_Mina.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_MacroSettings)
        {
            Intent in = new Intent(getApplicationContext(),MacroSettings.class );
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_woman, menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (currentUser.getGender() == 0) { //Male
            getMenuInflater().inflate(R.menu.menu_home_man, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_home_woman, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void initializeGUI() {
        lblCaloriesCurrent = (TextView) findViewById(R.id.lblCaloriesCurrent);
        lblCaloriesLeft = (TextView) findViewById(R.id.lblCaloriesLeft);
        lblCaloriesGoal = (TextView) findViewById(R.id.lblCaloriesGoal);

        lblCarbsCurrent = (TextView) findViewById(R.id.lblCarbsCurrent);
        lblCarbsLeft = (TextView) findViewById(R.id.lblCarbsLeft);
        lblCarbsGoal = (TextView) findViewById(R.id.lblCarbsGoal);

        lblProteinCurrent = (TextView) findViewById(R.id.lblProteinCurrent);
        lblProteinLeft = (TextView) findViewById(R.id.lblProteinLeft);
        lblProteinGoal = (TextView) findViewById(R.id.lblProteinGoal);

        lblFatCurrent = (TextView) findViewById(R.id.lblFatCurrent);
        lblFatLeft = (TextView) findViewById(R.id.lblFatLeft);
        lblFatGoal = (TextView) findViewById(R.id.lblFatGoal);

        progressBarCalories = (RoundCornerProgressBar) findViewById(R.id.progressBarCalories);
        progressBarCarbs = (RoundCornerProgressBar) findViewById(R.id.progressBarCarbs);
        progressBarProtein = (RoundCornerProgressBar) findViewById(R.id.progressBarProtein);
        progressBarFat = (RoundCornerProgressBar) findViewById(R.id.progressBarFat);

        arrDailyItems = new ArrayList<DailyItem>();
        adapterDailyItems = new adapterDailyItem(this, arrDailyItems);

        listViewDailyItems = (ListView) findViewById(R.id.listviewDailyItems);
        listViewDailyItems.setAdapter(adapterDailyItems);
        listViewDailyItems.setOnItemClickListener(this);

        // Setup enter and exit animation
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
    }

    private void initializeUser() {
        uid = myPrefs.getStringValue("session_uid");
        currentUser = DB_SQLite.retrieveUser(uid);

        // User Greetings
        Toast toast = Toast.makeText(context, "Hello " + currentUser.getName() + "!", Toast.LENGTH_SHORT);
        toast.show();

        isPercent = currentUser.isPercent();

        caloriesGoal = currentUser.getDailyCalories();
        carbsGoal = currentUser.getDailyCarbs();
        proteinGoal = currentUser.getDailyProtein();
        fatGoal = currentUser.getDailyFat();

        if (isPercent){
            carbsGoal = carbsGoal*caloriesGoal/400;
            proteinGoal = proteinGoal*caloriesGoal/400;
            fatGoal = fatGoal*caloriesGoal/900;
        }
    }

    public void UpdateMacros(){
        // Updating "Current" Variables
        int itemCount = adapterDailyItems.getCount();
        for (int i = 0; i < itemCount; i++) {
            DailyItem tempItem = adapterDailyItems.getItem(i);
            int calories = Math.round(tempItem.getFood().getCalories());
            int carbs = Math.round(tempItem.getFood().getCarbs());
            int protein = Math.round(tempItem.getFood().getProtein());
            int fat = Math.round(tempItem.getFood().getFat());

            caloriesCurrent +=calories;
            carbsCurrent    += carbs;
            proteinCurrent  += protein;
            fatCurrent      += fat;
        }

        // Updating "Left" Variables
        caloriesLeft = caloriesGoal - caloriesCurrent;
        carbsLeft = carbsGoal - carbsCurrent;
        proteinLeft = proteinGoal - proteinCurrent;
        fatLeft = fatGoal - fatCurrent;

        // Updating Labels
        lblCaloriesGoal.setText(caloriesGoal + "");
        lblCarbsGoal.setText(carbsGoal + "");
        lblProteinGoal.setText(proteinGoal + "");
        lblFatGoal.setText(fatGoal + "");

        lblCaloriesLeft.setText(caloriesLeft + "");
        lblCarbsLeft.setText(carbsLeft + "");
        lblProteinLeft.setText(proteinLeft + "");
        lblFatLeft.setText(fatLeft + "");

        lblCaloriesCurrent.setText(caloriesCurrent + "");
        lblCarbsCurrent.setText(carbsCurrent + "");
        lblProteinCurrent.setText(proteinCurrent + "");
        lblFatCurrent.setText(fatCurrent + "");

        // Updating ProgressBars
        if(caloriesCurrent <= caloriesGoal) {
            progressBarCalories.setProgress(100 * caloriesCurrent / caloriesGoal);
            // TODO change label color to normal
        }
        else {
            progressBarCalories.setProgress(100);
            // TODO change label color to red
        }

        if(carbsCurrent <= carbsGoal) {
            progressBarCarbs.setProgress(100 * carbsCurrent / carbsGoal);
            // TODO change label color to normal
        }
        else {
            progressBarCarbs.setProgress(100);
            // TODO change label color to red
        }
        if(proteinCurrent <= proteinGoal) {
            progressBarProtein.setProgress(100 * proteinCurrent / proteinGoal);
            // TODO change label color to normal
        }
        else {
            progressBarProtein.setProgress(100);
            // TODO change label color to red
        }
        if(fatCurrent <= fatGoal) {
            progressBarFat.setProgress(100 * fatCurrent / fatGoal);
            // TODO change label color to normal
        }
        else {
            progressBarFat.setProgress(100);
            // TODO change label color to red
        }
    }

    // Updates adapterDailyItems
    public void UpdateArrayList() {
        adapterDailyItems.clear();
        arrDailyItems = DB_SQLite.retrieveAllDailyItems();
        for (int i =0; i <arrDailyItems.size(); i++)
            adapterDailyItems.add(arrDailyItems.get(i));
    }

    @Override protected void onResume() {
        super.onResume();
        UpdateMacros();
        UpdateArrayList();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    private void signOut() {
        myPrefs.addPreference("session_uid", "");
        Log.d("Main_SignOut","session_uid removed");
        Log.d("Main_SignOut","checking ... " + myPrefs.getStringValue("session_uid"));
        firebaseAuth.signOut();
    }
}
