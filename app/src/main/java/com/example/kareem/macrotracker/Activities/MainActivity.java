package com.example.kareem.macrotracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kareem.macrotracker.Custom_Objects.DailyMeal;
import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.User;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.DailyMealAdapter;
import com.example.kareem.macrotracker.ViewComponents.OnListItemDeletedListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnListItemDeletedListener {

    private TextView Text_CarbsGoal, Text_ProteinGoal, Text_FatGoal, Text_CarbsLeft, Text_ProteinLeft, Text_FatLeft;
    private TextView usernameview;
    private Button Button_AddSavedMeal, Button_AddQuickMeal;

    private ArrayList<DailyMeal> ArrayList_DailyMeals;
    private DailyMealAdapter My_DailyMealAdapter;
    private ListView Meals_ListView;

    public static final String CarbsPrefKey = "EditTextPrefCarbsGoal";
    public static final String ProteinPrefKey = "EditTextPrefProteinGoal";
    public static final String FatPrefKey = "EditTextPrefFatGoal";
    public  int CarbsDefault ;
    public  int ProteinDefault ;
    public  int FatDefault ;

    private DatabaseConnector My_DB;

    Portion_Type portion = null;
    int daily_consumption;
    boolean isLogged;

    private String user_name;
    private int user_id;
    private CoordinatorLayout coordinatorLayout;

    private User currentUser;

    View parentLayout;

    SharedPreferences settings;

    int Carbs_Val,Protein_Val,Fat_Val;
    int userBMR;
    double userCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        //Declaring
        Text_CarbsGoal = (TextView) findViewById(R.id.Text_CarbsGoal);
        Text_ProteinGoal = (TextView) findViewById(R.id.Text_ProteinGoal);
        Text_FatGoal = (TextView) findViewById(R.id.Text_FatGoal);
        Text_CarbsLeft = (TextView) findViewById(R.id.Text_CarbsLeft);
        Text_ProteinLeft = (TextView) findViewById(R.id.Text_ProteinLeft);
        Text_FatLeft = (TextView) findViewById(R.id.Text_FatLeft);

        usernameview=(TextView)findViewById(R.id.txt_username);

        Button_AddSavedMeal = (Button) findViewById(R.id.Button_AddSavedMeal);
        Button_AddSavedMeal.setOnClickListener(this);
        Button_AddQuickMeal = (Button) findViewById(R.id.Button_AddQuickMeal);
        Button_AddQuickMeal.setOnClickListener(this);

        ArrayList_DailyMeals = new ArrayList<DailyMeal>();
        My_DailyMealAdapter = new DailyMealAdapter(this, ArrayList_DailyMeals);
        Meals_ListView = (ListView) findViewById(R.id.ListView_Meals);
        Meals_ListView.setAdapter(My_DailyMealAdapter);
        Meals_ListView.setOnItemClickListener(this);

        My_DB = new DatabaseConnector(getApplicationContext());
        parentLayout = findViewById(R.id.root_view);

        //TODO:(Abdulwahab) get current currentUser here
        Intent intent = getIntent();
        isLogged = intent.getBooleanExtra("logged",false);//checks if user just logged in
        getActiveUser(isLogged,intent); //get current user
        userBMR = getBMR(); // BMR fetched here
        setPrefMacros(); // puts preferred macro in shared prefs

        //TODO KILL DUMMIES
//        User DummyBoy = new User();
//        //DummyBoy = My_DB.getUser_ReturnsUser("DummyBoy");
//        DummyBoy = My_DB.getUser_ReturnsUser("DebuggerDummy");
//        Toast.makeText(this,"Welcome "+ DummyBoy.getUser_name() +" ID: "+DummyBoy.getUser_id(),Toast.LENGTH_SHORT).show(); //TODO: call only once
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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
            intent.setClassName(this, "com.example.kareem.macrotracker.Activities.ViewSavedMealsActivity");
            startActivity(intent);
            return true;
        }
        if(id==R.id.logout_menu_btn)
        {
            //My_DB.close();
            finish();
            Intent in = new Intent(getApplicationContext(), Login.class);
            startActivity(in);
            return true;
        }
        if(id==R.id.profile_menu_btn)
        {
            //Intent in = new Intent(getApplicationContext(), );
            //startActivity(in);
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

        UpdateArrayList();
        UpdateMacros();
    }

    @Override
    protected void onPause() {

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_name", user_name); // here string is the value you want to save
        editor.commit();


        super.onPause();
    }

    public void UpdateMacros (){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        int NumberOfMeals = My_DailyMealAdapter.getCount();

        int CarbGoals = Integer.parseInt(prefs.getString(CarbsPrefKey, CarbsDefault + ""));
        int ProteinGoals = Integer.parseInt(prefs.getString(ProteinPrefKey, ProteinDefault + ""));
        int FatGoals = Integer.parseInt(prefs.getString(FatPrefKey, FatDefault + ""));

        Text_CarbsGoal.setText(CarbGoals + "");
        Text_ProteinGoal.setText(ProteinGoals + "");
        Text_FatGoal.setText(FatGoals + "");

        int CarbsCurrent = 0;
        int ProteinCurrent = 0;
        int FatCurrent = 0;

        int CarbsLeft = 0;
        int ProteinLeft = 0;
        int FatLeft = 0;

        for (int i = 0; i < NumberOfMeals; i++) {
            DailyMeal TempMeal = My_DailyMealAdapter.getItem(i);
            int carbs = TempMeal.getCarbs();
            int protein = TempMeal.getProtein();
            int fat = TempMeal.getFat();

            CarbsCurrent    += carbs;
            ProteinCurrent  += protein;
            FatCurrent      += fat;
        }

        CarbsLeft = CarbGoals - CarbsCurrent;
        ProteinLeft = ProteinGoals - ProteinCurrent;
        FatLeft = FatGoals - FatCurrent;

        Text_CarbsLeft.setText(CarbsLeft + "");
        Text_ProteinLeft.setText(ProteinLeft + "");
        Text_FatLeft.setText(FatLeft + "");
    }

    //Updates My_MealAdapter
    //TODO: TEST AFTER IMPLEMENTING DATABASE
    private void UpdateArrayList() {
        My_DailyMealAdapter.clear();
        Cursor AllDailyMeals_Cursor = My_DB.getAllDailyMeals();
        int count = AllDailyMeals_Cursor.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                AllDailyMeals_Cursor.moveToNext();

                int     daily_meal_id       = AllDailyMeals_Cursor.getInt(1);      //meal_id
                int     daily_position      = AllDailyMeals_Cursor.getInt(2);      //position
                int     daily_multiplier    = AllDailyMeals_Cursor.getInt(3);      //multiplier

                Meal M = My_DB.getMeal(daily_meal_id);

                String M_name            = M.getMeal_name();
                int M_carbs              = M.getCarbs()*daily_multiplier;
                int M_protein            = M.getProtein()*daily_multiplier;
                int M_fat                = M.getFat()*daily_multiplier;
                Portion_Type M_portion   = M.getPortion();

                DailyMeal DM = new DailyMeal(M_name,daily_meal_id,M_carbs,M_protein,M_fat,M_portion,daily_position,daily_multiplier);
                My_DailyMealAdapter.add(DM);

                Log.d("DailyMeal Added:", "Name: "
                        + M.getMeal_name() + " " + M.getMeal_id() + " "
                        + M_carbs + " " + M_protein + " " + M_fat + " multiplier: " + daily_multiplier);
            }
        }
    }

    @Override
    public void onItemDeleted() {
        UpdateMacros();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onItemDeleted();
    }

    private void getActiveUser(boolean logged, Intent intent)
    {
        if(logged)
        {
            user_name = intent.getStringExtra("user_name");
            user_id = intent.getIntExtra("user_id",My_DB.fetchUserID(user_name,getApplicationContext()));
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "Welcome "+ user_name +" ID: "+ user_id, Snackbar.LENGTH_SHORT);
            snackbar.show();
            isLogged=false;
        }
        else {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            user_name = settings.getString("user_name", "");
        }
        usernameview.setText(user_name);
        currentUser = My_DB.getUserObject(user_name);
    }

    private void setPrefMacros()
    {
        //Prefs defaults from database
        CarbsDefault =Carbs_Val;
        FatDefault =Fat_Val;
        ProteinDefault = Protein_Val;

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt("pref_Carbs", currentUser.getPercent_carbs());
        editor.putInt("pref_Protein",  currentUser.getPercent_protein());
        editor.putInt("pref_Fat",  currentUser.getPercent_fat());
        editor.putInt("user_carbs", CarbsDefault);
        editor.putInt("user_fat", FatDefault);
        editor.putInt("user_protein", ProteinDefault);
        editor.putInt("cals", (int) userCalories);
        editor.putInt("BMR", userBMR);
        editor.commit();
    }
    //TODO: get macro percent from DB and other info to get macro values
    //MEN: BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
    //WOMEN: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) -161
    private int getBMR()
    {
        double BMR; // b/w 1000 - 3000
        //get all user data height, weight ,age:
        float Weight_Val = currentUser.getWeight();
        float Height_Val = currentUser.getHeight();
        String gender = currentUser.getGender();
        int Age_Val = currentUser.getAge();
        double Caloric_Intake;
        double Carb_Percent,Protein_Percent,Fat_Percent;

        Log.d("BMRINFO", ""+ Weight_Val+ " "+ Height_Val+" "+gender+" "+Age_Val);
        if(currentUser.getWeight_unit()!= 0) //not kg - convert from lbs to kg
        {
            Weight_Val = (Weight_Val/2.2046f);
            Log.d("WEIGHT", ""+Weight_Val);
        }
        if(currentUser.getHeight_unit()!=1)//not cm - convert from feet to cm
        {
            Height_Val = (Height_Val/0.032808f);
            Log.d("HEIGHT", ""+Height_Val);
        }
        if (gender.startsWith("M")) {
            BMR = (10*Weight_Val + 6.25*Height_Val + 5*Age_Val + 5.0); //Male
            Log.d("BMRMALE", ""+BMR);
        }
        else
        {
            BMR = (10*Weight_Val + 6.25*Height_Val + 5*Age_Val - 161.0); //Female
            Log.d("BMRFEMALE", ""+BMR);
        }

        //Activity Factor Multiplier
        //Sedentary
        if (currentUser.getWorkout_freq() == 0) {
            Caloric_Intake = BMR * 1.2;

        }
        //Lightly Active
        else if (currentUser.getWorkout_freq() == 1) {
            Caloric_Intake = BMR * 1.35;
            Log.d("WORKOUT", ""+Caloric_Intake);
        }
        //Moderately Active
        else if (currentUser.getWorkout_freq() == 2) {
            Caloric_Intake = BMR * 1.5;
        }
        //Very Active
        else if (currentUser.getWorkout_freq() ==3) {
            Caloric_Intake = BMR * 1.7;
        }
        else
        {
            Caloric_Intake = BMR * 1.9;
        }

        //Weight goals
        if (currentUser.getGoal() == 0) { //lose
            Caloric_Intake -= 250.0;
        }
        else if (currentUser.getGoal()== 1) { //maintain
            //Do nothing
        }
        else { //maintain
            Caloric_Intake += 250.0;
        }
        //Macronutrient ratio calculation
        Carb_Percent = currentUser.getPercent_carbs()/100.0;
        Protein_Percent = currentUser.getPercent_protein()/100.0;
        Fat_Percent = currentUser.getPercent_fat()/100.0;


        Carbs_Val = (int) ((Carb_Percent * Caloric_Intake) /4.0); //carbs = 4kcal/g
        Protein_Val = (int) ((Protein_Percent * Caloric_Intake) / 4.0); //protein = 4kcal/g
        Fat_Val = (int) ((Fat_Percent * Caloric_Intake)/ 9.0); //fat = 9kcal/g


        userCalories = Caloric_Intake;
        return (int) BMR;
    }

}
