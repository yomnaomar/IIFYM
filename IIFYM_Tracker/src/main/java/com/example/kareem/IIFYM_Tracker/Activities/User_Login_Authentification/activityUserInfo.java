package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Activities.Main.activityMain;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Gender;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.UnitSystem;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.SegmentedGroup;
import com.example.kareem.IIFYM_Tracker.ViewComponents.fragmentDatePicker;

public class activityUserInfo extends AppCompatActivity implements View.OnClickListener {

    private String uid, email;
    private TextView lblDateChosen, lblWeightUnit, lblHeightUnit1, lblHeightUnit2;
    private EditText etxtName, etxtWeight, etxtHeightParam1, etxtHeightParam2;
    private LinearLayout linearlayoutHeight, linearlayoutWeight;
    private SegmentedGroup seggroupGender, seggroupUnitSystem;
    private RadioButton rbtnGenderMale, rbtnGenderFemale, rbtnMetric, rbtnImperial;
    private Spinner spinnerWorkoutFreq, spinnerGoals;
    private Button btnNext;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lblDateChosen:
                showDatePickerDialog();
            case R.id.btnNext:
                goToUserMacros();
                break;
        }
    }

    private void goToUserMacros() {
        String      name, dob;
        Gender      gender;
        UnitSystem  unitSystem;
        float       weight;
        int         height1, height2, workoutFreq, goal;

        name = etxtName.getText().toString();
        //TODO Implement Date input
        dob = lblDateChosen.getText().toString();

        if (rbtnGenderMale.isChecked()){
            gender = Gender.Male;
        }
        else {
            gender = Gender.Female;
        }

        if(rbtnMetric.isChecked()){
            unitSystem = UnitSystem.Metric;
            unitSystemChange(unitSystem);
            weight  = Float.parseFloat(etxtWeight.getText().toString());
            height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
            height2 = -1;
        }
        else {
            unitSystem = UnitSystem.Imperial;
            unitSystemChange(unitSystem);
            weight  = Float.parseFloat(etxtWeight.getText().toString());
            height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
            height2 = Integer.parseInt(etxtHeightParam2.getText().toString());
        }

        workoutFreq = spinnerWorkoutFreq.getSelectedItemPosition();
        goal = spinnerGoals.getSelectedItemPosition();

        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.putExtra("uid", uid);
        intent.putExtra("email", email);
        intent.setClass(context, activityMain.class);
        startActivity(intent);
        finish();
    }

    private void initializeGUI() {
        lblDateChosen       = (TextView)findViewById(R.id.lblDateChosen);
        lblWeightUnit       = (TextView)findViewById(R.id.lblWeightUnit);
        lblHeightUnit1      = (TextView)findViewById(R.id.lblHeightUnit1);
        lblHeightUnit2      = (TextView)findViewById(R.id.lblHeightUnit2);
        etxtName            = (EditText)findViewById(R.id.etxtName);
        etxtWeight          = (EditText)findViewById(R.id.etxtWeight);
        etxtHeightParam1    = (EditText)findViewById(R.id.etxtHeightParam1);
        etxtHeightParam2    = (EditText)findViewById(R.id.etxtHeightParam2);
        linearlayoutHeight  = (LinearLayout)findViewById(R.id.linearlayoutHeight);
        linearlayoutWeight  = (LinearLayout)findViewById(R.id.linearlayoutWeight);
        seggroupGender      = (SegmentedGroup)findViewById(R.id.seggroupGender);
        seggroupUnitSystem  = (SegmentedGroup)findViewById(R.id.seggroupUnitSystem);
        rbtnGenderMale      = (RadioButton)findViewById(R.id.rbtnGenderMale);
        rbtnGenderFemale    = (RadioButton)findViewById(R.id.rbtnGenderFemale);
        rbtnMetric          = (RadioButton)findViewById(R.id.rbtnMetric);
        rbtnImperial        = (RadioButton)findViewById(R.id.rbtnImperial);
        spinnerWorkoutFreq  = (Spinner)findViewById(R.id.spinnerWorkoutFreq);
        spinnerGoals        = (Spinner)findViewById(R.id.spinnerGoals);
        btnNext             = (Button)findViewById(R.id.btnNext);

        lblDateChosen.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapterWorkoutFreq = ArrayAdapter.createFromResource(this,
                R.array.array_Goals, android.R.layout.simple_spinner_item);
        adapterWorkoutFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterGoals = ArrayAdapter.createFromResource(this,
                R.array.array_WorkoutFreq, android.R.layout.simple_spinner_item);
        adapterGoals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerWorkoutFreq.setAdapter(adapterWorkoutFreq);
        spinnerGoals.setAdapter(adapterGoals);

        btnNext.setOnClickListener(this);
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new fragmentDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void unitSystemChange(UnitSystem unitsystem) {
        if(unitsystem == UnitSystem.Metric)
        {
            linearlayoutHeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            linearlayoutWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));

            etxtWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));

            etxtHeightParam1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblHeightUnit1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));

            etxtHeightParam2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
            lblHeightUnit2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));

            etxtHeightParam2.setVisibility(View.GONE);
            lblHeightUnit2.setVisibility(View.GONE);
        }
        else if(unitsystem == UnitSystem.Imperial)
        {
            linearlayoutHeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.67f));
            linearlayoutWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.33f));

            etxtWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));

            etxtHeightParam1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            lblHeightUnit1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));

            etxtHeightParam2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            lblHeightUnit2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));

            etxtHeightParam2.setVisibility(View.VISIBLE);
            lblHeightUnit2.setVisibility(View.VISIBLE);
        }
    }
}
