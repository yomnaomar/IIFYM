package com.example.kareem.IIFYM_Tracker.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;

/**
 * Created by Kareem on 8/6/2016.
 */
public class MacronutrientPreferenceActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences prefs;
    private float Carb_Percent, Protein_Percent, Fat_Percent, Old_Carb, Old_Protein, Old_Fat;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    String username;
    private DatabaseConnector MY_DB;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.macronutrient_preference);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        MY_DB = new DatabaseConnector(getApplicationContext());


        Old_Carb = Float.parseFloat(prefs.getString("pref_Carbs", "50"));
        Old_Protein = Float.parseFloat(prefs.getString("pref_Protein", "25"));
        Old_Fat = Float.parseFloat(prefs.getString("pref_Fat", "25"));

        username = prefs.getString("user_name","");
        User user = MY_DB.getUserObject(username);


        Carb_Percent = user.getPercent_carbs();
        Fat_Percent = user.getPercent_fat();
        Protein_Percent = user.getPercent_protein();
        Log.d("MacrosPrefs ",Carb_Percent + " "+ Fat_Percent+" "+ Protein_Percent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME", "Prefsresumed");
       // prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() { //TODO(Abdulwahab): update DB when currentUser changes prefs
        Log.d("PAUSE", "PrefsPaused");
        Carb_Percent = Float.parseFloat(prefs.getString("pref_Carbs", "50"));
        Protein_Percent = Float.parseFloat(prefs.getString("pref_Protein", "25"));
        Fat_Percent = Float.parseFloat(prefs.getString("pref_Fat", "25"));

        if ((Carb_Percent + Protein_Percent + Fat_Percent) != 100){
            Toast toast = Toast.makeText(this, "Total was not 100%, values defaulted", Toast.LENGTH_SHORT);
            toast.show();
            prefs.unregisterOnSharedPreferenceChangeListener(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pref_Carbs", Old_Carb + "");
            editor.putString("pref_Protein", Old_Protein + "");
            editor.putString("pref_Fat", Old_Fat + "");
            editor.commit();
        }
        else {
            prefs.unregisterOnSharedPreferenceChangeListener(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pref_Carbs", Carb_Percent + "");
            editor.putString("pref_Protein", Protein_Percent + "");
            editor.putString("pref_Fat", Fat_Percent+ "");
            editor.commit();
        }
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "carbs":
                Carb_Percent = Float.parseFloat(prefs.getString("pref_Carbs", "50"));
                Toast toast1 = Toast.makeText(this, "Carb ratio changed to " + Carb_Percent, Toast.LENGTH_SHORT);
                toast1.show();
                break;
            case "protein":
                Protein_Percent = Float.parseFloat(prefs.getString("pref_Protein", "25"));
                Toast toast2 = Toast.makeText(this, "protein ratio changed to " + Protein_Percent, Toast.LENGTH_SHORT);
                toast2.show();
                break;
            case "fat":
                Fat_Percent = Float.parseFloat(prefs.getString("pref_Fat", "25"));
                Toast toast3 = Toast.makeText(this, "fat ratio changed to " + Fat_Percent, Toast.LENGTH_SHORT);
                toast3.show();
                break;
        }
    }
}