package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class activityUserMacros extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // UI Elements
    private SegmentedGroup  seggroupDisplay;
    private RadioButton     rbtnCalories, rbtnMacros;
    private EditText        etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    private TextView        lblTitle, lblUnitCarbs, lblUnitProtein, lblUnitFat, lblTotal, lblAmountTotal, lblPercentTotal, lblGramsCarbs, lblGramsProtein, lblGramsFat;
    private Button          btnFinish;
    private ImageButton     btnReset;

    // Final Variables (Cannot be changed)
    private int             height1, height2, workoutFreq, goal ,BMR, caloriesInitial;
    private float           weight;
    private String          uid, email, name, dob;
    private int             gender;
    private int             unitSystem;

    // Dyanamic Variables (Can be changed)
    private int             totalPercent, calories, carbs, protein, fat, carbsPercent, proteinPercent, fatPercent;
    private boolean         isRegistered, etxtCalories_isWatched, etxtMacros_areWatched;

    //Aninmation
    private Animation mEnterAnimation, mExitAnimation;

    // Storage
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_macros);

        // Storage
        myPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Data from previous Login activities
        getIntentData();

        // Calculate values to be displayed
        getBMR();

        // GUI
        initializeGUI();

        // Set Defaults
        defaultValues();

        // UI guide
        //beginChainTourGuide();
    }

    private void initializeGUI()
    {
        lblTitle        = (TextView) findViewById(R.id.lblTitle);
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
        btnReset        = (ImageButton) findViewById(R.id.btnReset);

        seggroupDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateGUI();
            }});

        addTextWatchers();

        btnFinish.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        // setup enter and exit animation
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
    }

    private void updateGUI() {
        if(rbtnCalories.isChecked())
        {
            etxtCalories.setEnabled(true);

            lblUnitCarbs.setText("%");
            lblUnitProtein.setText("%");
            lblUnitFat.setText("%");

            etxtCarbs.setText(carbsPercent + "");
            etxtProtein.setText(proteinPercent + "");
            etxtFat.setText(fatPercent + "");

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

            etxtCarbs.setText(carbs + "");
            etxtProtein.setText(protein + "");
            etxtFat.setText(fat + "");

            lblTotal.setVisibility(View.INVISIBLE);
            lblAmountTotal.setVisibility(View.INVISIBLE);
            lblPercentTotal.setVisibility(View.INVISIBLE);
            lblGramsCarbs.setVisibility(View.INVISIBLE);
            lblGramsProtein.setVisibility(View.INVISIBLE);
            lblGramsFat.setVisibility(View.INVISIBLE);
        }
    }

    private void updateValues(){
        if(rbtnCalories.isChecked()) // Calories
        {
            removeTextWatchers();

            lblGramsCarbs.setText("(" + Math.round(carbsPercent * 0.01f * calories/4) + " g)");
            lblGramsProtein.setText("(" + Math.round(proteinPercent * 0.01f * calories/4) + " g)");
            lblGramsFat.setText("(" + Math.round(fatPercent * 0.01f * calories/9) + " g)");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
            else
                lblAmountTotal.setTextColor(Color.parseColor("#D50000")); // Red

            addTextWatchers();
        }
        else if (rbtnMacros.isChecked()) // Macros
        {
            removeTextWatchers();



            addTextWatchers();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(rbtnCalories.isChecked())  // Calories
        {
            String currentCals = etxtCalories.getText().toString();
            if(currentCals.isEmpty())
                calories = 0;
            else
                calories = Integer.parseInt(currentCals);

            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbsPercent = 0;
            else
                carbsPercent = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                proteinPercent = 0;
            else
                proteinPercent = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fatPercent = 0;
            else
                fatPercent = Integer.parseInt(fatP);

            totalPercent = carbsPercent + proteinPercent + fatPercent;
            updateValues();
        }
        else {


        }
    }

    private void defaultValues() {
        // Macronutrient ratio default
        calories = caloriesInitial;
        carbsPercent = 50;
        proteinPercent = 25;
        fatPercent = 25;
        totalPercent = 100;

        // Calculate Macro values from ratios
        carbs = Math.round(carbsPercent*0.01f*calories/4);
        protein =  Math.round(proteinPercent*0.01f*calories/4);
        fat =  Math.round(fatPercent*0.01f*calories/9);

        // Consequently calls updateGUI
        rbtnCalories.setChecked(true);

        removeTextWatchers();

        etxtCalories.setText(calories + "");
        etxtCarbs.setText(carbsPercent + "");
        etxtProtein.setText(proteinPercent + "");
        etxtFat.setText(fatPercent + "");

        lblGramsCarbs.setText("(" + Math.round(carbsPercent * 0.01f * calories/4) + " g)");
        lblGramsProtein.setText("(" + Math.round(proteinPercent * 0.01f * calories/4) + " g)");
        lblGramsFat.setText("(" + Math.round(fatPercent * 0.01f * calories/9) + " g)");
        lblAmountTotal.setText(totalPercent + "");
        if (totalPercent == 100)
            lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
        else
            lblAmountTotal.setTextColor(Color.parseColor("#D50000")); // Red

        addTextWatchers();
    }

    // MALE:   BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
    // FEMALE: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) - 161
    private void getBMR()
    {
        // Store weight/height in local variables in order to perform unit conversion if needed
        float valWeight = weight;
        int valHeight = height1;

        // Need to convert to Metric in order to calculate BMR
        if(unitSystem == 1) // Imperial
        {
            // Convert LB to KG
            valWeight /= 2.2046226218;
            // Convert Feet/Inches to CM
            valHeight = (int) ((30.48 * valHeight) + (2.54 * height2));
        }

        // Parse Date of Birth string
        String ageArr[] = dob.split("-");

        // Obtain Age value from Date of Birth String
        int valAge = getAge(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));

        // Obtain Gender
        if (gender == 0) // Male
            BMR = (int) (10*valWeight + 6.25*valHeight + 5*valAge + 5.0);
        else
            BMR = (int) (10*valWeight + 6.25*valHeight + 5*valAge - 161.0);

        Log.d("getBMR", "BMR = " + BMR);

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
        else if (workoutFreq == 4)
            caloriesInitial = (int) (BMR * 1.9);

        Log.d("getBMR workoutfreq", "caloriesInitial = " + caloriesInitial);

        // Weight goals
        // Lose weight
        if (goal == 0)
            caloriesInitial -= 250.0;

            // Maintain weight
        else if (goal == 1)
        {} // Do nothing

        // Gain weight
        else if (goal == 2)
            caloriesInitial += 250.0;

        Log.d("getBMR goal", "caloriesInitial = " + caloriesInitial);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnReset:
                defaultValues();
                break;
            case R.id.btnFinish:
                // isRegistered = true;
                // Store values in DB
                break;
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = myPrefs.edit();
        if(rbtnCalories.isChecked()) {
            editor.putInt("temp_display", 0); // Calories
            editor.putInt("temp_total", Integer.parseInt(lblAmountTotal.getText().toString()));
            editor.putString("temp_gramsCarbs",lblGramsCarbs.getText().toString());
            editor.putString("temp_gramsProtein",lblGramsProtein.getText().toString());
            editor.putString("temp_gramsFat",lblGramsFat.getText().toString());
        }
        else
            editor.putInt("temp_display", 1); // Macros

        if(etxtCalories.getText().toString().isEmpty())
            editor.putInt("temp_calories", 0);
        else
            editor.putInt("temp_calories", Integer.parseInt(etxtCalories.getText().toString()));
        if(etxtCarbs.getText().toString().isEmpty())
            editor.putInt("temp_carbs", 0);
        else
            editor.putInt("temp_carbs", Integer.parseInt(etxtCarbs.getText().toString()));
        if(etxtProtein.getText().toString().isEmpty())
            editor.putInt("temp_protein", 0);
        else
            editor.putInt("temp_protein", Integer.parseInt(etxtProtein.getText().toString()));
        if(etxtFat.getText().toString().isEmpty())
            editor.putInt("temp_fat", 0);
        else
            editor.putInt("temp_fat", Integer.parseInt(etxtFat.getText().toString()));

        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Load preferences
        // If preferences unavailable, set defaults
        super.onResume();
        etxtCalories.setText(myPrefs.getInt("temp_calories", caloriesInitial) + "");

        if(myPrefs.getInt("temp_display",0) == 0) { // Calories
            rbtnCalories.setChecked(true);
            etxtCarbs.setText(myPrefs.getInt("temp_carbs", Integer.parseInt(etxtCarbs.getText().toString()))  + "");
            etxtProtein.setText(myPrefs.getInt("temp_protein", Integer.parseInt(etxtProtein.getText().toString()))  + "");
            etxtFat.setText(myPrefs.getInt("temp_fat", Integer.parseInt(etxtFat.getText().toString()))  + "");
            lblAmountTotal.setText(myPrefs.getInt("temp_total", Integer.parseInt(lblAmountTotal.getText().toString()))  + "");
            lblGramsCarbs.setText(myPrefs.getString("temp_gramsCarbs", lblGramsCarbs.getText().toString()));
            lblGramsProtein.setText(myPrefs.getString("temp_gramsProtein",  lblGramsProtein.getText().toString()));
            lblGramsFat.setText(myPrefs.getString("temp_gramsFat", lblGramsFat.getText().toString()));
        }
        else { // Macros
            rbtnMacros.setChecked(true);
            etxtCarbs.setText(myPrefs.getInt("temp_carbs", Integer.parseInt(etxtCarbs.getText().toString())) + "");
            etxtProtein.setText(myPrefs.getInt("temp_protein", Integer.parseInt(etxtProtein.getText().toString())) + "");
            etxtFat.setText(myPrefs.getInt("temp_fat", Integer.parseInt(etxtFat.getText().toString())) + "");
        }
        updateGUI();
    }

    private void beginChainTourGuide() {
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("Set your goals")
                        .setDescription("It's time to set you daily goal intake")
                        .setGravity(Gravity.BOTTOM)
                        .setBackgroundColor(Color.parseColor("#c0392b"))
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .playLater(lblTitle);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("Calories vs Macros")
                        .setDescription("What would you like to focus on?")
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .playLater(seggroupDisplay);


        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("Don't worry")
                        .setDescription("If you don't know where to start, " +
                                "here's a recommended starting point")
                        .setGravity(Gravity.BOTTOM | Gravity.RIGHT)
                )
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                )
                .playLater(etxtCarbs);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();
        ChainTourGuide.init(this).playInSequence(sequence);
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
        return a;
    }

    private void getIntentData() {
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        dob = getIntent().getStringExtra("dob");
        gender = getIntent().getIntExtra("gender", 0);
        unitSystem = getIntent().getIntExtra("unitSystem", 0);
        weight = getIntent().getFloatExtra("weight", 0.0f);
        height1 = getIntent().getIntExtra("height1", 0);
        height2 = getIntent().getIntExtra("height2", 0);
        workoutFreq = getIntent().getIntExtra("workoutFreq", 0);
        goal = getIntent().getIntExtra("goal", 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void addTextWatchers() {
        if(rbtnCalories.isChecked()) // Calories
        {
            etxtCalories.addTextChangedListener(this);
        }
        etxtCarbs.addTextChangedListener(this);
        etxtProtein.addTextChangedListener(this);
        etxtFat.addTextChangedListener(this);
    }

    private void removeTextWatchers(){
        if(rbtnCalories.isChecked()) // Calories
        {
            etxtCalories.removeTextChangedListener(this);
        }
        etxtCarbs.removeTextChangedListener(this);
        etxtProtein.removeTextChangedListener(this);
        etxtFat.removeTextChangedListener(this);
    }
}
