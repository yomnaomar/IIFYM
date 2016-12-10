package com.example.kareem.macrotracker.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidadvance.topsnackbar.TSnackbar;
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
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MacroSettings extends AppCompatActivity implements View.OnFocusChangeListener{

    String user_name;
    DatabaseConnector My_DB;
    User currentUser;

    EditText goal,gender,carbs,prot,fat,workoutfreq,weight,height,calories,bmr,age;
    View parentLayout;

    FloatingActionButton edit_fab;
    SharedPreferences settings;
    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = true;
    private boolean hasCenterText1 = true;
    private boolean hasCenterText2 = true;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    private boolean isEnabled=false;

    private int Carbs_Val,Protein_Val,Fat_Val;
    private double userCalories;


    private Animation animation;
    TourGuide mTourGuideHandler;
    private TextView psym1,psym2,psym3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_settings);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        animation = new TranslateAnimation(0f, 0f, 200f, 0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setInterpolator(new BounceInterpolator());
        //tour guide
        ToolTip toolTip = new ToolTip()
                .setTitle("Quick Edit")
                .setDescription("Click here to edit your macro settings, click again to save")
                .setTextColor(Color.parseColor("#bdc3c7"))
                .setBackgroundColor(Color.parseColor("#e74c3c"))
                .setShadow(true)
                .setGravity(Gravity.TOP | Gravity.LEFT)
                .setEnterAnimation(animation);
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(toolTip)
                .setOverlay(new Overlay());
        //tour guide

        edit_fab = (FloatingActionButton)findViewById(R.id.edit_fab);
        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEnabled) {
                    dismissTourguide();
                    enableFields();
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_black_24dp));
                }
                else {
                    edit_fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp));
                    saveData();
                }
            }
        });


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
        psym1=(TextView)findViewById(R.id.percentsym1);
        psym2=(TextView)findViewById(R.id.percentsym2);
        psym3=(TextView)findViewById(R.id.percentsym3);

        age.setOnFocusChangeListener(this);
        gender.setOnFocusChangeListener(this);
        carbs.setOnFocusChangeListener(this);
        prot.setOnFocusChangeListener(this);
        fat.setOnFocusChangeListener(this);
        workoutfreq.setOnFocusChangeListener(this);
        weight.setOnFocusChangeListener(this);
        height.setOnFocusChangeListener(this);
        goal.setOnFocusChangeListener(this);



        chart = (PieChartView)findViewById(R.id.macro_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_name = settings.getString("user_name", "");
        currentUser = My_DB.getUserObject(user_name);

        generateData(String.valueOf(currentUser.getPercent_carbs()),String.valueOf(currentUser.getPercent_protein()),String.valueOf(currentUser.getPercent_fat()));
        settextData();



        showtipOverlay();//UI guide

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch(view.getId())
        {
            case R.id.txt_age:
                if(b) {
                    new MaterialDialog.Builder(this)
                            .title("Age")
                            .content("")
                            .cancelable(true)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(age.getText().toString(), "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // Do something
                                    if(input.length()>0)
                                        age.setText(input);
                                    else
                                        age.setText(age.getText().toString());
                                    try {
                                        currentUser.setAge(Integer.parseInt(input.toString()));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    age.clearFocus();
                                }
                            }).show();
                }
                break;
            case R.id.txt_gender:
                if(b) {
                    new MaterialDialog.Builder(this)
                            .title("Gender")
                            .items(R.array.items)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    try {
                                        if(which>=0)
                                            gender.setText(text);
                                        else
                                            gender.setText(gender.getText().toString());
                                        currentUser.setGender(text.toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    gender.clearFocus();
                                    return true;
                                }
                            })
                            .positiveText("Ok")
                            .show();
                }

                break;
            case R.id.txt_height:

                if(b)
                {
                    new MaterialDialog.Builder(this)
                            .title("Height")
                            .content("")
                            .cancelable(true)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .input(height.getText().toString(), "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // Do something
                                    switch (currentUser.getHeight_unit())
                                    {
                                        case 0 :
                                            if(input.length()>0)
                                                height.setText(input+" ft");
                                            else
                                                height.setText(height.getText().toString());
                                            break;
                                        case 1:
                                            if(input.length()>0)
                                                height.setText(input+" cm");
                                            else
                                                height.setText(height.getText().toString());
                                            break;
                                    }
                                    try {
                                        currentUser.setHeight(Float.parseFloat(input.toString()));
                                    } catch (NumberFormatException e) {

                                    }
                                    height.clearFocus();

                                }
                            }).show();
                }

                break;

            case R.id.txt_weight:
                if(b) {
                    new MaterialDialog.Builder(this)
                            .title("Weight")
                            .content("")
                            .cancelable(true)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .input(weight.getText().toString(), "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // Do something
                                    switch (currentUser.getWeight_unit()) {
                                        case 0:
                                            if(input.length()>0)
                                                weight.setText(input + " kg");
                                            else
                                                weight.setText(weight.getText().toString());
                                            break;
                                        case 1:
                                            if(input.length()>0)
                                                weight.setText(input + " lbs");
                                            else
                                                weight.setText(weight.getText().toString());
                                            break;
                                    }
                                    try {
                                        currentUser.setWeight(Float.parseFloat(input.toString()));
                                    } catch (NumberFormatException e) {


                                    }
                                    weight.clearFocus();

                                }
                            }).show();
                }

                break;
            case R.id.txt_workoutfreq:
                if(b)
                {
                    new MaterialDialog.Builder(this)
                            .title("Workout Frequency")
                            .items(R.array.workfreqarray)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    try {
                                        switch(which)
                                        {
                                            case 0:
                                                workoutfreq.setText("None");
                                                currentUser.setWorkout_freq(0);
                                                break;
                                            case 1:
                                                workoutfreq.setText("Low");
                                                currentUser.setWorkout_freq(1);
                                                break;
                                            case 2:
                                                workoutfreq.setText("Medium");
                                                currentUser.setWorkout_freq(2);
                                                break;
                                            case 3:
                                                workoutfreq.setText("High");
                                                currentUser.setWorkout_freq(3);
                                                break;
                                            case 4:
                                                workoutfreq.setText("Very High");
                                                currentUser.setWorkout_freq(4);
                                                break;
                                            default:
                                                workoutfreq.setText(workoutfreq.getText().toString());
                                                currentUser.setWorkout_freq(currentUser.getWorkout_freq());
                                                break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    workoutfreq.clearFocus();
                                    return true;
                                }
                            })
                            .positiveText("Ok")
                            .show();
                }

                break;
            case R.id.txt_goal:
                if(b)
                {
                    new MaterialDialog.Builder(this)
                            .title("Goal")
                            .items(R.array.goalsarray)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    try {
                                        switch(which)
                                        {
                                            case 0://lose
                                                goal.setText("Lose");
                                                currentUser.setGoal(0);
                                                break;
                                            case 1://maintain
                                                goal.setText("Maintain");
                                                currentUser.setGoal(1);
                                                break;

                                            case 2://gain
                                                goal.setText("Gain");
                                                currentUser.setGoal(2);
                                                break;
                                            default:
                                                goal.setText(goal.getText().toString());
                                                currentUser.setGoal(currentUser.getGoal());
                                                break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    goal.clearFocus();
                                    return true;
                                }
                            })
                            .positiveText("Ok")
                            .show();
                }

                break;


        }

    }

    private void prepareDataAnimation() {
        for (SliceValue value : data.getValues()) {
            value.setTarget((float) Math.random() * 30 + 15);
        }
    }

    private void explodeChart() {
        isExploded = !isExploded;
        generateData(String.valueOf(currentUser.getPercent_carbs()),String.valueOf(currentUser.getPercent_protein()),String.valueOf(currentUser.getPercent_fat()));

    }

    private void generateData(String carbs, String prot, String fat) {

        List<SliceValue> values = new ArrayList<>();

        SliceValue sliceValue_prot = new SliceValue((float)currentUser.getPercent_protein(), Color.parseColor("#FF4081"));
        sliceValue_prot.setLabel("Protein: "+prot+"%");
        values.add(sliceValue_prot);
        SliceValue sliceValue_carbs = new SliceValue((float)currentUser.getPercent_carbs(), Color.parseColor("#FFFFEB3B"));
        sliceValue_carbs.setLabel("Carbs: "+carbs+"%");
        values.add(sliceValue_carbs);
        SliceValue sliceValue_fat = new SliceValue((float)currentUser.getPercent_fat(), Color.parseColor("#FF689F38"));
        sliceValue_fat.setLabel("Fat: "+fat+"%");
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

        switch(currentUser.getGoal())
        {
            case 0://lose
                goal.setText("Lose");
                break;
            case 1://maintain
                goal.setText("Maintain");
                break;

            case 2://gain
                goal.setText("Gain");
                break;
        }
        switch(currentUser.getWorkout_freq())
        {

            case 0:
                workoutfreq.setText("None");
                break;
            case 1:
                workoutfreq.setText("Low");
                break;
            case 2:
                workoutfreq.setText("Medium");
                break;
            case 3:
                workoutfreq.setText("High");
                break;
            case 4:
                workoutfreq.setText("Very High");
                break;
        }
        age.setText(String.valueOf(currentUser.getAge()));
        gender.setText(currentUser.getGender());
        carbs.setText( String.valueOf(settings.getInt("user_carbs",-1)));
        prot.setText( String.valueOf(settings.getInt("user_protein",-1)));
        fat.setText( String.valueOf(settings.getInt("user_fat",-1)));
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
        calories.setText("Calories: " + String.valueOf(settings.getInt("cals",-1)));
        bmr.setText("BMR: " +String.valueOf(settings.getInt("BMR",-1)));
    }

    private void enableFields()
    {
        //enable all edittext fields
        goal.setEnabled(true);
        gender.setEnabled(true);
        carbs.setEnabled(true);
        prot.setEnabled(true);
        fat.setEnabled(true);
        workoutfreq.setEnabled(true);
        weight.setEnabled(true);
        height.setEnabled(true);
        age.setEnabled(true);
        isEnabled=true;
        psym1.setVisibility(View.VISIBLE);
        psym2.setVisibility(View.VISIBLE);
        psym3.setVisibility(View.VISIBLE);
        //convert macros to percent in edittext fields
        carbs.setText(""+currentUser.getPercent_carbs());
        prot.setText(""+currentUser.getPercent_protein());
        fat.setText(""+currentUser.getPercent_fat());

        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "Edit Mode", TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#FFFF4081"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void saveData()
    {


        //save all new values to DB here
        int carbsint,protint,fatint;
        carbsint= Integer.parseInt(carbs.getText().toString());
        protint= Integer.parseInt(prot.getText().toString());
        fatint= Integer.parseInt(fat.getText().toString());

        if(carbsint+protint+fatint==100) {

            psym1.setVisibility(View.INVISIBLE);
            psym2.setVisibility(View.INVISIBLE);
            psym3.setVisibility(View.INVISIBLE);
            goal.setEnabled(false);
            gender.setEnabled(false);
            carbs.setEnabled(false);
            prot.setEnabled(false);
            fat.setEnabled(false);
            workoutfreq.setEnabled(false);
            weight.setEnabled(false);
            height.setEnabled(false);
            age.setEnabled(false);

            currentUser.setPercent_carbs(carbsint);
            currentUser.setPercent_protein(protint);
            currentUser.setPercent_fat(fatint);


            TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "Saved", TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#FFAED581"));
            TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.BLACK);
            snackbar.show();

            isEnabled = false;

            My_DB.updateUser(currentUser);

            //refresh graphs and fields
            bmr.setText("BMR: "+getBMR());
            carbs.setText(""+Carbs_Val);
            prot.setText(""+Protein_Val);
            fat.setText(""+Fat_Val);
            calories.setText("Calories: "+(int)userCalories);
            generateData(String.valueOf(currentUser.getPercent_carbs()), String.valueOf(currentUser.getPercent_protein()), String.valueOf(currentUser.getPercent_fat()));

            carbs.setError(null);
            fat.setError(null);
            prot.setError(null);

        }
        else
        {
            carbs.setError("Total must be 100");
            fat.setError("Total must be 100");
            prot.setError("Total must be 100");
        }


    }

    //TODO: (Abdulwahab) modify the pie information that appears when user clicks on chart segments
    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            switch(arcIndex)
            {
                case 0://P
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, "Protein is essential for growth and the building of new tissue as well as the repair of broken down tissue - like what happens when you work out.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
                case 1://C
                    Snackbar snackbar2 = Snackbar
                            .make(parentLayout, "Carbohydrates are the preferred fuel source for your bodies - and brain's - energy needs. It's carb energy that fuels your workouts. ", Snackbar.LENGTH_LONG);
                    snackbar2.show();
                    break;

                case 2://F
                    Snackbar snackbar3 = Snackbar
                            .make(parentLayout, "Fats, technically called lipids, are the most energy dense of the three macro nutrients. They are composed of building blocks called fatty acids, which fall into three main categories:Saturated,Polyunsaturated, and Monounsaturated", Snackbar.LENGTH_LONG);
                    snackbar3.show();

                    break;
            }

        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

    private int getBMR()
    {
        double BMR; // b/w 1000 - 3000
        //get all user data height, weight ,age:
        float Weight_Val = currentUser.getWeight();
        float Height_Val = currentUser.getHeight();
        String gender = currentUser.getGender();
        int Age_Val = currentUser.getAge();
        double Caloric_Intake;
        double Carb_Percent,Protein_Percent,Fat_Percent;

        Log.d("BMRINFO", ""+ Weight_Val+ " "+ Height_Val+" "+gender+" "+Age_Val);
        if(currentUser.getWeight_unit()!= 0) //not kg - convert from lbs to kg
        {
            Weight_Val = (Weight_Val/2.2046f);
            Log.d("WEIGHT", ""+Weight_Val);
        }
        if(currentUser.getHeight_unit()!=1)//not cm - convert from feet to cm
        {
            Height_Val = (Height_Val/0.032808f);
            Log.d("HEIGHT", ""+Height_Val);
        }
        if (gender.startsWith("M")) {
            BMR = (10*Weight_Val + 6.25*Height_Val + 5*Age_Val + 5.0); //Male
            Log.d("BMRMALE", ""+BMR);
        }
        else
        {
            BMR = (10*Weight_Val + 6.25*Height_Val + 5*Age_Val - 161.0); //Female
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


        Carbs_Val = (int) ((Carb_Percent * Caloric_Intake) /4.0); //carbs = 4kcal/g
        Protein_Val = (int) ((Protein_Percent * Caloric_Intake) / 4.0); //protein = 4kcal/g
        Fat_Val = (int) ((Fat_Percent * Caloric_Intake)/ 9.0); //fat = 9kcal/g


        userCalories = Caloric_Intake;
        return (int) BMR;
    }

    private void showtipOverlay()
    {
        if(settings.getBoolean("isnewtomacroSettings",true))
        {

            mTourGuideHandler.playOn(edit_fab);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isnewtomacroSettings", false); // here string is the value you want to save
            editor.commit();
        }
    }
    public void dismissTourguide(){
        try{
            if(null != mTourGuideHandler){
                mTourGuideHandler.cleanUp();
            }
        }catch (Exception e){}
    }
}
