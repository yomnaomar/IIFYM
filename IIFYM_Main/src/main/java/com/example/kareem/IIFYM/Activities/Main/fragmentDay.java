package com.example.kareem.IIFYM.Activities.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.kareem.IIFYM.Database.SQLiteConnector;
import com.example.kareem.IIFYM.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM.Models.DailyItem;
import com.example.kareem.IIFYM.Models.DateHelper;
import com.example.kareem.IIFYM.Models.Food;
import com.example.kareem.IIFYM.Models.User;
import com.example.kareem.IIFYM.R;
import com.example.kareem.IIFYM.ViewComponents.AdapterDailyItem;
import com.example.kareem.IIFYM.ViewComponents.OnListItemDeletedListener;

import java.util.ArrayList;

public class fragmentDay extends Fragment implements AdapterView.OnItemClickListener, OnListItemDeletedListener{

    // GUI
    private TextView lblCaloriesCurrent, lblCarbsCurrent, lblProteinCurrent, lblFatCurrent;
    private TextView lblCaloriesLeft,    lblCarbsLeft,    lblProteinLeft,    lblFatLeft;
    private TextView lblCaloriesGoal,    lblCarbsGoal,    lblProteinGoal,    lblFatGoal;

    private ArrayList<DailyItem>    arrDailyItems;
    private AdapterDailyItem        adapterDailyItems;
    private ListView                listViewDailyItems;
    private RoundCornerProgressBar  progressBarCalories, progressBarCarbs, progressBarProtein, progressBarFat;
    private Animation               mEnterAnimation, mExitAnimation;

    // Variables
    private View view;
    private FragmentActivity    activity;
    private Context             context;
    private String              date;

    public boolean  isPercent;
    public int      caloriesCurrent = 0,    carbsCurrent = 0,   proteinCurrent = 0, fatCurrent = 0;
    public int      caloriesLeft = 0,       carbsLeft = 0,      proteinLeft = 0,    fatLeft = 0;
    public int      caloriesGoal,           carbsGoal,          proteinGoal,        fatGoal;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;
    private User                    currentUser;
    private String                  uid;

    public fragmentDay() {}

    public void setDate(DateHelper.StringDate day) {
        this.date = day.date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_day, container, false);
        activity = getActivity();
        context = view.getContext();

        // Database
        myPrefs = new SharedPreferenceHelper(context);
        DB_SQLite = new SQLiteConnector(context);

        // GUI
        initializeGUI();

        // User
        initializeUser();

