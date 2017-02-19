package com.example.kareem.IIFYM_Tracker.Activities.Settings;

import android.support.v7.app.AppCompatActivity;

public class UserProfile_Mina extends AppCompatActivity {}
/*public class UserProfile_Mina extends AppCompatActivity implements View.OnClickListener{

    private SQLiteConnector DB;
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

        DB = new SQLiteConnector(getApplicationContext());

    }

    @Override
    public void onClick(View view) {

        Intent i = new Intent(getApplicationContext(),EditProfile_Mina.class);
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
        Cursor C = DB.getUserOld(user_name);
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
            userid.setText("User_Old is Not Found");
            Toast.makeText(this, "Error Finding User_Old", Toast.LENGTH_SHORT).show();
        }
    }
}
*/
