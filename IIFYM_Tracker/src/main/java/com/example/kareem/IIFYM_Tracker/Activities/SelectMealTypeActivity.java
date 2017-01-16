package com.example.kareem.IIFYM_Tracker.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.kareem.IIFYM_Tracker.R;

public class SelectMealTypeActivity extends AppCompatActivity implements View.OnClickListener {

    Button Button_Complex, Button_Simple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button_Complex = (Button)findViewById(R.id.Button_Complex);
        Button_Simple = (Button)findViewById(R.id.Button_Simple);

        Button_Complex.setOnClickListener(this);
        Button_Simple.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Button_Complex:
                GoToComplexActivity();
                break;
            case R.id.Button_Simple:
                GoToSimpleActivity();
                break;
        }
    }

    private void GoToSimpleActivity() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,CreateSimpleMealActivity.class);
        startActivity(intent);
    }

    private void GoToComplexActivity() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        intent.setClass(context,CreateComplexMealActivity.class);
        startActivity(intent);
    }
}
