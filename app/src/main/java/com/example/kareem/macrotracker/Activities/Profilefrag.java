package com.example.kareem.macrotracker.Activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kareem.macrotracker.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Profilefrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Profilefrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profilefrag extends Fragment implements View.OnClickListener {
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
    Button next;
    RadioGroup radiogrp;
    Spinner weight_spinner,height_spinner;




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

        weight_spinner = (Spinner)myView.findViewById(R.id.unit_weight);
        height_spinner= (Spinner)myView.findViewById(R.id.unit_height);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.weight_units_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.height_units_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        weight_spinner.setAdapter(adapter1);
        height_spinner.setAdapter(adapter2);

        radiogrp = (RadioGroup)myView.findViewById(R.id.genderRadio);

        next = (Button)myView.findViewById(R.id.nextbtn);
        next.setOnClickListener(this);

        //TODO(Abdulwahab): get text field data and send to newUser in Login Activity




        return myView;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextbtn:
                //storedata then switch fragment
                listener.switchFrag(2);
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
}
