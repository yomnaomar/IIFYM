package com.example.kareem.IIFYM_Tracker.Activities.UserLoginAuthentification;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.DecimalDigitsInputFilter;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityUserInfo extends AppCompatActivity implements View.OnClickListener{

    // GUI
    private String              uid, email;
    private TextView            lblWeightUnit, lblHeightUnit1, lblHeightUnit2;
    private EditText            etxtName, etxtDateOfBirth, etxtWeight, etxtHeightParam1, etxtHeightParam2;
    private LinearLayout        linearlayoutHeight, linearlayoutWeight;
    private RadioButton         rbtnGenderMale, rbtnGenderFemale, rbtnMetric, rbtnGenderImperial;
    private SegmentedGroup      seggroupUnitSystem;
    private Spinner             spinnerWorkoutFreq, spinnerGoals;
    private Button              btnNext;
    private DatePickerDialog    datePickerDialog;
    private SimpleDateFormat    dateFormatter;

    // Variables
    private String      name, dob;
    private int         gender;
    private int         unitSystem;
    private float       weight;
    private int         height1, height2, workoutFreq, goal;
    private Context     context;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private FirebaseAuth            firebaseAuth;
    private BroadcastReceiver       broadcast_reciever;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Data from previous activity
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");

        // GUI
        initializeGUI();

        // Database
        context = getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void readUserInput() {
        name = etxtName.getText().toString();

        dob = etxtDateOfBirth.getText().toString();

        if (rbtnGenderMale.isChecked()) {
            gender = 0; // Male
        } else {
            gender = 1; // Female
        }

        // Metric
        if (rbtnMetric.isChecked()) {
            unitSystem = 0; // Metric
            if(!etxtWeight.getText().toString().isEmpty())
                weight = Float.parseFloat(etxtWeight.getText().toString());
            else
                weight = 0.0f;
            if(!etxtHeightParam1.getText().toString().isEmpty())
            height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
            else
            height1 = 0;
            height2 = 0;
        }

        // Imperial
        else {
            unitSystem = 0; // Imperial
            if(!etxtWeight.getText().toString().isEmpty())
                weight = Float.parseFloat(etxtWeight.getText().toString());
            else
                weight = 0.0f;
            if(!etxtHeightParam1.getText().toString().isEmpty())
                height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
            else
                height1 = 0;
            if(!etxtHeightParam2.getText().toString().isEmpty())
                height2 = Integer.parseInt(etxtHeightParam2.getText().toString());
            else
                height2 = 0;
        }

        workoutFreq = spinnerWorkoutFreq.getSelectedItemPosition();
        goal = spinnerGoals.getSelectedItemPosition();
    }

    private void goToUserMacros() {
        context = getApplicationContext();
        Intent intent = new Intent();
        intent.putExtra("uid", uid);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        intent.putExtra("dob", dob);
        intent.putExtra("gender", gender);
        intent.putExtra("unitSystem", unitSystem);
        intent.putExtra("weight", weight);
        intent.putExtra("height1", height1);
        intent.putExtra("height2", height2);
        intent.putExtra("workoutFreq", workoutFreq);
        intent.putExtra("goal", goal);
        intent.setClass(context, activityUserMacros.class);
        startActivity(intent);
    }

    private boolean validateFields() {
        boolean valid = true;

        String validate_name = etxtName.getText().toString();
        if (TextUtils.isEmpty(validate_name)) {
            etxtName.setError("Required");
            valid = false;
        } else
            etxtName.setError(null);

        if (!verifyAge()) {
            etxtDateOfBirth.setError("Invalid Date");
            valid = false;
        }
        else {
            etxtDateOfBirth.setError(null);
        }

        String validate_weight = etxtWeight.getText().toString();
        if (TextUtils.isEmpty(validate_weight)) {
            etxtWeight.setError("Required");
            valid = false;
        } else if ( // Weight KG boundaries (30-225)
                (Float.parseFloat(validate_weight) < 30 ||
                        Float.parseFloat(validate_weight) > 225) &&
                        rbtnMetric.isChecked()) {
            etxtWeight.setError("Invalid measurement");
            valid = false;
        } else if ( // Weight LB boundaries (50-500)
                (Float.parseFloat(validate_weight) < 50 ||
                        Float.parseFloat(validate_weight) > 500) &&
                        !rbtnMetric.isChecked()) {
            etxtWeight.setError("Invalid measurement");
            valid = false;
        } else
            etxtWeight.setError(null);

        String validate_height1 = etxtHeightParam1.getText().toString();
        if (TextUtils.isEmpty((validate_height1))) {
            etxtHeightParam1.setError("Required");
            valid = false;
        } else if (   // Height cm boundaries (50-250)
                (Integer.parseInt(validate_height1) < 50 ||
                        Integer.parseInt(validate_height1) > 250) &&
                        rbtnMetric.isChecked()) {
            etxtHeightParam1.setError("Invalid measurement");
            valid = false;
        } else if (   // Height feet boundaries (3-8)
                (Integer.parseInt(validate_height1) < 3 ||
                        Integer.parseInt(validate_height1) > 8) &&
                        !rbtnMetric.isChecked()) {
            etxtHeightParam1.setError("Invalid measurement");
            valid = false;
        } else
            etxtHeightParam1.setError(null);

        if (!rbtnMetric.isChecked()) {
            String validate_height2 = etxtHeightParam2.getText().toString();
            if (TextUtils.isEmpty((validate_height2))) {
                etxtHeightParam2.setError("Required");
                valid = false;
            } else if (Integer.parseInt(validate_height2) > 12) { // Inch boundary 0-12
                etxtHeightParam2.setError("Invalid measurement");
                valid = false;
            } else
                etxtHeightParam2.setError(null);
        }
        return valid;
    }

    private void initializeGUI() {
        etxtName            = (EditText)findViewById(R.id.etxtName);
        etxtDateOfBirth     = (EditText) findViewById(R.id.etxtDateOfBirth);
        etxtWeight          = (EditText)findViewById(R.id.etxtWeight);
        etxtHeightParam1    = (EditText)findViewById(R.id.etxtHeightParam1);
        etxtHeightParam2    = (EditText)findViewById(R.id.etxtHeightParam2);
        lblWeightUnit       = (TextView)findViewById(R.id.lblWeightUnit);
        lblHeightUnit1      = (TextView)findViewById(R.id.lblHeightUnit1);
        lblHeightUnit2      = (TextView)findViewById(R.id.lblHeightUnit2);
        linearlayoutHeight  = (LinearLayout)findViewById(R.id.linearlayoutHeight);
        linearlayoutWeight  = (LinearLayout)findViewById(R.id.linearlayoutWeight);
        seggroupUnitSystem  = (SegmentedGroup) findViewById(R.id.seggroupUnitSystem);
        rbtnGenderMale      = (RadioButton)findViewById(R.id.rbtnGenderMale);
        rbtnGenderFemale    = (RadioButton)findViewById(R.id.rbtnGenderFemale);
        rbtnMetric          = (RadioButton)findViewById(R.id.rbtnMetric);
        rbtnGenderImperial  = (RadioButton)findViewById(R.id.rbtnImperial);
        spinnerWorkoutFreq  = (Spinner)findViewById(R.id.spinnerWorkoutFreq);
        spinnerGoals        = (Spinner)findViewById(R.id.spinnerGoals);
        btnNext             = (Button)findViewById(R.id.btnNext);

        etxtDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    datePickerDialog.show();
                }
            }
        });
        etxtDateOfBirth.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        etxtDateOfBirth.setText(dateFormatter.format(new Date()));
        etxtDateOfBirth.setOnClickListener(this);
        setDateTimeField();

        etxtWeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});

        seggroupUnitSystem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                unitSystemChange();
            }
        });

        ArrayAdapter<CharSequence> adapterWorkoutFreq = ArrayAdapter.createFromResource(this,
                R.array.array_WorkoutFreq, R.layout.spinner_item);
        adapterWorkoutFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterGoals = ArrayAdapter.createFromResource(this,
                R.array.array_Goals, R.layout.spinner_item);
        adapterGoals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerWorkoutFreq.setAdapter(adapterWorkoutFreq);
        spinnerGoals.setAdapter(adapterGoals);

        btnNext.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etxtDateOfBirth:
                datePickerDialog.show();
                break;
            case R.id.btnNext:
                if(validateFields()) {
                    readUserInput();
                    goToUserMacros();
                }
                break;
        }
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etxtDateOfBirth.setText(dateFormatter.format(newDate.getTime()));
                if(etxtDateOfBirth.getError() != null) {
                    etxtDateOfBirth.setError(null);
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void unitSystemChange() {
        if(rbtnMetric.isChecked()) // Metric
        {
            linearlayoutHeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            linearlayoutWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));

            etxtWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            lblWeightUnit.setText("kg");

            etxtHeightParam1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblHeightUnit1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            lblHeightUnit1.setText("cm");

            etxtHeightParam2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
            lblHeightUnit2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));

            etxtHeightParam2.setVisibility(View.GONE);
            lblHeightUnit2.setVisibility(View.GONE);
        }
        else // Imperial
        {
            linearlayoutHeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.67f));
            linearlayoutWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.33f));

            etxtWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            lblWeightUnit.setText("lb");

            etxtHeightParam1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            lblHeightUnit1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
            lblHeightUnit1.setText("ft");

            etxtHeightParam2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            lblHeightUnit2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));

            etxtHeightParam2.setVisibility(View.VISIBLE);
            lblHeightUnit2.setVisibility(View.VISIBLE);
        }
    }

    @Override protected void onPause() {
        readUserInput();
        unregisterReceiver(broadcast_reciever);
        myPrefs.addPreference("temp_name_registration",name);
        myPrefs.addPreference("temp_dob_registration",dob);
        if(rbtnGenderMale.isChecked())
            myPrefs.addPreference("temp_gender_registration",0); // Male
        else
            myPrefs.addPreference("temp_gender_registration",1); // Female
        if(rbtnMetric.isChecked())
            myPrefs.addPreference("temp_unitsystem_registration",0); // Metric
        else
            myPrefs.addPreference("temp_unitsystem_registration",1); // Imperial
        myPrefs.addPreference("temp_weight_registration",weight);
        myPrefs.addPreference("temp_height1_registration",height1);
        myPrefs.addPreference("temp_height2_registration",height2);
        myPrefs.addPreference("temp_workoutfreq_registration",workoutFreq);
        myPrefs.addPreference("temp_goal_registration",goal);

        signOut();
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity"))
                    finish();
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));
        etxtName.setText(myPrefs.getStringValue("temp_name_registration"));
        Calendar newDate = Calendar.getInstance();
        etxtDateOfBirth.setText(myPrefs.getStringValue("temp_dob_registration", dateFormatter.format(newDate.getTime())));
        if(myPrefs.getIntValue("temp_gender_registration") == 0) // Male
            rbtnGenderMale.setChecked(true);
        else
            rbtnGenderFemale.setChecked(true); // Female
        if(myPrefs.getIntValue("temp_unitsystem_registration") == 0) // Metric
            rbtnMetric.setChecked(true);
        else
            rbtnGenderImperial.setChecked(true); // Imperial

        if(myPrefs.getFloatValue("temp_weight_registration") != 0)
            etxtWeight.setText(myPrefs.getFloatValue("temp_weight_registration") + "");
        else
            etxtWeight.setText("");

        if(myPrefs.getIntValue("temp_height1_registration") != 0)
            etxtHeightParam1.setText(myPrefs.getIntValue("temp_height1_registration") + "");
        else
            etxtHeightParam1.setText("");

        if(myPrefs.getIntValue("temp_height2_registration") != 0)
            etxtHeightParam2.setText(myPrefs.getIntValue("temp_height2_registration") + "");
        else
            etxtHeightParam2.setText("");

        spinnerWorkoutFreq.setSelection(myPrefs.getIntValue("temp_workoutfreq_registration"));
        spinnerGoals.setSelection(myPrefs.getIntValue("temp_goal_registration"));
    }

    private boolean verifyAge(){
        String ageArr[] = etxtDateOfBirth.getText().toString().split("-");
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            return false;
        return true;
    }

    private void signOut() {
        Log.d("UserInfo","Signed out");
        firebaseAuth.signOut();
    }
}
