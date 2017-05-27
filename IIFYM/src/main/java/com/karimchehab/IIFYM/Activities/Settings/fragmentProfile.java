package com.karimchehab.IIFYM.Activities.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.Models.User;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.ViewComponents.DecimalDigitsInputFilter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import info.hoang8f.android.segmented.SegmentedGroup;


public class fragmentProfile extends Fragment implements View.OnClickListener, OnDateSetListener, TextWatcher, AdapterView.OnItemSelectedListener {

    // GUI
    private String              uid;
    private TextView            lblWeightUnit, lblHeightUnit1, lblHeightUnit2;
    private EditText            etxtName, etxtDateOfBirth, etxtWeight, etxtHeightParam1, etxtHeightParam2;
    private LinearLayout        linearlayoutHeight, linearlayoutWeight;
    private RadioButton         rbtnGenderMale, rbtnGenderFemale, rbtnMetric, rbtnImperial;
    private SegmentedGroup      seggroupUnitSystem, seggroupGender;
    private Spinner             spinnerWorkoutFreq, spinnerGoals;
    private SimpleDateFormat    dateFormatter;
    private LinearLayout        linearLayoutRoot;
    private View                view;
    private ProgressDialog      progressDialog;

    // Variables
    private String      name, dob;
    private int         gender;
    private int         unitSystem;
    private float       weight;
    private int         height1, height2, workoutFreq, goal;
    private Context     context;
    private User        user;

    // Database
    private SharedPreferenceHelper  myPrefs;
    private SQLiteConnector         DB_SQLite;
    private DatabaseReference       firebaseDbRef;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private changesMade profileChangesMade = new changesMade(false);

