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

import static android.view.View.GONE;

public class activityUserMacros extends AppCompatActivity implements View.OnClickListener {

    // UI Elements
    private SegmentedGroup  seggroupDisplay;
    private RadioButton     rbtnCalories, rbtnMacros;
    private EditText        etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    private TextView        lblUnitCarbs, lblUnitProtein, lblUnitFat, lblTotal, lblAmountTotal, lblPercentTotal, lblGramsCarbs, lblGramsProtein, lblGramsFat;
    private Button          btnFinish, btnReset;

    // Variables
    private int             calories, carbs, protein, fat, carbsPercent, proteinPercent, fatPercent, totalPercent, height1, height2, workoutFreq, goal;
    private boolean         isRegistered;
    private String          uid, email, name, dob;
    private Gender          gender;
    private UnitSystem      unitSystem;
    private float           weight;

    // Storage
    private SharedPreferences myPrefs;

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

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnReset:
                // Reset macro values
                break;
            case R.id.btnFinish:
                isRegistered = true;
                // Store values in DB
                break;
        }
    }

    private void readUserInput()
    {

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
    }

    private int getBMR()
    {
        double BMR; // b/w 1000 - 3000
        //get all user data height, weight ,age
        float valWeight;
        int valHeight;
        if(unitSystem == UnitSystem.Imperial)
        {
            valWeight = (float) (weight / 2.2046226218);
            valHeight = (int) ((30.48 * height1) + (2.54 * height2));
        }
        String ageArr[] = dob.split("/");
        int valAge = getAge(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));
        double Caloric_Intake;
        double Carb_Percent,Protein_Percent,Fat_Percent;
        Log.d("BMRINFO", ""+ valWeight+ " "+ valHeight+" "+gender+" "+valAge);
        if (gender.startsWith("M")) {
            BMR = (10*valWeight + 6.25*valHeight + 5*valAge + 5.0); //Male
            Log.d("BMRMALE", ""+BMR);
        }
        else
        {
            BMR = (10*valWeight + 6.25*valHeight + 5*valAge - 161.0); //Female
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
        Carbs_Val = (int) ((Carb_Percent * Caloric_Intake) /4.0); //icon_carbs = 4kcal/g
        Protein_Val = (int) ((Protein_Percent * Caloric_Intake) / 4.0); //icon_protein = 4kcal/g
        Fat_Val = (int) ((Fat_Percent * Caloric_Intake)/ 9.0); //icon_fat = 9kcal/g
        userCalories = Caloric_Intake;
        return (int) BMR;
    }

    public int getAge (int _year, int _month, int _day) {
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
