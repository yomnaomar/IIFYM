package com.example.kareem.IIFYM_Tracker.Activities.Old_Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kareem.IIFYM_Tracker.Activities.Main.MainActivity;
import com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification.activityLogin;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Body_Height_Unit;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.Body_Weight_Unit;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;
import com.example.kareem.IIFYM_Tracker.ViewComponents.LockableViewPager;

public class Login_Abdu extends AppCompatActivity implements myFragEventListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private LockableViewPager mViewPager;
    private DatabaseConnector My_DB;
    User newUser;
    Body_Weight_Unit bodyweight;
    Body_Height_Unit bodyheight;
    float weightunit,heightunit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_abdu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        My_DB = new DatabaseConnector(getApplicationContext());
        // Specify that tabs should be displayed in the action bar.


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (LockableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setSwipeable(false); //disable swiping with gestures ( this will lock the view and restrict moving to buttons only)



        //My_DB.openReadableDB();
        //My_DB.openWriteableDB();
        newUser = new User(); //instantiate currentUser
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            switch (position)
            {
                case 0:
                    Signupfrag_Abdu frag1 = new Signupfrag_Abdu();
                    return frag1;
                case 1:
                    Profilefrag_Abdu frag2 = new Profilefrag_Abdu();
                    return frag2;
                case 2:
                    Goalsfrag_Abdu frag3 = new Goalsfrag_Abdu();
                    return frag3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOGIN/SIGNUP";
                case 1:
                    return "PROFILE";
                case 2:
                    return "GOALS/PREFERENCES";
            }
            return null;
        }
    }

    //TODO (Abdulwahab) : all listener functions for currentUser Login/Sign Up fragments ---------------------

    @Override
    public void insertUser() {
        //get all currentUser details and send to DB (called when currentUser sign up is finished)

        try {
            My_DB.insertUser(newUser);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"DB Error Occurred" +e.getMessage(),Toast.LENGTH_LONG).show();
            Log.d("DB ERROR", e.getMessage());
        }
        openHome(); //open main activity
    }

    @Override
    public void userLogin(String username) {
        //check if currentUser exists when trying to login (check credentials)
        //Cursor mCursor = My_DB.getUser(username);
        if(My_DB.validateLogin(username,getApplicationContext()))
        {
            //open home page here (main activity) and send user_id
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user_name", username);
            intent.putExtra("logged",true);
            intent.putExtra("user_id", My_DB.fetchUserID(username,getApplicationContext())); //get user_id
            startActivity(intent);
            finish();
        }
        else
        {
            userReg(username);
            //login failed - message will be displayed by validateLogin()
        }

    }
    @Override
    public void userReg(String username) {
        //star creating new User
        Log.d("DEBUG", "Switch To Profile Fragment");
        newUser.setUser_name(username);
        switchFrag(1); //store username and pass and switch to next page. (fragment switching does not lose value instances)

    }

    @Override
    public void storeuserProfile(String fname, String lname, String dob, String email, String gender, float weight, float height, int age, int weightunit, int heightunit) {

        newUser.setFname(fname);
        newUser.setLname(lname);
        newUser.setDob(dob);
        newUser.setEmail(email);
        newUser.setGender(gender);
        newUser.setWeight(weight);
        newUser.setHeight(height);
        newUser.setAge(age);
        newUser.setWeight_unit(weightunit);
        newUser.setHeight_unit(heightunit);

    }

    @Override
    public void storeuserGoals(int goal, int pcarbs, int pfat, int pprotein, int workout_freq) {
        newUser.setGoal(goal);
        newUser.setPercent_carbs(pcarbs);
        newUser.setPercent_fat(pfat);
        newUser.setPercent_protein(pprotein);
        newUser.setWorkout_freq(workout_freq);
        newUser.setUser_id(-1);
    }


    @Override
    public void switchFrag(int pos) {
        mViewPager.setCurrentItem(pos);
    }


    public void openHome() //final method before opening main activity
    {
        //insertUser();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isnewUser", true); // here string is the value you want to save
        editor.commit();


        Log.d("LOGIN","Signup Complete: User: "+ newUser.toString());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_name", newUser.getUser_name()); //get user_name
        intent.putExtra("logged",true);
        startActivity(intent); //open main activity
        finish(); //disable going back to login with BACK button
    }

    @Override
    public void GoToFirebase(){
        Intent intent = new Intent(this, activityLogin.class);
        intent.putExtra("user_name", newUser.getUser_name()); //get user_name
        intent.putExtra("logged",true);
        startActivity(intent); //open main activity
        finish(); //disable going back to login with BACK button
    }


}
