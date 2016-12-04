package com.example.kareem.macrotracker.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.kareem.macrotracker.Custom_Objects.User;
import com.example.kareem.macrotracker.Database.DatabaseConnector;
import com.example.kareem.macrotracker.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class MacroSettings extends AppCompatActivity {

    String user_name;
    DatabaseConnector My_DB;
    User currentUser;

    EditText goal,gender,carbs,prot,fat,workoutfreq,weight,height,calories,bmr,age;
    View parentLayout;


    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
    private boolean hasCenterText1 = true;
    private boolean hasCenterText2 = true;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_settings);

        parentLayout = findViewById(R.id.activity_macro_settings);

        My_DB = new DatabaseConnector(getApplicationContext());

        goal = (EditText)findViewById(R.id.txt_goal);
        gender = (EditText)findViewById(R.id.txt_gender);
        carbs = (EditText)findViewById(R.id.txt_carbs);
        prot = (EditText)findViewById(R.id.txt_protein);
        fat =(EditText)findViewById(R.id.txt_fat);
        workoutfreq = (EditText)findViewById(R.id.txt_workoutfreq);
        weight =(EditText)findViewById(R.id.txt_weight);
        height = (EditText)findViewById(R.id.txt_height);
        calories = (EditText)findViewById(R.id.txt_cal);
        bmr = (EditText)findViewById(R.id.txt_BMR);
        age = (EditText)findViewById(R.id.txt_age);


        chart = (PieChartView)findViewById(R.id.macro_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_name = settings.getString("user_name", "");
        currentUser = My_DB.getUserObject(user_name);

        generateData();
        settextData();



    }
    //TODO: (Abdulwahab) modify the pie information that appears when user clicks on chart segments
    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            switch(arcIndex)
            {
                case 0://P
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Protein: " + value.getValue() +"%", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    break;
                case 1://C
                    Snackbar snackbar2 = Snackbar
                            .make(parentLayout, "Carbs: " +  value.getValue()+"%", Snackbar.LENGTH_SHORT);
                    snackbar2.show();
                    break;

                case 2://F
                    Snackbar snackbar3 = Snackbar
                            .make(parentLayout, "Fat: " +  value.getValue()+"%", Snackbar.LENGTH_SHORT);
                    snackbar3.show();

                    break;
            }

        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
    private void prepareDataAnimation() {
        for (SliceValue value : data.getValues()) {
            value.setTarget((float) Math.random() * 30 + 15);
        }
    }
    private void explodeChart() {
        isExploded = !isExploded;
        generateData();

    }
    private void generateData() {

        List<SliceValue> values = new ArrayList<>();

        SliceValue sliceValue_prot = new SliceValue((float)currentUser.getPercent_protein(), Color.parseColor("#FF4081"));
        sliceValue_prot.setLabel(String.valueOf(currentUser.getPercent_protein()));
        values.add(sliceValue_prot);
        SliceValue sliceValue_carbs = new SliceValue((float)currentUser.getPercent_carbs(), Color.parseColor("#FFFFEB3B"));
        sliceValue_carbs.setLabel(String.valueOf(currentUser.getPercent_carbs()));
        values.add(sliceValue_carbs);
        SliceValue sliceValue_fat = new SliceValue((float)currentUser.getPercent_fat(), Color.parseColor("#FF689F38"));
        sliceValue_fat.setLabel(String.valueOf(currentUser.getPercent_fat()));
        values.add(sliceValue_fat);

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) {
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            data.setCenterText1("Macros");

            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(MacroSettings.this.getAssets(), "fonts/green_avocado.ttf");
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }
        if (hasCenterText2) {
            data.setCenterText2("Tap for more info");

            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(MacroSettings.this.getAssets(), "fonts/green_avocado.ttf");
            data.setCenterText2Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }
        chart.animate();
        chart.animationDataUpdate(2f);
        chart.setPieChartData(data);
    }
    private void reset() {
        chart.setCircleFillRatio(1.0f);
        hasLabels = false;
        hasLabelsOutside = false;
        hasCenterCircle = false;
        hasCenterText1 = false;
        hasCenterText2 = false;
        isExploded = false;
        hasLabelForSelected = false;
    }
    private void settextData()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        switch(currentUser.getGoal())
        {
            case 0://lose
                goal.setText("Goal: Lose");
                break;
            case 1://maintain
                goal.setText("Goal: Maintain");
                break;

            case 2://gain
                goal.setText("Goal: Gain");
                break;
        }
        switch(currentUser.getWorkout_freq())
        {

            case 0:
                workoutfreq.setText("Freq: Low");
                break;
            case 1:
                workoutfreq.setText("Freq: Light");
                break;
            case 2:
                workoutfreq.setText("Freq: Modertae");
                break;
            case 3:
                workoutfreq.setText("Freq: Active");
                break;
            case 4:
                workoutfreq.setText("Freq: Extreme");
                break;
        }
        age.setText("Age: "+String.valueOf(currentUser.getAge()));
        gender.setText(currentUser.getGender());
        carbs.setText( "C: "+ String.valueOf(settings.getInt("user_carbs",-1)));
        prot.setText( "P: "+String.valueOf(settings.getInt("user_protein",-1)));
        fat.setText( "F: "+String.valueOf(settings.getInt("user_fat",-1)));
        switch (currentUser.getWeight_unit())
        {
            case 0 :
                weight .setText(String.valueOf(currentUser.getWeight())+" kg");
                break;
            case 1:
                weight .setText(String.valueOf(currentUser.getWeight())+" lbs");
                break;
        }
        switch (currentUser.getHeight_unit())
        {
            case 0 :
                height.setText(String.valueOf(currentUser.getHeight())+" ft");
                break;
            case 1:
                height.setText(String.valueOf(currentUser.getHeight())+" cm");
                break;
        }
        calories.setText("Cals: " + String.valueOf(settings.getInt("cals",-1)));
        bmr.setText("BMR: " +String.valueOf(settings.getInt("BMR",-1)));
    }


}