    // Required empty public constructor
    public fragmentProfile() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentProfile.
     */
    public static fragmentProfile newInstance(String param1, String param2) {
        fragmentProfile fragment = new fragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Database
        context = getActivity().getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        firebaseDbRef = FirebaseDatabase.getInstance().getReference();
        DB_SQLite = new SQLiteConnector(context);

        // Options Menu
        setHasOptionsMenu(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // GUI
        initializeGUI();

        // User Data
        getUserData();

        // Set values to User's stored preferences
        setInitialValues();

        return view;
    }

    private void initializeGUI() {
        etxtName            = (EditText)view.findViewById(R.id.etxtName);
        etxtDateOfBirth     = (EditText)view.findViewById(R.id.etxtDateOfBirth);
        etxtWeight          = (EditText)view.findViewById(R.id.etxtWeight);
        etxtHeightParam1    = (EditText)view.findViewById(R.id.etxtHeightParam1);
        etxtHeightParam2    = (EditText)view.findViewById(R.id.etxtHeightParam2);
        lblWeightUnit       = (TextView)view.findViewById(R.id.lblWeightUnit);
        lblHeightUnit1      = (TextView)view.findViewById(R.id.lblHeightUnit1);
        lblHeightUnit2      = (TextView)view.findViewById(R.id.lblHeightUnit2);
        linearlayoutHeight  = (LinearLayout)view.findViewById(R.id.linearlayoutHeight);
        linearlayoutWeight  = (LinearLayout)view.findViewById(R.id.linearlayoutWeight);
        seggroupGender      = (SegmentedGroup) view.findViewById(R.id.seggroupGender);
        rbtnGenderMale      = (RadioButton)view.findViewById(R.id.rbtnGenderMale);
        rbtnGenderFemale    = (RadioButton)view.findViewById(R.id.rbtnGenderFemale);
        seggroupUnitSystem  = (SegmentedGroup) view.findViewById(R.id.seggroupUnitSystem);
        rbtnMetric          = (RadioButton)view.findViewById(R.id.rbtnMetric);
        rbtnImperial        = (RadioButton)view.findViewById(R.id.rbtnImperial);
        spinnerWorkoutFreq  = (Spinner)view.findViewById(R.id.spinnerWorkoutFreq);
        spinnerGoals        = (Spinner)view.findViewById(R.id.spinnerGoals);
        linearLayoutRoot    = (LinearLayout)view.findViewById(R.id.linearLayoutRoot);

        etxtDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    showDatePicker();
            }
        });
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        etxtDateOfBirth.setInputType(InputType.TYPE_NULL);
        etxtDateOfBirth.setOnClickListener(this);

        etxtWeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});

        // Change detectors
        etxtName.addTextChangedListener(this);
        etxtDateOfBirth.addTextChangedListener(this);
        etxtWeight.addTextChangedListener(this);
        etxtHeightParam1.addTextChangedListener(this);
        etxtHeightParam2.addTextChangedListener(this);

        seggroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                profileChangesMade.setChanged(true);
                ((activitySettings) getActivity()).setChangesDetected(profileChangesMade.isChanged());
            }
        });

        seggroupUnitSystem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                profileChangesMade.setChanged(true);
                ((activitySettings) getActivity()).setChangesDetected(profileChangesMade.isChanged());
                unitSystemChange();
            }
        });

        ArrayAdapter<CharSequence> adapterWorkoutFreq = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_WorkoutFreq, R.layout.spinner_item);
        adapterWorkoutFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterGoals = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_Goals, R.layout.spinner_item);
        adapterGoals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerWorkoutFreq.setAdapter(adapterWorkoutFreq);
        spinnerGoals.setAdapter(adapterGoals);

        profileChangesMade.setListener(new changesMade.ChangeListener() {
            @Override
            public void onChange() {
                Log.d("profileChangesMade", profileChangesMade.isChanged() + "");
            }
        });

    }

    private void getUserData() {
        uid = myPrefs.getStringValue("session_uid");
        user = DB_SQLite.retrieveUser(uid);
        Log.d("getUserData", user.toString());

        name = user.getName();
        dob = user.getDob();
        gender = user.getGender();
        unitSystem = user.getUnitSystem();
        weight = user.getWeight();
        height1 = user.getHeight1();
        height2 = user.getHeight2();
        workoutFreq = user.getWorkoutFreq();
        goal = user.getGoal();
    }

    private void setInitialValues() {
        etxtName.setText(name);
        etxtDateOfBirth.setText(dob);
        if(gender == 0) // Male
            rbtnGenderMale.setChecked(true);
        else
            rbtnGenderFemale.setChecked(true); // Female
        if(unitSystem == 0) // Metric
            rbtnMetric.setChecked(true);
        else
            rbtnImperial.setChecked(true); // Imperial

        if(weight != 0)
            etxtWeight.setText(weight + "");
        else
            etxtWeight.setText("");

        if(height1 != 0)
            etxtHeightParam1.setText(height1 + "");
        else
            etxtHeightParam1.setText("");

        if(height2 != 0)
            etxtHeightParam2.setText(height2 + "");
        else
            etxtHeightParam2.setText("");

        spinnerWorkoutFreq.setSelection(workoutFreq, false);
        spinnerGoals.setSelection(goal, false);

        // Item Selected Listeners
        spinnerWorkoutFreq.setOnItemSelectedListener(this);
        spinnerGoals.setOnItemSelectedListener(this);

        profileChangesMade.setChanged(false);
        ((activitySettings) getActivity()).setChangesDetected(profileChangesMade.isChanged());
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etxtDateOfBirth:
                showDatePicker();
                break;
        }
    }

    private void showDatePicker() {
        String ageArr[] = dob.split("-");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(ageArr[2]),
                Integer.parseInt(ageArr[1]) - 1,
                Integer.parseInt(ageArr[0]));
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.showYearPickerFirst(true);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void updateUser() {
        if (isNetworkStatusAvialable(context)) {
            if (validateFields()) {
                readUserInput();

                user.setName(name);
                user.setDob(dob);
                user.setGender(gender);
                user.setUnitSystem(unitSystem);
                user.setHeight1(height1);
                user.setHeight2(height2);
                user.setWeight(weight);
                user.setWorkoutFreq(workoutFreq);
                user.setGoal(goal);

                showProgressDialog();

                firebaseDbRef.child("users").child(uid).setValue(user, new DatabaseReference.CompletionListener() {
                    public void onComplete(DatabaseError error, DatabaseReference ref) {

                        // No error
                        if (error == null) {
                            DB_SQLite.updateUser(user);

                            hideProgressDialog();

                            profileChangesMade.setChanged(false);
                            ((activitySettings) getActivity()).setChangesDetected(profileChangesMade.isChanged());

                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                        // Error writing to database
                        else {
                        }
                    }
                });
            }
        } else {
            showNetworkDialog("No Internet Connection", "Please check your internet connection and try again.");
        }
    }

    private void showNetworkDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    private void readUserInput() {
        name = etxtName.getText().toString();

        dob = etxtDateOfBirth.getText().toString();

        if (rbtnGenderMale.isChecked()) {
            gender = 0; // Male
        } else {
            gender = 1; // Female
        }

        // Metric
        if (rbtnMetric.isChecked()) {
            unitSystem = 0; // Metric
            if(!etxtWeight.getText().toString().isEmpty())
                weight = Float.parseFloat(etxtWeight.getText().toString());
            else
                weight = 0.0f;
            if(!etxtHeightParam1.getText().toString().isEmpty())
                height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
            else
                height1 = 0;
            height2 = 0;
        }

        // Imperial
        else {
            unitSystem = 0; // Imperial
            if(!etxtWeight.getText().toString().isEmpty())
                weight = Float.parseFloat(etxtWeight.getText().toString());
            else
                weight = 0.0f;
            if(!etxtHeightParam1.getText().toString().isEmpty())
                height1 = Integer.parseInt(etxtHeightParam1.getText().toString());
            else
                height1 = 0;
            if(!etxtHeightParam2.getText().toString().isEmpty())
                height2 = Integer.parseInt(etxtHeightParam2.getText().toString());
            else
                height2 = 0;
        }

        workoutFreq = spinnerWorkoutFreq.getSelectedItemPosition();
        goal = spinnerGoals.getSelectedItemPosition();
    }

    private boolean validateFields() {
        boolean valid = true;

        String validate_name = etxtName.getText().toString();
        if (TextUtils.isEmpty(validate_name)) {
            etxtName.setError("Required");
            valid = false;
        } else
            etxtName.setError(null);

        if (!verifyAge()) {
            etxtDateOfBirth.setError("Invalid Date");
            valid = false;
        }
        else {
            etxtDateOfBirth.setError(null);
        }

        String validate_weight = etxtWeight.getText().toString();
        if (TextUtils.isEmpty(validate_weight)) {
            etxtWeight.setError("Required");
            valid = false;
        } else if ( // Weight KG boundaries (30-225)
                (Float.parseFloat(validate_weight) < 30 ||
                        Float.parseFloat(validate_weight) > 225) &&
                        rbtnMetric.isChecked()) {
            etxtWeight.setError("Invalid measurement");
            valid = false;
        } else if ( // Weight LB boundaries (50-500)
                (Float.parseFloat(validate_weight) < 50 ||
                        Float.parseFloat(validate_weight) > 500) &&
                        !rbtnMetric.isChecked()) {
            etxtWeight.setError("Invalid measurement");
            valid = false;
        } else
            etxtWeight.setError(null);

        String validate_height1 = etxtHeightParam1.getText().toString();
        if (TextUtils.isEmpty((validate_height1))) {
            etxtHeightParam1.setError("Required");
            valid = false;
        } else if (   // Height cm boundaries (50-250)
                (Integer.parseInt(validate_height1) < 50 ||
                        Integer.parseInt(validate_height1) > 250) &&
                        rbtnMetric.isChecked()) {
            etxtHeightParam1.setError("Invalid measurement");
            valid = false;
        } else if (   // Height feet boundaries (3-8)
                (Integer.parseInt(validate_height1) < 3 ||
                        Integer.parseInt(validate_height1) > 8) &&
                        !rbtnMetric.isChecked()) {
            etxtHeightParam1.setError("Invalid measurement");
            valid = false;
        } else
            etxtHeightParam1.setError(null);

        if (!rbtnMetric.isChecked()) {
            String validate_height2 = etxtHeightParam2.getText().toString();
            if (TextUtils.isEmpty((validate_height2))) {
                etxtHeightParam2.setError("Required");
                valid = false;
            } else if (Integer.parseInt(validate_height2) > 12) { // Inch boundary 0-12
                etxtHeightParam2.setError("Invalid measurement");
                valid = false;
            } else
                etxtHeightParam2.setError(null);
        }
        return valid;
    }

    private boolean verifyAge(){
        String ageArr[] = etxtDateOfBirth.getText().toString().split("-");
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Integer.parseInt(ageArr[2]), Integer.parseInt(ageArr[1]), Integer.parseInt(ageArr[0]));
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            return false;
        return true;
    }

    private void unitSystemChange() {
        if(rbtnMetric.isChecked()) // Metric
        {
            linearlayoutHeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            linearlayoutWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));

            etxtWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            lblWeightUnit.setText("kg");

            etxtHeightParam1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblHeightUnit1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            lblHeightUnit1.setText("cm");

            etxtHeightParam2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
            lblHeightUnit2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));

            etxtHeightParam2.setVisibility(View.GONE);
            lblHeightUnit2.setVisibility(View.GONE);
        }
        else // Imperial
        {
            linearlayoutHeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.67f));
            linearlayoutWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.33f));

            etxtWeight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            lblWeightUnit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            lblWeightUnit.setText("lb");

            etxtHeightParam1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            lblHeightUnit1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
            lblHeightUnit1.setText("ft");

            etxtHeightParam2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            lblHeightUnit2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));

            etxtHeightParam2.setVisibility(View.VISIBLE);
            lblHeightUnit2.setVisibility(View.VISIBLE);
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(year,monthOfYear,dayOfMonth);
        String date = dateFormatter.format(cal.getTime());
        etxtDateOfBirth.setText(date);
        if(etxtDateOfBirth.getError() != null) {
            etxtDateOfBirth.setError(null);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_save_floppy):
                updateUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_save_settings, menu);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        profileChangesMade.setChanged(true);
        ((activitySettings) getActivity()).setChangesDetected(profileChangesMade.isChanged());
    }

    @Override public void afterTextChanged(Editable s) {}

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        profileChangesMade.setChanged(true);
        ((activitySettings) getActivity()).setChangesDetected(profileChangesMade.isChanged());
    }

    @Override public void onNothingSelected(AdapterView<?> parent) {}
}