        // Inflate the layout for this fragment
        return view;
    }

    private void initializeGUI() {
        ;

        lblCaloriesCurrent = (TextView) view.findViewById(R.id.lblCaloriesCurrent);
        lblCaloriesLeft = (TextView) view.findViewById(R.id.lblCaloriesLeft);
        lblCaloriesGoal = (TextView) view.findViewById(R.id.lblCaloriesGoal);

        lblCarbsCurrent = (TextView) view.findViewById(R.id.lblCarbsCurrent);
        lblCarbsLeft = (TextView) view.findViewById(R.id.lblCarbsLeft);
        lblCarbsGoal = (TextView) view.findViewById(R.id.lblCarbsGoal);

        lblProteinCurrent = (TextView) view.findViewById(R.id.lblProteinCurrent);
        lblProteinLeft = (TextView) view.findViewById(R.id.lblProteinLeft);
        lblProteinGoal = (TextView) view.findViewById(R.id.lblProteinGoal);

        lblFatCurrent = (TextView) view.findViewById(R.id.lblFatCurrent);
        lblFatLeft = (TextView) view.findViewById(R.id.lblFatLeft);
        lblFatGoal = (TextView) view.findViewById(R.id.lblFatGoal);

        progressBarCalories = (RoundCornerProgressBar) view.findViewById(R.id.progressBarCalories);
        progressBarCarbs = (RoundCornerProgressBar) view.findViewById(R.id.progressBarCarbs);
        progressBarProtein = (RoundCornerProgressBar) view.findViewById(R.id.progressBarProtein);
        progressBarFat = (RoundCornerProgressBar) view.findViewById(R.id.progressBarFat);

        arrDailyItems = new ArrayList<DailyItem>();
        adapterDailyItems = new AdapterDailyItem(context, arrDailyItems);

        listViewDailyItems = (ListView) view.findViewById(R.id.listviewDailyItems);
        listViewDailyItems.setAdapter(adapterDailyItems);
        listViewDailyItems.setOnItemClickListener(this);

        // Setup enter and exit animation
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
    }

    private void initializeUser() {
        uid = myPrefs.getStringValue("session_uid");
        currentUser = DB_SQLite.retrieveUser(uid);

        isPercent = currentUser.getIsPercent();

        caloriesGoal = currentUser.getDailyCalories();
        carbsGoal = currentUser.getDailyCarbs();
        proteinGoal = currentUser.getDailyProtein();
        fatGoal = currentUser.getDailyFat();

        if (isPercent){ // Convert from percentage to grams
            carbsGoal = (carbsGoal*caloriesGoal)/400;
            proteinGoal = (proteinGoal*caloriesGoal)/400;
            fatGoal = (fatGoal*caloriesGoal)/900;
        }

        Log.d("initializeUser", currentUser.toString());
    }

    private void updateMacros() {
        // Reset to prevent accumulation
        caloriesCurrent = 0;
        carbsCurrent = 0;
        proteinCurrent = 0;
        fatCurrent = 0;

        int itemCount = adapterDailyItems.getCount();
        for (int i = 0; i < itemCount; i++) {
            DailyItem dailyItem = adapterDailyItems.getItem(i);
            Food tempFood = DB_SQLite.retrieveFood(dailyItem.getFood_id());
            DailyItem tempDailyItem = DB_SQLite.retrieveDailyItem(dailyItem.getId());
            int calories = Math.round(tempFood.getCalories() * tempDailyItem.getMultiplier());
            int carbs = Math.round(tempFood.getCarbs() * tempDailyItem.getMultiplier());
            int protein = Math.round(tempFood.getProtein() * tempDailyItem.getMultiplier());
            int fat = Math.round(tempFood.getFat() * tempDailyItem.getMultiplier());

            caloriesCurrent += calories;
            carbsCurrent    += carbs;
            proteinCurrent  += protein;
            fatCurrent      += fat;
        }

        // Updating "Left" Variables
        caloriesLeft = caloriesGoal - caloriesCurrent;
        carbsLeft = carbsGoal - carbsCurrent;
        proteinLeft = proteinGoal - proteinCurrent;
        fatLeft = fatGoal - fatCurrent;

        // Updating Labels
        lblCaloriesGoal.setText(caloriesGoal + "");
        lblCarbsGoal.setText(carbsGoal + "");
        lblProteinGoal.setText(proteinGoal + "");
        lblFatGoal.setText(fatGoal + "");

        lblCaloriesLeft.setText(caloriesLeft + "");
        lblCarbsLeft.setText(carbsLeft + "");
        lblProteinLeft.setText(proteinLeft + "");
        lblFatLeft.setText(fatLeft + "");

        lblCaloriesCurrent.setText(caloriesCurrent + "");
        lblCarbsCurrent.setText(carbsCurrent + "");
        lblProteinCurrent.setText(proteinCurrent + "");
        lblFatCurrent.setText(fatCurrent + "");

        // Updating ProgressBars
        if(caloriesCurrent < caloriesGoal) {
            progressBarCalories.setProgress(100 * caloriesCurrent / caloriesGoal);
            lblCaloriesLeft.setTextColor(context.getResources().getColor(R.color.CaloriesProgessColor));
            lblCaloriesLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarCalories.setProgress(100);
            lblCaloriesLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblCaloriesLeft.setTypeface(null, Typeface.BOLD);
        }

        if(carbsCurrent < carbsGoal) {
            progressBarCarbs.setProgress(100 * carbsCurrent / carbsGoal);
            lblCarbsLeft.setTextColor(context.getResources().getColor(R.color.CarbProgressColor));
            lblCarbsLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarCarbs.setProgress(100);
            lblCarbsLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblCarbsLeft.setTypeface(null, Typeface.BOLD);
        }
        if(proteinCurrent < proteinGoal) {
            progressBarProtein.setProgress(100 * proteinCurrent / proteinGoal);
            lblProteinLeft.setTextColor(context.getResources().getColor(R.color.ProteinProgessColor));
            lblProteinLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarProtein.setProgress(100);
            lblProteinLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblProteinLeft.setTypeface(null, Typeface.BOLD);
        }
        if(fatCurrent < fatGoal) {
            progressBarFat.setProgress(100 * fatCurrent / fatGoal);
            lblFatLeft.setTextColor(context.getResources().getColor(R.color.FatProgessColor));
            lblFatLeft.setTypeface(null, Typeface.NORMAL);
        }
        else {
            progressBarFat.setProgress(100);
            lblFatLeft.setTextColor(context.getResources().getColor(R.color.error_red));
            lblFatLeft.setTypeface(null, Typeface.BOLD);
        }
    }

    private void updateArrayList() {
        adapterDailyItems.clear();
        arrDailyItems = DB_SQLite.retrieveAllDailyItems(date);
        for (int i = 0; i <arrDailyItems.size(); i++) {
            adapterDailyItems.add(arrDailyItems.get(i));
        }
    }

    public void render() {
        updateArrayList();
        updateMacros();
    }

    @Override public void onResume() {
        super.onResume();
        initializeUser();
        render();
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onItemDeleted() {
        render();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long row_id) {
        DailyItem dailyItem = (DailyItem) parent.getItemAtPosition(position);
        int id = dailyItem.getId();

        Intent intent = new Intent(activity.getBaseContext(), activityViewDailyItem.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
