package com.example.kareem.macrotracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.kareem.macrotracker.R;
import com.example.kareem.macrotracker.ViewComponents.InputFilterMinMax;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Goalsfrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Goalsfrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Goalsfrag extends Fragment implements  View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private myFragEventListener listener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    //GUI vars ----------------------------------
    View myView;
    Button finish,back;
    Spinner goal_spinner,workout_spinner;
    EditText pcarbs,pfat,pprotein;




    //GUI vars ----------------------------------

    public Goalsfrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Goalsfrag.
     */
    // TODO: Rename and change types and number of parameters
    public static Goalsfrag newInstance(String param1, String param2) {
        Goalsfrag fragment = new Goalsfrag();
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
        myView = inflater.inflate(R.layout.fragment_sublogin3, container, false);
        // Inflate the layout for this fragment

        pcarbs = (EditText)myView.findViewById(R.id.carbsfield);
        pfat = (EditText)myView.findViewById(R.id.fatfield);
        pprotein = (EditText)myView.findViewById(R.id.proteinfield);

        pcarbs.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        pfat.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});
        pprotein.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});

        goal_spinner= (Spinner)myView.findViewById(R.id.goalSpinner);
        workout_spinner=(Spinner)myView.findViewById(R.id.workout_freq_spinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.goals_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.workfreq_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        goal_spinner.setAdapter(adapter1);
        workout_spinner.setAdapter(adapter2);


        back = (Button)myView.findViewById(R.id.backbtn) ;
        finish=(Button)myView.findViewById(R.id.finishregBtn);
        finish.setOnClickListener(this);
        back.setOnClickListener(this);

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finishregBtn:
                //store data and finish reg
                if(validate(new EditText[]{pcarbs, pfat,pprotein}))
                {

                    listener.storeuserGoals(goal_spinner.getSelectedItemPosition(),Integer.parseInt(pcarbs.getText().toString()),Integer.parseInt(pfat.getText().toString()),Integer.parseInt(pprotein.getText().toString()),workout_spinner.getSelectedItemPosition());

                    listener.insertUser(); //inserts user to DB
                }
                else
                {

                    pcarbs.setError("All Fields Required");
                    pfat.setError("All Fields Required");
                    pprotein.setError("All Fields Required");
                }

                break;
            case R.id.backbtn:
                listener.switchFrag(1);
                break;
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

    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }
}
