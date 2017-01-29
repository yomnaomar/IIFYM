package com.example.kareem.IIFYM_Tracker.Activities.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;

public class EditProfile_Mina extends AppCompatActivity implements View.OnClickListener{

    EditText userid ;
    TextView username;
    EditText dob;
    EditText fname;
    EditText lname;
    EditText email;
    Button update;
    String user_name;
    DatabaseConnector DB;

    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userid = (EditText) findViewById(R.id.UserIdET);
        username = (TextView) findViewById(R.id.UsernameTV2);
        dob = (EditText) findViewById(R.id.DOBET);
        fname = (EditText) findViewById(R.id.FNameET);
        lname = (EditText) findViewById(R.id.LNameET);
        email = (EditText) findViewById(R.id.EmailET);
        update = (Button) findViewById(R.id.UpdateBTN);
        user_name = "";
        user_name = settings.getString("user_name",null);
        userid.setEnabled(false);

//        Intent i = getIntent();
//        user_name = i.getStringExtra("username");
        Log.i("username",user_name);

        update.setOnClickListener(this);

        DB = new DatabaseConnector(getApplication());


    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Cursor C = DB.getUser(user_name);
        if(C.moveToFirst())
        {
            userid.setText(C.getString(0));
            username.setText(C.getString(1));
            dob.setText(C.getString(2));
            fname.setText(C.getString(9));
            lname.setText(C.getString(10));
            email.setText(C.getString(11));

        }
        else
        {
            userid.setText("User_Old is Not Found !!");
            Toast.makeText(this, "Ooops User_Old is not there", Toast.LENGTH_SHORT).show();
            Log.i("Error",user_name);
        }*/
    }

    @Override
    public void onClick(View view) {

        switch ( view.getId()) {
            case R.id.UpdateBTN: {

                String id = userid.getText().toString();
                String fn = fname.getText().toString();
                String dateofbirth = dob.getText().toString();
                String ln = lname.getText().toString();
                String em = email.getText().toString();

               if( DB.updateuserdata(id,user_name,dateofbirth,fn,ln,em)) {
                   Toast.makeText(getApplication(), "Profile Updated", Toast.LENGTH_LONG).show();
//                   Intent i = new Intent(getApplicationContext(), UserProfile_Mina.class);
//                   i.putExtra("username", user_name);
//                   startActivity(i);
                   finish();
                   break;
               }
                else {
                   Toast.makeText(getApplication(),"Cannot Update",Toast.LENGTH_LONG).show();
                   break;
               }
            }
        }


    }
}
