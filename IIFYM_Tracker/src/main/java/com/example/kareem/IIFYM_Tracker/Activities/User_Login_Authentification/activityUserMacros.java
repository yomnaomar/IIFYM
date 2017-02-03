package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.kareem.IIFYM_Tracker.Custom_Objects.Gender;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.UnitSystem;
import com.example.kareem.IIFYM_Tracker.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class activityUserMacros extends AppCompatActivity implements View.OnClickListener, TextWatcher {
//delete this
    // UI Elements
    private SegmentedGroup  seggroupDisplay;
    private RadioButton     rbtnCalories, rbtnMacros;
    private EditText        etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    private TextView        lblTitle, lblUnitCarbs, lblUnitProtein, lblUnitFat, lblTotal, lblAmountTotal, lblPercentTotal, lblGramsCarbs, lblGramsProtein, lblGramsFat;
    private Button          btnFinish;
    private ImageButton     btnReset;

    // Static Variables
    private int             BMR, height1, height2, workoutFreq, goal ,caloriesInitial;
    private float           weight;
    private String          uid, email, name, dob;
    private Gender          gender;
    private UnitSystem      unitSystem;

    // Dyanamic Variables
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
        myPrefs = getPreferences(AppCompatActivity.MODE_PRIVATE);

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

        // Calculate values to be displayed
        getBMR();

        // GUI
        initializeGUI();

        // Set Defaults
        DefaultValues();

        // Initialize Prefs to avoid onResume Override
        InitializePrefs();

        //UI guide
        showtipOverlay();
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

            }});

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

    private void UpdateGUI()
    {
        if(rbtnCalories.isChecked())
        {
            // Update GUI
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
            // Update GUI
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
        String ageArr[] = dob.split("-");

        // Obtain Age value from Date of Birth String
        int valAge = getAge(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));

        // Obtain Gender
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
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnReset:
                DefaultValues();
                displayRbtnChange();
                break;
            case R.id.btnFinish:
                isRegistered = true;
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
            editor.putInt("temp_calories",0);
        else
            editor.putInt("temp_calories", Integer.parseInt(etxtCalories.getText().toString()));
        if(etxtCalories.getText().toString().isEmpty())
            editor.putInt("temp_carbs",0);
        else
            editor.putInt("temp_carbs", Integer.parseInt(etxtCarbs.getText().toString()));
        if(etxtCalories.getText().toString().isEmpty())
            editor.putInt("temp_protein",0);
        else
            editor.putInt("temp_protein", Integer.parseInt(etxtProtein.getText().toString()));
        if(etxtFat.getText().toString().isEmpty())
            editor.putInt("temp_fat",0);
        else
            editor.putInt("temp_fat", Integer.parseInt(etxtFat.getText().toString()));

        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Load preferences
        // If preferences unavailable, set defaults
        etxtCalories.setText(myPrefs.getInt("temp_calories", caloriesInitial) + "");

        if(myPrefs.getInt("temp_display",0) == 0) { // Calories
            rbtnCalories.setChecked(true);
            etxtCarbs.setText(myPrefs.getInt("temp_carbs", 50)  + "");
            etxtProtein.setText(myPrefs.getInt("temp_protein", 25)  + "");
            etxtFat.setText(myPrefs.getInt("temp_fat", 25)  + "");

            lblAmountTotal.setText(myPrefs.getInt("temp_total", 100)  + "");
            lblGramsCarbs.setText(myPrefs.getString("temp_gramsCarbs", "(" + Math.round(carbsPercent*0.01f*caloriesInitial/4) + " g)"));
            lblGramsProtein.setText(myPrefs.getString("temp_gramsProtein", "(" + Math.round(proteinPercent*0.01f*caloriesInitial/4) + " g)"));
            lblGramsFat.setText(myPrefs.getString("temp_gramsFat","(" + Math.round(fatPercent*0.01f*caloriesInitial/9) + " g)"));
        }
        else { // Macros
            rbtnMacros.setChecked(true);
            etxtCarbs.setText(myPrefs.getInt("temp_carbs", 0) + "");
            etxtProtein.setText(myPrefs.getInt("temp_protein", 0) + "");
            etxtFat.setText(myPrefs.getInt("temp_fat", 0) + "");
        }

        UpdateGUI();
        super.onResume();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void showtipOverlay() {
        if (myPrefs.getBoolean("isnewUser", true)) {
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
                    .playLater(etxtCalories);

            Sequence sequence = new Sequence.SequenceBuilder()
                    .add(tourGuide1, tourGuide2, tourGuide3)
                    .setDefaultOverlay(new Overlay()
                            .setEnterAnimation(mEnterAnimation)
                            .setExitAnimation(mExitAnimation)
                    )
                    .setDefaultPointer(null)
                    .setContinueMethod(Sequence.ContinueMethod.Overlay)
                    .build();

            ChainTourGuide.init(this).playInSequence(sequence);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isnewUser", true);
            editor.commit();
        }
    }

    private void DefaultValues() {
        // Macronutrient ratio default
        calories = caloriesInitial;
        carbsPercent = 50;
        proteinPercent = 25;
        fatPercent = 25;
        totalPercent = 100;

        // Calculate Macro values from ratios
        carbs = Math.round(carbsPercent*0.01f*caloriesInitial/4);
        protein =  Math.round(proteinPercent*0.01f*caloriesInitial/4);
        fat =  Math.round(fatPercent*0.01f*caloriesInitial/9);

        rbtnCalories.setChecked(true);
        etxtCalories.setText(caloriesInitial + "");
        etxtCarbs.setText(carbsPercent + "");
        etxtProtein.setText(proteinPercent + "");
        etxtFat.setText(fatPercent + "");
        lblAmountTotal.setText(totalPercent + "");
        lblAmountTotal.setTextColor(Color.parseColor("#2E7D32"));
        lblGramsCarbs.setText("(" + Math.round(50 * 0.01f * caloriesInitial/4) + " g)");
        lblGramsProtein.setText("(" + Math.round(25 * 0.01f * caloriesInitial/4) + " g)");
        lblGramsFat.setText("(" + Math.round(25 * 0.01f * caloriesInitial/9) + " g)");
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

    private void InitializePrefs() {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putInt("temp_display", 0); // Calories
        editor.putInt("temp_gramsCarbs", carbs);
        editor.putInt("temp_gramsProtein", protein);
        editor.putInt("temp_gramsFat", fat);
        editor.putInt("temp_calories", caloriesInitial);
        editor.putInt("temp_carbs", carbsPercent);
        editor.putInt("temp_protein", proteinPercent);
        editor.putInt("temp_fat", fatPercent);
        editor.putInt("temp_total", carbsPercent + proteinPercent + fatPercent);
        editor.commit();
    }
}
