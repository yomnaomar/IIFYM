package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.Gender;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.UnitSystem;
import com.example.kareem.IIFYM_Tracker.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityUserMacros extends AppCompatActivity implements View.OnClickListener {

    // UI Elements
    private SegmentedGroup  seggroupDisplay;
    private RadioButton     rbtnCalories, rbtnMacros;
    private EditText        etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    private TextView        lblUnitCarbs, lblUnitProtein, lblUnitFat, lblTotal, lblAmountTotal, lblPercentTotal, lblGramsCarbs, lblGramsProtein, lblGramsFat;
    private Button          btnFinish, btnReset;

    // Variables
    private int             BMR, caloriesInitial, calories, carbs, protein, fat, carbsPercent, proteinPercent, fatPercent, totalPercent, height1, height2, workoutFreq, goal;
    private boolean         isRegistered;
    private String          uid, email, name, dob;
    private Gender          gender;
    private UnitSystem      unitSystem;
    private float           weight;

    // Storage
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_macros);

        // Data from previous activity
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        dob = getIntent().getStringExtra("dob");
        gender = (Gender) getIntent().getSerializableExtra("gender");
        unitSystem = (UnitSystem) getIntent().getSerializableExtra("unitSystem");
        weight = getIntent().getFloatExtra("weight", 0.0f);
        height1 = getIntent().getIntExtra("height1", 0);
        height2 = getIntent().getIntExtra("height2", 0);
        workoutFreq = getIntent().getIntExtra("workoutFreq", 0);
        goal = getIntent().getIntExtra("goal", 0);

        // GUI
        initializeGUI();

        // Storage
        myPrefs = getPreferences(AppCompatActivity.MODE_PRIVATE);

        // Calculate values to be displayed
        getBMR();

        // Display values
        displayDefaultValues();
    }

    private void initializeGUI()
    {
        seggroupDisplay = (SegmentedGroup) findViewById(R.id.seggroupDisplay);
        rbtnCalories    = (RadioButton) findViewById(R.id.rbtnCalories);
        rbtnMacros      = (RadioButton) findViewById(R.id.rbtnMacros);
        etxtCalories    = (EditText) findViewById(R.id.etxtCalories);
        etxtCarbs       = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein     = (EditText) findViewById(R.id.etxtProtein);
        etxtFat         = (EditText) findViewById(R.id.etxtFat);
        lblUnitCarbs    = (TextView) findViewById(R.id.lblUnitCarbs);
        lblUnitProtein  = (TextView) findViewById(R.id.lblUnitProtein);
        lblUnitFat      = (TextView) findViewById(R.id.lblUnitFat);
        lblTotal        = (TextView) findViewById(R.id.lblTotal);
        lblAmountTotal  = (TextView) findViewById(R.id.lblAmountTotal);
        lblPercentTotal = (TextView) findViewById(R.id.lblPercentTotal);
        lblGramsCarbs   = (TextView) findViewById(R.id.lblGramsCarbs);
        lblGramsProtein = (TextView) findViewById(R.id.lblGramsProtein);
        lblGramsFat     = (TextView) findViewById(R.id.lblGramsFat);
        btnFinish       = (Button) findViewById(R.id.btnFinish);
        btnReset        = (Button) findViewById(R.id.btnReset);

        seggroupDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                displayChange();
            }});

        btnFinish.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    // MEN: BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
    // WOMEN: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) -161
    private void getBMR()
    {
        // Store weight/height in local variables in order to perform unit conversion if needed
        float valWeight = weight;
        int valHeight = height1;

        // Need to convert to Metric in order to calculate BMR
        if(unitSystem == UnitSystem.Imperial)
        {
            // Convert LB to KG
            valWeight /= 2.2046226218;
            // Convert Feet/Inches to CM
            valHeight = (int) ((30.48 * valHeight) + (2.54 * height2));
        }

        // Parse Date of Birth string
        String ageArr[] = dob.split("/");

        // Obtain Age value from Date of Birth String
        int valAge = getAge(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));

        if (gender == Gender.Male)
            BMR = (int) (10*valWeight + 6.25*valHeight + 5*valAge + 5.0);
        else
            BMR = (int) (10*valWeight + 6.25*valHeight + 5*valAge - 161.0);

        // Activity Factor Multiplier
        // None (little or no exercise)
        if (workoutFreq == 0)
            caloriesInitial = (int) (BMR * 1.2);

        // Low (1-3 times/week)
        else if (workoutFreq == 1)
            caloriesInitial = (int) (BMR * 1.35);

        // Medium (3-5 days/week)
        else if (workoutFreq == 2)
            caloriesInitial = (int) (BMR * 1.5);

        // High (6-7 days/week)
        else if (workoutFreq == 3)
            caloriesInitial = (int) (BMR * 1.7);

        // Very High (physical job or 7+ times/week)
        else
            caloriesInitial = (int) (BMR * 1.9);

        // Weight goals
        // Lose weight
        if (goal == 0)
            caloriesInitial -= 250.0;

        // Maintain weight
        else if (caloriesInitial == 1) {} // Do nothing

        else
            caloriesInitial += 250.0;

        // Macronutrient ratio default
        carbsPercent = 50;
        proteinPercent = 25;
        fatPercent = 50;

        // Calculate Macro values from ratios
        carbs = Math.round(carbsPercent*0.01f*caloriesInitial/4);
        protein =  Math.round(proteinPercent*0.01f*caloriesInitial/4);
        fat =  Math.round(fatPercent*0.01f*caloriesInitial/9);
    }

    private void displayValues() {
        if(rbtnCalories.isChecked()) {
            etxtCalories.setText(calories);
            etxtCarbs.setText(carbsPercent);
            etxtProtein.setText(proteinPercent);
            etxtFat.setText(fatPercent);
            lblAmountTotal.setText(carbsPercent + proteinPercent + fatPercent);
            int total = Integer.parseInt(lblAmountTotal.getText().toString());
            if (total == 100){
                lblAmountTotal.setTextColor(getResources().getColor(R.color.Custom_Green));
            }
            else if (total > 100) {
                lblAmountTotal.setTextColor(getResources().getColor(R.color.Custom_Red));
            }
            else if (total < 100){
                lblAmountTotal.setTextColor(getResources().getColor(R.color.Custom_Gray));
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnReset:
                displayDefaultValues();
                displayChange();
                break;
            case R.id.btnFinish:
                isRegistered = true;
                // Store values in DB
                break;
        }
    }

    private void readUserInput()
    {
        calories = Integer.parseInt(etxtCalories.getText().toString());
        carbs = Integer.parseInt(etxtCarbs.getText().toString());
        protein = Integer.parseInt(etxtProtein.getText().toString());
        fat = Integer.parseInt(etxtFat.getText().toString());
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = myPrefs.edit();
        if(rbtnCalories.isChecked()) {
            editor.putInt("temp_display", 0); // Calories
            editor.putInt("temp_total", Integer.parseInt(lblAmountTotal.getText().toString()));
            editor.putInt("temp_gramsCarbs",Integer.parseInt(lblGramsCarbs.getText().toString()));
            editor.putInt("temp_gramsProtein",Integer.parseInt(lblGramsProtein.getText().toString()));
            editor.putInt("temp_gramsFat",Integer.parseInt(lblGramsFat.getText().toString()));
        }
        else
            editor.putInt("temp_display", 1); // Macros

        editor.putInt("temp_calories", Integer.parseInt(etxtCalories.getText().toString()));
        editor.putInt("temp_carbs", Integer.parseInt(etxtCarbs.getText().toString()));
        editor.putInt("temp_protein", Integer.parseInt(etxtProtein.getText().toString()));
        editor.putInt("temp_fat", Integer.parseInt(etxtFat.getText().toString()));
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(myPrefs.getInt("temp_display",0) == 0) { // Calories
            rbtnCalories.setChecked(true);
            lblAmountTotal.setText(myPrefs.getInt("temp_total",0) + "");
            lblGramsCarbs.setText(myPrefs.getInt("temp_gramsCarbs",0) + "");
            lblGramsProtein.setText(myPrefs.getInt("temp_gramsProtein",0) + "");
            lblGramsFat.setText(myPrefs.getInt("temp_gramsFat",0) + "");
        }
        else
            rbtnMacros.setChecked(true);

        etxtCalories.setText(myPrefs.getInt("temp_calories",0) + "");
        etxtCarbs.setText(myPrefs.getInt("temp_carbs",0) + "");
        etxtProtein.setText(myPrefs.getInt("temp_protein",0) + "");
        etxtFat.setText(myPrefs.getInt("temp_fat",0) + "");
        displayChange();
        super.onResume();
    }

    private void displayDefaultValues() {
        rbtnCalories.setChecked(true);
        etxtCalories.setText(calories + "");
        etxtCarbs.setText(50 + "");
        etxtProtein.setText(25 + "");
        etxtFat.setText(25 + "");
        lblAmountTotal.setText((50 + 25 + 25) + "");
        int total = Integer.parseInt(lblAmountTotal.getText().toString());
        if (total == 100) {
            lblAmountTotal.setTextColor(getResources().getColor(R.color.Custom_Green));
        } else if (total > 100) {
            lblAmountTotal.setTextColor(getResources().getColor(R.color.Custom_Red));
        } else if (total < 100) {
            lblAmountTotal.setTextColor(getResources().getColor(R.color.Custom_Gray));
        }
        lblGramsCarbs.setText(50 * 0.01f * calories + "");
        lblGramsProtein.setText(25 * 0.01f * calories + "");
        lblGramsFat.setText(25 * 0.01f * calories + "");
    }

    private void displayChange()
    {
        if(rbtnCalories.isChecked())
        {
            etxtCalories.setEnabled(true);
            lblUnitCarbs.setText("%");
            lblUnitProtein.setText("%");
            lblUnitFat.setText("%");
            lblTotal.setVisibility(View.VISIBLE);
            lblAmountTotal.setVisibility(View.VISIBLE);
            lblPercentTotal.setVisibility(View.VISIBLE);
            lblGramsCarbs.setVisibility(View.VISIBLE);
            lblGramsProtein.setVisibility(View.VISIBLE);
            lblGramsFat.setVisibility(View.VISIBLE);
        }
        else
        {
            etxtCalories.setEnabled(false);
            lblUnitCarbs.setText("g");
            lblUnitProtein.setText("g");
            lblUnitFat.setText("g");
            lblTotal.setVisibility(View.INVISIBLE);
            lblAmountTotal.setVisibility(View.INVISIBLE);
            lblPercentTotal.setVisibility(View.INVISIBLE);
            lblGramsCarbs.setVisibility(View.INVISIBLE);
            lblGramsProtein.setVisibility(View.INVISIBLE);
            lblGramsFat.setVisibility(View.INVISIBLE);
        }
    }

    private int getAge (int _year, int _month, int _day) {
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
    }
}
