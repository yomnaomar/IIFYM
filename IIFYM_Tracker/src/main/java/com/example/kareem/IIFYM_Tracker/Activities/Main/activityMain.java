package com.example.kareem.IIFYM_Tracker.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.example.kareem.IIFYM_Tracker.Activities.Old_Login.Login_Abdu;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.MacroSettings;
import com.example.kareem.IIFYM_Tracker.Activities.Settings.UserProfile_Mina;
import com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification.activityLogin;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.DailyMeal;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Meal;
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
    private TextView etxtCarbsGoal, etxtProteinGoal, etxtFatGoal;
    private TextView Text_CarbsLeft, Text_ProteinLeft, Text_FatLeft;
    private TextView Text_CarbsCurrent, Text_ProteinCurrent, Text_FatCurrent;
    private Button Button_AddSavedMeal, Button_AddQuickMeal;

    private ArrayList<DailyMeal> ArrayList_DailyMeals;
    private DailyMealAdapter My_DailyMealAdapter;
    private ListView Meals_ListView;
    private IconRoundCornerProgressBar Carb_ProgressBar, Protein_ProgressBar, Fat_ProgressBar;

    public static final String CarbsPrefKey = "EditTextPrefCarbsGoal";
    public static final String ProteinPrefKey = "EditTextPrefProteinGoal";
    public static final String FatPrefKey = "EditTextPrefFatGoal";
    public  int CarbsDefault ;
    public  int ProteinDefault ;
    public  int FatDefault ;

    View parentLayout;

    SharedPreferences settings;

    // Storage
    SharedPreferenceHelper myPrefs;
    private FirebaseAuth mAuth;
    private SQLiteConnector DB_SQLite;

    private String uid;
    private User currentUser;

    // Animation
    private Animation mEnterAnimation, mExitAnimation;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        DB_SQLite = new SQLiteConnector(context);
        myPrefs = new SharedPreferenceHelper(context);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Declaring
        etxtCarbsGoal = (TextView) findViewById(R.id.Text_CarbsGoal);
        etxtProteinGoal = (TextView) findViewById(R.id.Text_ProteinGoal);
        etxtFatGoal = (TextView) findViewById(R.id.Text_FatGoal);

        Text_CarbsLeft = (TextView) findViewById(R.id.Text_CarbsLeft);
        Text_ProteinLeft = (TextView) findViewById(R.id.Text_ProteinLeft);
        Text_FatLeft = (TextView) findViewById(R.id.Text_FatLeft);

        Text_CarbsCurrent = (TextView) findViewById(R.id.Text_CarbsCurrent);
        Text_ProteinCurrent = (TextView) findViewById(R.id.Text_ProteinCurrent);
        Text_FatCurrent = (TextView) findViewById(R.id.Text_FatCurrent);

        Carb_ProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.Carb_ProgressBar);
        Protein_ProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.Protein_ProgressBar);
        Fat_ProgressBar = (IconRoundCornerProgressBar) findViewById(R.id.Fat_ProgressBar);

        Button_AddSavedMeal = (Button) findViewById(R.id.Button_AddSavedMeal);
        Button_AddSavedMeal.setOnClickListener(this);
        Button_AddQuickMeal = (Button) findViewById(R.id.Button_AddQuickMeal);
        Button_AddQuickMeal.setOnClickListener(this);

        ArrayList_DailyMeals = new ArrayList<DailyMeal>();
        My_DailyMealAdapter = new DailyMealAdapter(this, ArrayList_DailyMeals);

        Meals_ListView = (ListView) findViewById(R.id.ListView_Meals);
        Meals_ListView.setAdapter(My_DailyMealAdapter);
        Meals_ListView.setOnItemClickListener(this);


        parentLayout = findViewById(R.id.root_view);

        // Getting the current user
        uid = myPrefs.getStringValue("session_uid");
        currentUser = DB_SQLite.retrieveUser(uid);

        // Setup enter and exit animation
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_woman, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (currentUser.getGender() == 0) { //Male
            getMenuInflater().inflate(R.menu.menu_home_man, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_home_woman, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            //DB_SQLite.close();
            finish();
            Intent in = new Intent(getApplicationContext(), Login_Abdu.class);
            startActivity(in);
            return true;
        }
        if(id==R.id.logout_firbase_menu_btn)
        {
            signOut();
            finish();
            Intent in = new Intent(getApplicationContext(), activityLogin.class);
            startActivity(in);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Button_AddSavedMeal:
                AddSavedMeal();
                break;
            case R.id.Button_AddQuickMeal:
                AddQuickMeal();
                break;
        }
    }

    private void AddSavedMeal() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,SelectSavedMealActivity.class);
        startActivity(intent);
    }

    private void AddQuickMeal() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,AddQuickMealActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void UpdateMacros (){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        int CarbGoals = Integer.parseInt(prefs.getString(CarbsPrefKey, CarbsDefault + ""));
        int ProteinGoals = Integer.parseInt(prefs.getString(ProteinPrefKey, ProteinDefault + ""));
        int FatGoals = Integer.parseInt(prefs.getString(FatPrefKey, FatDefault + ""));

        etxtCarbsGoal.setText(CarbGoals + "");
        etxtProteinGoal.setText(ProteinGoals + "");
        etxtFatGoal.setText(FatGoals + "");

        int CarbsCurrent = 0;
        int ProteinCurrent = 0;
        int FatCurrent = 0;

        int CarbsLeft = 0;
        int ProteinLeft = 0;
        int FatLeft = 0;

        int NumberOfMeals = My_DailyMealAdapter.getCount();
        for (int i = 0; i < NumberOfMeals; i++) {
            DailyMeal TempMeal = My_DailyMealAdapter.getItem(i);
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

        Text_CarbsLeft.setText(CarbsLeft + "");
        Text_CarbsCurrent.setText(CarbsCurrent + "");
        if(CarbsCurrent <= CarbGoals)
        {
            Carb_ProgressBar.setProgress(100 * CarbsCurrent / CarbGoals);
            Carb_ProgressBar.setSecondaryProgress(0);
        }
        else
        {
            int CarbsExcess = CarbsCurrent - CarbGoals;
            Carb_ProgressBar.setProgress(100*((float) CarbGoals)/CarbsCurrent);
            Carb_ProgressBar.setSecondaryProgress(100);
        }

        Text_ProteinLeft.setText(ProteinLeft + "");
        Text_ProteinCurrent.setText(ProteinCurrent + "");
        if(ProteinCurrent <= ProteinGoals)
        {
            Protein_ProgressBar.setProgress(100 * ProteinCurrent / ProteinGoals);
            Protein_ProgressBar.setSecondaryProgress(0);
        }
        else
        {
            int ProteinExcess = ProteinCurrent - ProteinGoals;
            Protein_ProgressBar.setProgress(100*ProteinGoals/ProteinCurrent);
            Protein_ProgressBar.setSecondaryProgress(100);
        }

        Text_FatLeft.setText(FatLeft + "");
        Text_FatCurrent.setText(FatCurrent + "");
        if(FatCurrent <= FatGoals)
        {
            Fat_ProgressBar.setProgress(100 * FatCurrent / FatGoals);
            Fat_ProgressBar.setSecondaryProgress(0);
        }
        else
        {
            int FatExcess = FatCurrent - FatGoals;
            Fat_ProgressBar.setProgress(100*FatGoals/FatCurrent);
            Fat_ProgressBar.setSecondaryProgress(100);
        }
    }

    //Updates My_MealAdapter
    //TODO: TEST AFTER IMPLEMENTING DATABASE
    public void UpdateArrayList() {
        My_DailyMealAdapter.clear();
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

                Meal M = DB_SQLite.getMeal(daily_meal_id);

                String M_name            = M.getMeal_name();
                int M_carbs              = Math.round(M.getCarbs()*daily_multiplier);
                int M_protein            = Math.round(M.getProtein()*daily_multiplier);
                int M_fat                = Math.round(M.getFat()*daily_multiplier);
                Portion_Type M_portion   = M.getPortion();

                DailyMeal DM = new DailyMeal(M_name,daily_meal_id,M_carbs,M_protein,M_fat,M_portion,daily_position,daily_multiplier);
                My_DailyMealAdapter.add(DM);

                Log.i("DailyMeal Added:", "Name: "
                        + M.getMeal_name() + " " + M.getMeal_id() + " "
                        + M_carbs + " " + M_protein + " " + M_fat + " position: " + daily_position + " multiplier: " + daily_multiplier);
            }
        }
    }

    @Override
    public void onItemDeleted() {
        UpdateArrayList();
        UpdateMacros();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DailyMeal DM = (DailyMeal) parent.getItemAtPosition(position);
        int DM_ID = DM.getMeal_id();

        Intent intent = new Intent(getBaseContext(), ViewMealActivity.class);
        intent.putExtra("Meal_ID", DM_ID);
        intent.putExtra("position", position);
        intent.putExtra("isDaily", true);
        startActivity(intent);
    }

    private void signOut() {
        mAuth.signOut();
    }

}
