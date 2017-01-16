package com.example.kareem.IIFYM_Tracker.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.kareem.IIFYM_Tracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profilefrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profilefrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profilefrag extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RadioButton radioButton;
    private myFragEventListener listener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //GUI vars ----------------------------------
    View myView;
    EditText fname,lname,dob,weight,height,email;
    Button next,cancel;
    RadioGroup radiogrp;
    RadioButton btn1,btn2;
    Spinner weight_unit_spinner, height_unit_spinner;
    int age;

    private DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormat;


    //GUI vars ----------------------------------

    public Profilefrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profilefrag.
     */
    // TODO: Rename and change types and number of parameters
    public static Profilefrag newInstance(String param1, String param2) {
        Profilefrag fragment = new Profilefrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_sublogin2, container, false);
        // Inflate the layout for this fragment

        fname = (EditText)myView.findViewById(R.id.fnamefield);
        lname = (EditText)myView.findViewById(R.id.lnamefield);
        dob = (EditText)myView.findViewById(R.id.dobfield);
        weight = (EditText)myView.findViewById(R.id.weightfield);
        height=(EditText)myView.findViewById(R.id.heightfield);
        email = (EditText)myView.findViewById(R.id.emailfield);
        btn1 = (RadioButton)myView.findViewById(R.id.radioButton);
        btn2 = (RadioButton)myView.findViewById(R.id.radioButton2);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dob.setOnFocusChangeListener(this);
        setDateTimeField();


        weight_unit_spinner = (Spinner)myView.findViewById(R.id.unit_weight);
        height_unit_spinner = (Spinner)myView.findViewById(R.id.unit_height);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.bodyweight_units_array, R.layout.spinner_row);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.height_units_array, R.layout.spinner_row);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        weight_unit_spinner.setAdapter(adapter1);
        height_unit_spinner.setAdapter(adapter2);

        height_unit_spinner.setSelection(1);


        radiogrp = (RadioGroup)myView.findViewById(R.id.genderRadio);

        next = (Button)myView.findViewById(R.id.nextbtn);
        cancel=(Button)myView.findViewById(R.id.cancelBtn);
        next.setOnClickListener(this);
        cancel.setOnClickListener(this);

        //TODO(Abdulwahab): get text field data and send to newUser in Login Activity




        return myView;
    }
    private void setDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @TargetApi(Build.VERSION_CODES.N)
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dob.setText(dateFormat.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof myFragEventListener) {
            listener = (myFragEventListener) activity;
        } else {
            Log.d("FragmentEvent","Activity not attached to fragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextbtn:
                //storedata then switch fragment - check if all field are filled
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    java.util.Date d = sdf.parse(dob.getText().toString());
                    int currentyear = Calendar.getInstance().get(Calendar.YEAR);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(d);
                    age = currentyear - calendar.get(Calendar.YEAR); //TODO: fix age calculation

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean fieldsOk = validate(new EditText[]{fname, lname, email,dob,weight,height});
                if(fieldsOk)
                {
                    listener.storeuserProfile(fname.getText().toString(),lname.getText().toString(),dob.getText().toString(),email.getText().toString(),getGender(),Float.parseFloat(weight.getText().toString()),Float.parseFloat(height.getText().toString()),age,weight_unit_spinner.getSelectedItemPosition(),height_unit_spinner.getSelectedItemPosition());
                    listener.switchFrag(2);
                }
                else
                {
                    fname.setError("All Fields Required");
                    lname.setError("All Fields Required");
                    email.setError("All Fields Required");
                    dob.setError("All Fields Required");
                    weight.setError("All Fields Required");
                    height.setError("All Fields Required");
                }
                break;
            case R.id.cancelBtn:
                listener.switchFrag(0);
                break;
        }

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch(view.getId())
        {
            case R.id.dobfield:

                if(b)
                {
                    datePickerDialog.show();
                    dob.clearFocus();
                }

                break;

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private String getGender() //get gender from radiogroup
    {
        int selectedId = radiogrp.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButton = (RadioButton) myView.findViewById(selectedId);
        return radioButton.getText().toString();
    }

    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0 || (!btn1.isChecked() && !btn2.isChecked())){
                return false;
            }
        }
        return true;
    }
}
