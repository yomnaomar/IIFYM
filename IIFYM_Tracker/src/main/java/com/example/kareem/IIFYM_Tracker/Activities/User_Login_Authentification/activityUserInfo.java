package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
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

import com.example.kareem.IIFYM_Tracker.Custom_Objects.Gender;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.UnitSystem;
import com.example.kareem.IIFYM_Tracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityUserInfo extends AppCompatActivity implements View.OnClickListener{

    private String uid, email;
    private TextView lblWeightUnit, lblHeightUnit1, lblHeightUnit2;
    private EditText etxtName, etxtDateOfBirth, etxtWeight, etxtHeightParam1, etxtHeightParam2;
    private LinearLayout linearlayoutHeight, linearlayoutWeight;
    private RadioButton rbtnGenderMale, rbtnMetric;
    private SegmentedGroup seggroupUnitSystem;
    private Spinner spinnerWorkoutFreq, spinnerGoals;
    private Button btnNext;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    String      name, dob;
    Gender      gender;
    UnitSystem  unitSystem;
    float       weight;
    int         height1, height2, workoutFreq, goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //UID
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");

        //GUI
        initializeGUI();
    }

    private void readUserInput() {
        if(validateFields()){
            name = etxtName.getText().toString();

            dob = etxtDateOfBirth.getText().toString();

            if (rbtnGenderMale.isChecked()) {
                gender = Gender.Male;
            } else {
                gender = Gender.Female;
            }

            if (rbtnMetric.isChecked()) {
                unitSystem = UnitSystem.Metric;
                weight = Float.parseFloat(etxtWeight.getText().toString());
                height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
                height2 = -1;
            } else {
                unitSystem = UnitSystem.Imperial;
                weight = Float.parseFloat(etxtWeight.getText().toString());
                height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
                height2 = Integer.parseInt(etxtHeightParam2.getText().toString());
            }

            workoutFreq = spinnerWorkoutFreq.getSelectedItemPosition();
            goal = spinnerGoals.getSelectedItemPosition();

            Context context = getApplicationContext();
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
            finish();
        }
    }

    private boolean validateFields() {
        boolean valid = true;

        String validate_name = etxtName.getText().toString();
        if (TextUtils.isEmpty(validate_name)) {
            etxtName.setError("Required.");
            valid = false;
        } else {
            etxtName.setError(null);
        }

        String validate_dob = etxtDateOfBirth.getText().toString();
        if (validate_dob.contains("")) {
            etxtName.setError("Required.");
            valid = false;
        } else {
            etxtName.setError(null);
        }

        String validate_weight = etxtWeight.getText().toString();
        if (TextUtils.isEmpty(validate_weight)) {
            etxtWeight.setError("Required.");
            valid = false;
        } else {
            etxtWeight.setError(null);
        }

        String validate_height1 = etxtHeightParam1.getText().toString();
        if (TextUtils.isEmpty((validate_height1))){
            etxtHeightParam1.setError("Required.");
            valid = false;
        } else {
            etxtHeightParam1.setError(null);
        }

        if(!rbtnMetric.isChecked()){
            String validate_height2 = etxtHeightParam2.getText().toString();
            if (TextUtils.isEmpty((validate_height2))){
                etxtHeightParam2.setError("Required.");
                valid = false;
            } else {
                etxtHeightParam2.setError(null);
            }
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
        rbtnMetric          = (RadioButton)findViewById(R.id.rbtnMetric);
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

        setDateTimeField();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etxtDateOfBirth:
                datePickerDialog.show();
                break;
            case R.id.btnNext:
                readUserInput();
                break;
        }
    }

    private void setDateTimeField() {
        etxtDateOfBirth.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etxtDateOfBirth.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void unitSystemChange() {
        if(rbtnMetric.isChecked())
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
        else
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
}
