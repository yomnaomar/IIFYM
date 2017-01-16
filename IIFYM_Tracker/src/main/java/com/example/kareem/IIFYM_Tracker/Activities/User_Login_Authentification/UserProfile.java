package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.IIFYM_Tracker.Activities.Settings.EditProfile;
import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;

public class UserProfile extends AppCompatActivity implements View.OnClickListener{

    private DatabaseConnector DB;
    TextView username;
    TextView userid;
    TextView dob;
    TextView fname;
    TextView lname;
    TextView email;
    Button edit;
    String user_name; // Receiving The Username From the Calling Intent
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userid =(TextView) findViewById(R.id.UserIDTV);
        username = (TextView) findViewById(R.id.UsernameTV);
        dob  = (TextView) findViewById(R.id.DOBTV);
        fname= (TextView) findViewById(R.id.FNameTV);
        lname= (TextView) findViewById(R.id.LNameTV);
        email= (TextView) findViewById(R.id.EmailTV);
        edit= (Button) findViewById(R.id.EditBTN);
        edit.setOnClickListener(this);

        DB = new DatabaseConnector(getApplicationContext());

    }

    @Override
    public void onClick(View view) {

        Intent i = new Intent(getApplicationContext(),EditProfile.class);
        //i.putExtra("username",user_name);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        user_name = "";
        user_name = settings.getString("user_name",null);
       // user_name = intent.getStringExtra("username");
        Log.i("Username",user_name);
        Cursor C = DB.getUser(user_name);
        if(C.moveToFirst()) {
            userid.setText(C.getString(0));
            username.setText(C.getString(1));
            dob.setText(C.getString(2));
            fname.setText(C.getString(9));
            lname.setText(C.getString(10));
            email.setText(C.getString(11));
            //Log.i("Email",C.getString(11));
        }
        else
        {
            userid.setText("User is Not Found");
            Toast.makeText(this, "Error Finding User", Toast.LENGTH_SHORT).show();
        }
    }
}
