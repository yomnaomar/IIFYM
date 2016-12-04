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

import com.example.kareem.macrotracker.Custom_Objects.Meal;
import com.example.kareem.macrotracker.Custom_Objects.Portion_Type;
import com.example.kareem.macrotracker.Custom_Objects.User;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.MealAdapter;
import com.example.kareem.macrotracker.ViewComponents.OnListItemDeletedListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnListItemDeletedListener {

    private TextView Text_CarbsGoal, Text_ProteinGoal, Text_FatGoal, Text_CarbsLeft, Text_ProteinLeft, Text_FatLeft;
    private TextView usernameview;
    private Button Button_AddSavedMeal, Button_AddQuickMeal;

    private ArrayList<Meal> ArrayList_Meals;
    private MealAdapter My_MealAdapter;
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

        ArrayList_Meals = new ArrayList<Meal>();
        My_MealAdapter = new MealAdapter(this, ArrayList_Meals);
        Meals_ListView = (ListView) findViewById(R.id.ListView_Meals);
        Meals_ListView.setAdapter(My_MealAdapter);
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
        if (id == R.id.action_MacroSettings) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.example.kareem.macrotracker.Activities.MacronutrientPreferenceActivity");
            startActivity(intent);
            return true;
        }
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
        if(id==R.id.macro_settings_btn)
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
        intent.setClass(context,AddSavedMealActivity.class);
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

        int NumberOfMeals = My_MealAdapter.getCount();

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
            Meal TempMeal = My_MealAdapter.getItem(i);
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
        My_MealAdapter.clear();
        Cursor C = My_DB.getAllDailyMeals();
        int count = C.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                C.moveToNext();
                int     meal_id         = C.getInt(0);      //meal)id
                String  meal_name       = C.getString(1);   //meal_name
                String  date_created    = C.getString(2);   //date_created
                int     carbs           = C.getInt(3);      //carbs
                int     protein         = C.getInt(4);      //protein
                int     fat             = C.getInt(5);      //fat
                portion = portion.values()[C.getInt(6)];    //portion
                int   daily_consumption = C.getInt(7);      //daily_consumption
                int     user_id         = C.getInt(8);      //user_id
                Meal M = new Meal(meal_id,meal_name,carbs,protein,fat,portion,daily_consumption,user_id);
                Log.d("Meal Retrieved:", "Name: " + M.getMeal_name() + " " + M.getMeal_id() + " "
                        + M.getCarbs() + " " + M.getProtein() + " " + M.getFat());
                My_MealAdapter.add(M);
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

        editor.putString("pref_Carbs", currentUser.getPercent_carbs() + "");
        editor.putString("pref_Protein",  currentUser.getPercent_protein() + "");
        editor.putString("pref_Fat",  currentUser.getPercent_fat() + "");
        editor.commit();
    }
    //TODO: get macro percent from DB and other info to get macro values
    //MEN: BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
    //WOMEN: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) -161
    private int getBMR()
    {
        int BMR; // b/w 1000 - 3000
        //get all user data height, weight ,age:
        float Weight_Val = currentUser.getWeight();
        float Height_Val = currentUser.getHeight();
        String gender = currentUser.getGender();
        int Age_Val = currentUser.getAge();
        double Caloric_Intake;
        int Carb_Percent,Protein_Percent,Fat_Percent;

        if(currentUser.getWeight_unit()!= 0) //not kg - convert from lbs to kg
        {
            Weight_Val = Weight_Val/2.2046f;
        }
        if(currentUser.getHeight_unit()!=1)//not cm - convert from feet to cm
        {
            Height_Val = Height_Val/0.032808f;
        }
        if (gender.startsWith("M")) {
            BMR = (int) (10*Weight_Val + 6.25*Height_Val + 5*Age_Val + 5.0); //Male
        }
        else
        {
            BMR = (int) (10*Weight_Val + 6.25*Height_Val + 5*Age_Val - 161.0); //Female
        }

        //Activity Factor Multiplier
        //Sedentary
        if (currentUser.getWorkout_freq() == 0) {
            Caloric_Intake = BMR * 1.2;
        }
        //Lightly Active
        else if (currentUser.getWorkout_freq() == 1) {
            Caloric_Intake = BMR * 1.35;
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
        Carb_Percent = currentUser.getPercent_carbs();
        Protein_Percent = currentUser.getPercent_protein();
        Fat_Percent = currentUser.getPercent_fat();

        Carbs_Val = (int) ((Carb_Percent * Caloric_Intake) /4.0); //carbs = 4kcal/g
        Protein_Val = (int) ((Protein_Percent * Caloric_Intake) / 4.0); //protein = 4kcal/g
        Fat_Val = (int) ((Fat_Percent * Caloric_Intake)/ 9.0); //fat = 9kcal/g

        return BMR;
    }

}
