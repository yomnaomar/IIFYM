package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.R;

import info.hoang8f.android.segmented.SegmentedGroup;

public class activityUserMacros extends AppCompatActivity {

    private SegmentedGroup seggroupDisplay;
    RadioButton rbtnCalories, rbtnMacros;
    EditText etxtCalories, etxtCarbs, etxtProtein, etxtFat;
    TextView lblUnitCarbs, lblUnitProtein, lblUnitFat, lblTotal, lblAmountTotal, lblPercentTotal, lblGramsCarbs, lblGramsProtein, lblGramsFat;
    Button btnFinish;

    private int calories, carbs, protein, fat, carbsPercent, proteinPercent, fatPercent, totalPercent;
    private boolean isRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_macros);
    }
}
