package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.example.kareem.IIFYM_Tracker.Custom_Objects.DailyItem;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Food;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Portion_Type;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.DailyMealAdapter;
import com.example.kareem.IIFYM_Tracker.ViewComponents.OnListItemDeletedListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class activityMain extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnListItemDeletedListener {

    // GUI
    private TextView lblCaloriesCurrent, lblCaloriesLeft, lblCaloriesGoal;
    private TextView lblCarbsCurrent,    lblCarbsLeft,    lblCarbsGoal;
    private TextView lblProteinCurrent,  lblProteinLeft,  lblProteinGoal;
    private TextView lblFatCurrent,      lblFatLeft,      lblFatGoal;
    private ArrayList<DailyItem> arrDailyMeals;
    private DailyMealAdapter adapterDailyMeals;
    private ListView listViewDailyMeals;
    private RoundCornerProgressBar progressBarCarbs, progressBarProtein, progressBarFat;
    private Animation mEnterAnimation, mExitAnimation;

    // Variables
    private Context context;
    public int defaultCarbs, defaultProtein, defaultFat;
    public static final String CarbsPrefKey = "EditTextPrefCarbsGoal";
    public static final String ProteinPrefKey = "EditTextPrefProteinGoal";
    public static final String FatPrefKey = "EditTextPrefFatGoal";

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

        // Get User
        uid = myPrefs.getStringValue("session_uid");
        currentUser = DB_SQLite.retrieveUser(uid);

        // GUI
        initializeGUI();
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    public void UpdateMacros (){
        int CarbGoals = Integer.parseInt(prefs.getString(CarbsPrefKey, defaultCarbs + ""));
        int ProteinGoals = Integer.parseInt(prefs.getString(ProteinPrefKey, defaultProtein + ""));
        int FatGoals = Integer.parseInt(prefs.getString(FatPrefKey, defaultFat + ""));

        lblCarbsGoal.setText(CarbGoals + "");
        lblProteinGoal.setText(ProteinGoals + "");
        lblFatGoal.setText(FatGoals + "");

        int CarbsCurrent = 0;
        int ProteinCurrent = 0;
        int FatCurrent = 0;

        int CarbsLeft = 0;
        int ProteinLeft = 0;
        int FatLeft = 0;

        int NumberOfMeals = adapterDailyMeals.getCount();
        for (int i = 0; i < NumberOfMeals; i++) {
            DailyItem TempMeal = adapterDailyMeals.getItem(i);
            int carbs = Math.round(TempMeal.getCarbs());
            int protein = Math.round(TempMeal.getProtein());
            int fat = Math.round(TempMeal.getFat());

            CarbsCurrent    += carbs;
            ProteinCurrent  += protein;
            FatCurrent      += fat;
        }

        CarbsLeft = CarbGoals - CarbsCurrent;
        ProteinLeft = ProteinGoals - ProteinCurrent;
        FatLeft = FatGoals - FatCurrent;

        lblCarbsLeft.setText(CarbsLeft + "");
        lblCarbsCurrent.setText(CarbsCurrent + "");
        if(CarbsCurrent <= CarbGoals)
        {
            progressBarCarbs.setProgress(100 * CarbsCurrent / CarbGoals);
            progressBarCarbs.setSecondaryProgress(0);
        }
        else
        {
            int CarbsExcess = CarbsCurrent - CarbGoals;
            progressBarCarbs.setProgress(100*((float) CarbGoals)/CarbsCurrent);
            progressBarCarbs.setSecondaryProgress(100);
        }

        lblProteinLeft.setText(ProteinLeft + "");
        lblProteinCurrent.setText(ProteinCurrent + "");
        if(ProteinCurrent <= ProteinGoals)
        {
            progressBarProtein.setProgress(100 * ProteinCurrent / ProteinGoals);
            progressBarProtein.setSecondaryProgress(0);
        }
        else
        {
            int ProteinExcess = ProteinCurrent - ProteinGoals;
            progressBarProtein.setProgress(100*ProteinGoals/ProteinCurrent);
            progressBarProtein.setSecondaryProgress(100);
        }

        lblFatLeft.setText(FatLeft + "");
        lblFatCurrent.setText(FatCurrent + "");
        if(FatCurrent <= FatGoals)
        {
            progressBarFat.setProgress(100 * FatCurrent / FatGoals);
            progressBarFat.setSecondaryProgress(0);
        }
        else
        {
            int FatExcess = FatCurrent - FatGoals;
            progressBarFat.setProgress(100*FatGoals/FatCurrent);
            progressBarFat.setSecondaryProgress(100);
        }
    }

    //Updates My_MealAdapter
    //TODO: TEST AFTER IMPLEMENTING DATABASE
    public void UpdateArrayList() {
        adapterDailyMeals.clear();
        Cursor AllDailyMeals_Cursor = DB_SQLite.getAllDailyMeals();
        int count = AllDailyMeals_Cursor.getCount();
        Log.i("Count","Count = " + count);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AllDailyMeals_Cursor.moveToNext();

                int     daily_position      = AllDailyMeals_Cursor.getInt(0);      //position
                int     daily_meal_id       = AllDailyMeals_Cursor.getInt(1);      //meal_id
                float   daily_multiplier    = AllDailyMeals_Cursor.getFloat(2);      //multiplier
                Log.i("meal_id", daily_meal_id + "");
                Log.i("position", daily_position + "");
                Log.i("multiplier", daily_multiplier + "");

                Food M = DB_SQLite.getMeal(daily_meal_id);

                String M_name            = M.getMeal_name();
                int M_carbs              = Math.round(M.getCarbs()*daily_multiplier);
                int M_protein            = Math.round(M.getProtein()*daily_multiplier);
                int M_fat                = Math.round(M.getFat()*daily_multiplier);
                Portion_Type M_portion   = M.getPortion();

                DailyItem DM = new DailyItem(M_name,daily_meal_id,M_carbs,M_protein,M_fat,M_portion,daily_position,daily_multiplier);
                adapterDailyMeals.add(DM);

                Log.i("DailyItem Added:", "Name: "
                        + M.getMeal_name() + " " + M.getMeal_id() + " "
                        + M_carbs + " " + M_protein + " " + M_fat + " position: " + daily_position + " multiplier: " + daily_multiplier);
            }
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()){

        }
    }

    private void AddItem() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,AddSavedMealActivity.class);
        startActivity(intent);
    }

    @Override public void onItemDeleted() {
        UpdateArrayList();
        UpdateMacros();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DailyItem DM = (DailyItem) parent.getItemAtPosition(position);
        int DM_ID = DM.getMeal_id();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("Meal_ID", DM_ID);
        intent.putExtra("position", position);
        intent.putExtra("isDaily", true);
        startActivity(intent);
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

    private void signOut() {
        myPrefs.addPreference("session_uid", "");
        Log.d("Main_SignOut","session_uid removed");
        Log.d("Main_SignOut","checking ... " + myPrefs.getStringValue("session_uid"));
        firebaseAuth.signOut();
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

        progressBarCarbs = (RoundCornerProgressBar) findViewById(R.id.progressBarCarbs);
        progressBarProtein = (RoundCornerProgressBar) findViewById(R.id.progressBarProtein);
        progressBarFat = (RoundCornerProgressBar) findViewById(R.id.progressBarFat);

        arrDailyMeals = new ArrayList<DailyItem>();
        adapterDailyMeals = new DailyMealAdapter(this, arrDailyMeals);

        listViewDailyMeals = (ListView) findViewById(R.id.listViewDailyMeals);
        listViewDailyMeals.setAdapter(adapterDailyMeals);
        listViewDailyMeals.setOnItemClickListener(this);

        // Setup enter and exit animation
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);

        // User Greetings
        Toast toast = Toast.makeText(context, "Hello " + currentUser.getName() + "!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
