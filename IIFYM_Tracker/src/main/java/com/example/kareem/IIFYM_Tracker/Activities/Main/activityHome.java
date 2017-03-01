package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.kareem.IIFYM_Tracker.Activities.UserLoginAuthentification.activityLogin;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.Models.DailyItem;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.User;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.OnListItemDeletedListener;
import com.example.kareem.IIFYM_Tracker.ViewComponents.adapterDailyItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/*import com.example.kareem.IIFYM_Tracker.Activities.Settings.MacroSettings;*/

public class activityHome extends AppCompatActivity implements AdapterView.OnItemClickListener, OnListItemDeletedListener {

    // GUI
    private TextView lblCaloriesCurrent, lblCarbsCurrent, lblProteinCurrent, lblFatCurrent;
    private TextView lblCaloriesLeft,    lblCarbsLeft,    lblProteinLeft,    lblFatLeft;
    private TextView lblCaloriesGoal,    lblCarbsGoal,    lblProteinGoal,    lblFatGoal;

    private ArrayList<DailyItem>    arrDailyItems;
    private adapterDailyItem        adapterDailyItems;
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

        isPercent = currentUser.getIsPercent();
        Log.d("initializeUser","isPercent " + isPercent);

        caloriesGoal = currentUser.getDailyCalories();
        carbsGoal = currentUser.getDailyCarbs();
        proteinGoal = currentUser.getDailyProtein();
        fatGoal = currentUser.getDailyFat();

        if (isPercent){ // Convert from percentage to grams
            carbsGoal = carbsGoal*caloriesGoal/400;
            proteinGoal = proteinGoal*caloriesGoal/400;
            fatGoal = fatGoal*caloriesGoal/900;
        }
    }

    public void updateMacros(){
        // Updating "Current" Variables

        // Reset to prevent accumulation
        caloriesCurrent = 0;
        carbsCurrent = 0;
        proteinCurrent = 0;
        fatCurrent = 0;

        int itemCount = adapterDailyItems.getCount();
        for (int i = 0; i < itemCount; i++) {
            Food tempFood = DB_SQLite.retrieveFood(adapterDailyItems.getItem(i).getId());
            int calories = Math.round(tempFood.getCalories());
            int carbs = Math.round(tempFood.getCarbs());
            int protein = Math.round(tempFood.getProtein());
            int fat = Math.round(tempFood.getFat());

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
            lblCaloriesCurrent.setTextColor(context.getResources().getColor(R.color.CaloriesProgessColor));
        }
        else {
            progressBarCalories.setProgress(100);
            lblCaloriesCurrent.setTextColor(context.getResources().getColor(R.color.error_red));
        }

        if(carbsCurrent < carbsGoal) {
            progressBarCarbs.setProgress(100 * carbsCurrent / carbsGoal);
            lblCarbsCurrent.setTextColor(context.getResources().getColor(R.color.CarbProgressColor));
        }
        else {
            progressBarCarbs.setProgress(100);
            lblCarbsCurrent.setTextColor(context.getResources().getColor(R.color.error_red));
        }
        if(proteinCurrent < proteinGoal) {
            progressBarProtein.setProgress(100 * proteinCurrent / proteinGoal);
            lblProteinCurrent.setTextColor(context.getResources().getColor(R.color.ProteinProgessColor));
        }
        else {
            progressBarProtein.setProgress(100);
            lblProteinCurrent.setTextColor(context.getResources().getColor(R.color.error_red));
        }
        if(fatCurrent < fatGoal) {
            progressBarFat.setProgress(100 * fatCurrent / fatGoal);
            lblFatCurrent.setTextColor(context.getResources().getColor(R.color.FatProgessColor));
        }
        else {
            progressBarFat.setProgress(100);
            lblFatCurrent.setTextColor(context.getResources().getColor(R.color.error_red));
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

    @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DailyItem dailyItem = (DailyItem) parent.getItemAtPosition(position);
        long dailyItem_id = dailyItem.getId();

        Intent intent = new Intent(getBaseContext(), activityViewDailyItem.class);
        intent.putExtra("id", dailyItem_id);
        intent.putExtra("position", position);
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
            /*    intent = new Intent(context,MacroSettings.class );
                startActivity(intent);
                finish();*/
                return true;
            case (R.id.actionFoodManager):
                intent = new Intent(context, activityFoodManager.class);
                startActivity(intent);
                return true;
            case (R.id.menuAccount):

                return true;
            case (R.id.menuSettings):

                return true;
            case (R.id.menuLogout):
                signOut();
                intent = new Intent(context, activityLogin.class);
                startActivity(intent);
                finish();
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
        Log.d("Main_SignOut","session_uid removed");
        Log.d("Main_SignOut","checking ... " + myPrefs.getStringValue("session_uid"));
        firebaseAuth.signOut();
    }
}
