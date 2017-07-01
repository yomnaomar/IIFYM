package com.karimchehab.IIFYM.Activities.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.karimchehab.IIFYM.Activities.Application.ActivityHome;
import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.Models.User;
import com.karimchehab.IIFYM.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class ActivityRegisterGoals extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // GUI
    private SegmentedGroup  seggroupDisplay;
    private RadioButton     rbtnCalories, rbtnMacros;
    private EditText        etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    private TextView        lblPlus1, lblPlus2, lblUnit1, lblUnit2, lblUnit3, lblEqual, lblCarbs, lblProtein, lblFat, lblTotal, lblAmountTotal, lblValueCarbs, lblValueProtein, lblValueFat;
    private ImageButton     btnReset, btnInfo;
    private Animation       mEnterAnimation, mExitAnimation;
    private ProgressDialog  progressDialog;
    private Menu            menu;

    // Variables
    // Final Variables (Cannot be changed)
    private int             gender, unitSystem, height1, height2, workoutFreq, goal ,BMR;
    private float           weight;
    private String          uid, email, name, dob;
    private int             caloriesDefault;
    private final int       carbsPercentDefault = 50, proteinPercentDefault = 25, fatPercentDefault = 25;
    private Context         context;
    private ChainTourGuide  tourguide;

    // Dynamic Variables (Can be changed)
    private int             totalPercent, calories, carbs, protein, fat, carbsPercent, proteinPercent, fatPercent;
    private int             carbsOld, proteinOld, fatOld;
    private boolean         carbsChanged, proteinChanged, fatChanged;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;
    private FirebaseAuth            firebaseAuth;
    private DatabaseReference       firebaseDbRef;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_macros);

        // Database
        context = getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDbRef = FirebaseDatabase.getInstance().getReference();
        DB_SQLite = new SQLiteConnector(context);

        // Data from previous Login activities
        getIntentData();

        // Calculate values to be displayed
        getBMR();

        // GUI
        initializeGUI();

        // Set and display default values
        defaultValues();

        // UI guide
        beginChainTourGuide();
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

    private void getBMR() {
        // MALE:   BMR = (10 x weight in kg) + (6.25 x height in cm) – (5 x age in years) + 5
        // FEMALE: BMR = (10 x weight in kg) + (6.25 x height in cm)  – (5 x age in years) - 161
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

        // Activity Factor Multiplier
        // None (little or no exercise)
        if (workoutFreq == 0)
            caloriesDefault = (int) (BMR * 1.2);

            // Low (1-3 times/week)
        else if (workoutFreq == 1)
            caloriesDefault = (int) (BMR * 1.35);

            // Medium (3-5 days/week)
        else if (workoutFreq == 2)
            caloriesDefault = (int) (BMR * 1.5);

            // High (6-7 days/week)
        else if (workoutFreq == 3)
            caloriesDefault = (int) (BMR * 1.7);

            // Very High (physical job or 7+ times/week)
        else if (workoutFreq == 4)
            caloriesDefault = (int) (BMR * 1.9);

        // Weight goals
        // Lose weight
        if (goal == 0)
            caloriesDefault -= 250.0;

            // Maintain weight
        else if (goal == 1)
        {} // Do nothing

        // Gain weight
        else if (goal == 2)
            caloriesDefault += 250.0;
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

    private void initializeGUI() {
        seggroupDisplay = (SegmentedGroup) findViewById(R.id.seggroupDisplay);
        rbtnCalories    = (RadioButton) findViewById(R.id.rbtnCalories);
        rbtnMacros      = (RadioButton) findViewById(R.id.rbtnMacros);
        etxtCalories    = (EditText) findViewById(R.id.etxtCalories);
        etxtCarbs       = (EditText) findViewById(R.id.etxtCarbs);
        etxtProtein     = (EditText) findViewById(R.id.etxtProtein);
        etxtFat         = (EditText) findViewById(R.id.etxtFat);
        lblPlus1        = (TextView) findViewById(R.id.lblPlus1);
        lblPlus2        = (TextView) findViewById(R.id.lblPlus2);
        lblUnit1        = (TextView) findViewById(R.id.lblUnit1);
        lblUnit2        = (TextView) findViewById(R.id.lblUnit2);
        lblUnit3        = (TextView) findViewById(R.id.lblUnit3);
        lblEqual        = (TextView) findViewById(R.id.lblEqual);
        lblCarbs        = (TextView) findViewById(R.id.lblCarbs);
        lblProtein      = (TextView) findViewById(R.id.lblProtein);
        lblFat          = (TextView) findViewById(R.id.lblFat);
        lblTotal        = (TextView) findViewById(R.id.lblTotal);
        lblAmountTotal  = (TextView) findViewById(R.id.lblAmountTotal);
        lblValueCarbs   = (TextView) findViewById(R.id.lblValueCarbs);
        lblValueProtein = (TextView) findViewById(R.id.lblValueProtein);
        lblValueFat     = (TextView) findViewById(R.id.lblValueFat);
        btnReset        = (ImageButton) findViewById(R.id.btnReset);
        btnInfo         = (ImageButton) findViewById(R.id.btnInfo);

        seggroupDisplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateGUI();
                defaultValues();
            }});

        addTextWatchers();

        btnReset.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

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

            lblCarbs.setText("Carbs");
            lblProtein.setText("Protein");
            lblFat.setText("Fat");

            lblUnit1.setText("%");
            lblUnit2.setText("%");
            lblUnit3.setText("%");

            lblPlus1.setVisibility(View.VISIBLE);
            lblPlus2.setVisibility(View.VISIBLE);
            lblEqual.setVisibility(View.VISIBLE);
            lblTotal.setVisibility(View.VISIBLE);
            lblAmountTotal.setVisibility(View.VISIBLE);
        }
        else
        {
            etxtCalories.setEnabled(false);

            lblCarbs.setText("Carbs");
            lblProtein.setText("Protein");
            lblFat.setText("Fat");

            lblUnit1.setText("g");
            lblUnit2.setText("g");
            lblUnit3.setText("g");

            lblPlus1.setVisibility(View.GONE);
            lblPlus2.setVisibility(View.GONE);
            lblEqual.setVisibility(View.GONE);
            lblTotal.setVisibility(View.GONE);
            lblAmountTotal.setVisibility(View.GONE);
        }
    }

    private void defaultValues() {
        if (rbtnCalories.isChecked()) {
            // Macronutrient ratio default
            calories = caloriesDefault;
            carbsPercent = carbsPercentDefault;
            proteinPercent = proteinPercentDefault;
            fatPercent = fatPercentDefault;
            totalPercent = carbsPercent + proteinPercent + fatPercent;

            removeTextWatchers();

            etxtCalories.setText(calories + "");
            etxtCarbs.setText(carbsPercent + "");
            etxtProtein.setText(proteinPercent + "");
            etxtFat.setText(fatPercent + "");

            lblValueCarbs.setText("~" + Math.round(carbsPercent * 0.01f * calories / 4) + " g");
            lblValueProtein.setText("~" + Math.round(proteinPercent * 0.01f * calories / 4) + " g");
            lblValueFat.setText("~" + Math.round(fatPercent * 0.01f * calories / 9) + " g");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
            else
                lblAmountTotal.setTextColor(Color.parseColor("#FF0000")); // error_red

            addTextWatchers();
        }
        else {
            calories = caloriesDefault;
            carbs = Math.round((carbsPercentDefault * 0.01f * calories) / 4);
            protein = Math.round((proteinPercentDefault * 0.01f * calories) / 4);
            fat = Math.round((fatPercentDefault * 0.01f * calories) / 9);

            removeTextWatchers();

            etxtCalories.setText(calories + "");
            etxtCarbs.setText(carbs + "");
            etxtProtein.setText(protein + "");
            etxtFat.setText(fat + "");

            lblValueCarbs.setText("~" + Math.round(carbs * 4 * 100 / calories) + " %");
            lblValueProtein.setText("~" + Math.round(protein * 4 * 100 / calories)  + " %");
            lblValueFat.setText("~" + Math.round(fat * 9 * 100 / calories) + " %");

            addTextWatchers();
        }

        if ((carbsPercent + proteinPercent + fatPercent) != 100) {
            lblAmountTotal.setError("Must equal 100");
        } else {
            lblAmountTotal.setError(null);
        }
        Toast.makeText(context, R.string.lblShowingRecommended, Toast.LENGTH_SHORT).show();
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        captureOldValues();
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        captureNewValues();
        compareValues();
        updateValues();
        validateFields();
    }

    private void captureOldValues() {
        if(rbtnCalories.isChecked()) {
            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbsOld = 0;
            else
                carbsOld = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                proteinOld = 0;
            else
                proteinOld = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fatOld = 0;
            else
                fatOld = Integer.parseInt(fatP);
            totalPercent = carbsPercent + proteinPercent + fatPercent;
        }
        else {
            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbsOld = 0;
            else
                carbsOld = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                proteinOld = 0;
            else
                proteinOld = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fatOld = 0;
            else
                fatOld = Integer.parseInt(fatP);

            calories = Math.round(carbs*4 + protein*4 + fat*9);
        }
    }

    private void captureNewValues(){
        if(rbtnCalories.isChecked()) {
            String currentCals = etxtCalories.getText().toString();
            if (currentCals.isEmpty())
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
        }
        else {
            String carbP = etxtCarbs.getText().toString();
            if (carbP.isEmpty())
                carbs = 0;
            else
                carbs = Integer.parseInt(carbP);

            String proteinP = etxtProtein.getText().toString();
            if (proteinP.isEmpty())
                protein = 0;
            else
                protein = Integer.parseInt(proteinP);

            String fatP = etxtFat.getText().toString();
            if (fatP.isEmpty())
                fat = 0;
            else
                fat = Integer.parseInt(fatP);

            calories = Math.round(carbs*4 + protein*4 + fat*9);
        }
    }

    private void compareValues() {
        if (carbsOld != carbsPercent)
            carbsChanged = true;
        else
            carbsChanged = false;
        if (proteinOld != proteinPercent)
            proteinChanged = true;
        else
            proteinChanged = false;
        if (fatOld != fatPercent)
            fatChanged = true;
        else
            fatChanged = false;
    }

    private void updateValues(){
        if(rbtnCalories.isChecked()) // Calories
        {
            removeTextWatchers();

            if (!carbsChanged)
                etxtCarbs.setText(carbsPercent + "");
            if (!proteinChanged)
                etxtProtein.setText(proteinPercent + "");
            if (!fatChanged)
                etxtFat.setText(fatPercent + "");

            lblValueCarbs.setText("~" + Math.round(carbsPercent * 0.01f * calories/4) + " g");
            lblValueProtein.setText("~" + Math.round(proteinPercent * 0.01f * calories/4) + " g");
            lblValueFat.setText("~" + Math.round(fatPercent * 0.01f * calories/9) + " g");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
            else
                lblAmountTotal.setTextColor(Color.parseColor("#FF0000")); // error_red

            addTextWatchers();
        }
        else if (rbtnMacros.isChecked()) // Macros
        {
            removeTextWatchers();
            etxtCalories.setText((calories) + "");
            if(calories != 0) {
                lblValueCarbs.setText("~" + Math.round(carbs * 4 * 100 / calories) + " %");
                lblValueProtein.setText("~" + Math.round(protein * 4 * 100 / calories) + " %");
                lblValueFat.setText("~" + Math.round(fat * 9 * 100 / calories) + " %");
            }
            else {
                lblValueCarbs.setText("~0 %");
                lblValueProtein.setText("~0 %");
                lblValueFat.setText("~0 %");
            }
            addTextWatchers();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_macros, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_user_macros:
                Finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void Finish(){
        // Validate Fields
        if(validateFields()){
            final User user;
            if (rbtnCalories.isChecked()) {
                user = new User(uid, email, true, name, dob, gender,
                        unitSystem, weight, height1, height2, workoutFreq,
                        goal, calories, true, carbsPercent, proteinPercent, fatPercent);
            }
            else {
                user = new User(uid, email, true, name, dob, gender,
                        unitSystem, weight, height1, height2, workoutFreq,
                        goal, calories, false, carbs, protein, fat);
            }

            showProgressDialog();
            Log.i("RegisterUser","adding user data: " + user.toString());
            Log.i("RegisterUser", user + "");
            firebaseDbRef.child("users").child(uid).setValue(user, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    Log.d("RegisterUser","Value was set. Error = " + error);

                    // No error
                    if (error == null) {
                        DB_SQLite.createUser(user);

                        hideProgressDialog();

                        // Go to ActivityHome
                        // Store user session in Preferences
                        myPrefs.addPreference("session_uid", uid);

                        Context context = getApplicationContext();
                        Intent intent = new Intent();
                        intent.setClass(context, ActivityHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    // Error writing to database
                    else {
                        showAlertDialog("Network Error","Please check your network connection and try again");
                    }
                }
            });
        }
    }

    @Override public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnInfo:
                beginChainTourGuide();
                break;
            case R.id.btnReset:
                defaultValues();
                break;
        }
    }

    private void beginChainTourGuide() {
        Overlay overlay = new Overlay()
                .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                .setEnterAnimation(mEnterAnimation)
                .setExitAnimation(mExitAnimation);

/*        Overlay overlayLast = new Overlay()
                .setBackgroundColor(Color.parseColor("#EE2c3e50"))
                .setEnterAnimation(mEnterAnimation)
                .setExitAnimation(mExitAnimation)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MenuInflater inflater = getMenuInflater();
                        inflater.inflate(R.menu.menu_user_macros, menu);
                        tourguide.cleanUp();
                    }
                });*/

        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("Calories vs Macros")
                        .setDescription("What would you like to focus on?")
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(overlay)
                .playLater(seggroupDisplay);


        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("Don't worry")
                        .setDescription("If you don't know where to start, " +
                                "here's a recommended starting point")
                        .setGravity(Gravity.BOTTOM | Gravity.RIGHT)
                )
                .setOverlay(overlay)
                .playLater(etxtCarbs);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("Reset")
                        .setDescription("Press the reset button to show recommended goals")
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(overlay)
                .playLater(btnReset);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        tourguide = ChainTourGuide.init(this).playInSequence(sequence);
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
        etxtCalories.removeTextChangedListener(this);
        etxtCarbs.removeTextChangedListener(this);
        etxtProtein.removeTextChangedListener(this);
        etxtFat.removeTextChangedListener(this);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        if(rbtnCalories.isChecked()) {
            if ((carbsPercent + proteinPercent + fatPercent) != 100) {
                lblAmountTotal.setError("Must equal 100");
                valid = false;
            } else {
                lblAmountTotal.setError(null);
            }
        }
        return valid;
    }

    @Override protected void onPause() {
        if(rbtnCalories.isChecked())
            myPrefs.addPreference("temp_display_registration", false); // Calories
        else
            myPrefs.addPreference("temp_display_registration", true); // Macros

        tourguide.cleanUp();
        signOut();
        super.onPause();
    }

    @Override protected void onResume() {
        // Load preferences
        // If preferences unavailable, set defaults
        super.onResume();
        etxtCalories.setText(caloriesDefault + "");

        if(myPrefs.getBooleanValue("temp_display_registration")) { // Macros
            rbtnMacros.setChecked(true);

            etxtCarbs.setText(carbs + "");
            etxtProtein.setText(protein + "");
            etxtFat.setText(fat + "");
        }
        else { // Calories
            rbtnCalories.setChecked(true);

            etxtCarbs.setText(carbsPercent + "");
            etxtProtein.setText(proteinPercent + "");
            etxtFat.setText(fatPercent + "");

            lblValueCarbs.setText("~" + Math.round(carbsPercent * 0.01f * caloriesDefault/4) + " g");
            lblValueProtein.setText("~" + Math.round(proteinPercent * 0.01f * caloriesDefault/4) + " g");
            lblValueFat.setText("~" + Math.round(fatPercent * 0.01f * caloriesDefault/9) + " g");
            lblAmountTotal.setText(totalPercent + "");
            if (totalPercent == 100)
                lblAmountTotal.setTextColor(Color.parseColor("#2E7D32")); // Green
            else
                lblAmountTotal.setTextColor(Color.parseColor("#FF0000")); // error_red
        }
        updateGUI();
    }

    private void signOut() {
        Log.d("UserInfo","Signed out");
        firebaseAuth.signOut();
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegisterGoals.this);
        builder.setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override public void afterTextChanged(Editable s) {

    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        signOut();
    }
}
