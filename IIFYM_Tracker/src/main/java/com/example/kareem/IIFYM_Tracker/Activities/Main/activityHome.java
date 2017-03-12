package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.activitySettings;
import com.example.kareem.IIFYM_Tracker.Activities.UserLoginAuthentification.activityLogin;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.User;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.AdapterDailyItem;
import com.example.kareem.IIFYM_Tracker.ViewComponents.OnListItemDeletedListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/*import com.example.kareem.IIFYM_Tracker.Activities.Settings.MacroSettings;*/

public class activityHome extends AppCompatActivity implements AdapterView.OnItemClickListener, OnListItemDeletedListener {

    // GUI
    private TextView lblCaloriesCurrent, lblCarbsCurrent, lblProteinCurrent, lblFatCurrent;
    private TextView lblCaloriesLeft,    lblCarbsLeft,    lblProteinLeft,    lblFatLeft;
    private TextView lblCaloriesGoal,    lblCarbsGoal,    lblProteinGoal,    lblFatGoal;

    private ArrayList<DailyItem>    arrDailyItems;
    private AdapterDailyItem adapterDailyItems;
    private ListView                listViewDailyItems;
    private RoundCornerProgressBar  progressBarCalories, progressBarCarbs, progressBarProtein, progressBarFat;
    private Animation               mEnterAnimation, mExitAnimation;
    private FloatingActionButton    fabAddDailyItem;

    // Variables
    private Context context;
    public boolean  isPercent;
    public int      caloriesCurrent = 0,    carbsCurrent = 0,   proteinCurrent = 0, fatCurrent = 0;
    public int      caloriesLeft = 0,       carbsLeft = 0,      proteinLeft = 0,    fatLeft = 0;
    public int      caloriesGoal,           carbsGoal,          proteinGoal,        fatGoal;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;
    private FirebaseAuth            firebaseAuth;
    private User                    currentUser;
    private String                  uid;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        // Database
        myPrefs = new SharedPreferenceHelper(context);
        DB_SQLite = new SQLiteConnector(context);
        firebaseAuth = FirebaseAuth.getInstance();

        // GUI
        initializeGUI();

        // User
        initializeUser();
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

        fabAddDailyItem = (FloatingActionButton) findViewById(R.id.fabAddDailyItem);
        fabAddDailyItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToAddDailyItem();
            }
        });

        arrDailyItems = new ArrayList<DailyItem>();
        adapterDailyItems = new AdapterDailyItem(this, arrDailyItems);

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

        isPercent = currentUser.getIsPercent();

        caloriesGoal = currentUser.getDailyCalories();
        carbsGoal = currentUser.getDailyCarbs();
        proteinGoal = currentUser.getDailyProtein();
        fatGoal = currentUser.getDailyFat();

        if (isPercent){ // Convert from percentage to grams
            carbsGoal = (carbsGoal*caloriesGoal)/400;
            proteinGoal = (proteinGoal*caloriesGoal)/400;
            fatGoal = (fatGoal*caloriesGoal)/900;
        }

        Log.d("initializeUser", currentUser.toString());
    }

    public void updateMacros(){
        // Reset to prevent accumulation
        caloriesCurrent = 0;
        carbsCurrent = 0;
        proteinCurrent = 0;
        fatCurrent = 0;

        int itemCount = adapterDailyItems.getCount();
        for (int i = 0; i < itemCount; i++) {
            DailyItem dailyItem = adapterDailyItems.getItem(i);
            Food tempFood = DB_SQLite.retrieveFood(dailyItem.getFood_id());
            DailyItem tempDailyItem = DB_SQLite.retrieveDailyItem(dailyItem.getId());
            int calories = Math.round(tempFood.getCalories() * tempDailyItem.getMultiplier());
            int carbs = Math.round(tempFood.getCarbs() * tempDailyItem.getMultiplier());
            int protein = Math.round(tempFood.getProtein() * tempDailyItem.getMultiplier());
            int fat = Math.round(tempFood.getFat() * tempDailyItem.getMultiplier());

            caloriesCurrent += calories;
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
        if(caloriesCurrent < caloriesGoal) {
            progressBarCalories.setProgress(100 * caloriesCurrent / caloriesGoal);
            lblCaloriesLeft.setTextColor(context.getResources().getColor(R.color.CaloriesProgessColor));
            lblCaloriesLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarCalories.setProgress(100);
            lblCaloriesLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblCaloriesLeft.setTypeface(null, Typeface.BOLD);
        }

        if(carbsCurrent < carbsGoal) {
            progressBarCarbs.setProgress(100 * carbsCurrent / carbsGoal);
            lblCarbsLeft.setTextColor(context.getResources().getColor(R.color.CarbProgressColor));
            lblCarbsLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarCarbs.setProgress(100);
            lblCarbsLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblCarbsLeft.setTypeface(null, Typeface.BOLD);
        }
        if(proteinCurrent < proteinGoal) {
            progressBarProtein.setProgress(100 * proteinCurrent / proteinGoal);
            lblProteinLeft.setTextColor(context.getResources().getColor(R.color.ProteinProgessColor));
            lblProteinLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarProtein.setProgress(100);
            lblProteinLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblProteinLeft.setTypeface(null, Typeface.BOLD);
        }
        if(fatCurrent < fatGoal) {
            progressBarFat.setProgress(100 * fatCurrent / fatGoal);
            lblFatLeft.setTextColor(context.getResources().getColor(R.color.FatProgessColor));
            lblFatLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarFat.setProgress(100);
            lblFatLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblFatLeft.setTypeface(null, Typeface.BOLD);
        }
    }

    public void updateArrayList() {
        adapterDailyItems.clear();
        arrDailyItems = DB_SQLite.retrieveAllDailyItems();
        for (int i =0; i <arrDailyItems.size(); i++)
            adapterDailyItems.add(arrDailyItems.get(i));
    }

    @Override protected void onResume() {
        super.onResume();
        initializeUser();
        updateArrayList();
        updateMacros();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    private void goToAddDailyItem() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,activitySelectDailyItem.class);
        startActivity(intent);
    }

    @Override public void onItemDeleted() {
        updateArrayList();
        updateMacros();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long row_id) {
        DailyItem dailyItem = (DailyItem) parent.getItemAtPosition(position);
        int id = dailyItem.getId();

        Intent intent = new Intent(getBaseContext(), activityViewDailyItem.class);
        intent.putExtra("id", id);
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
}
