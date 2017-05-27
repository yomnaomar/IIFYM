package com.karimchehab.IIFYM.Activities.UserLoginAuthentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.karimchehab.IIFYM.Activities.Main.activityHome;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.R;

public class activityLauncher extends AppCompatActivity {

    private SharedPreferenceHelper  myPrefs;
    private Context                 context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        String session_uid = myPrefs.getStringValue("session_uid");

        if(session_uid.isEmpty()){
            // Go to activityLogin
            Intent intent = new Intent();
            intent.setClass(context, activityLogin1.class);
            startActivity(intent);
            finish();
        }
        else {
            // Go to activityHome
            Intent intent = new Intent();
            intent.setClass(context, activityHome.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_launcher);
    }
}