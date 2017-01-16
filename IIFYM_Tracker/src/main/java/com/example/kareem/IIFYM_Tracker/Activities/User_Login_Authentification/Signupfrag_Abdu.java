package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.kareem.IIFYM_Tracker.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Signupfrag_Abdu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Signupfrag_Abdu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Signupfrag_Abdu extends Fragment implements View.OnClickListener {
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
    EditText username;
    Button login,register,skip;





    //GUI vars ----------------------------------

    public Signupfrag_Abdu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Signupfrag_Abdu.
     */
    // TODO: Rename and change types and number of parameters
    public static Signupfrag_Abdu newInstance(String param1, String param2) {
        Signupfrag_Abdu fragment = new Signupfrag_Abdu();
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
        myView = inflater.inflate(R.layout.fragment_sublogin1, container, false);
        // Inflate the layout for this fragment
        username = (EditText)myView.findViewById(R.id.usernamefield);
        login = (Button)myView.findViewById(R.id.loginbtn);
        register = (Button)myView.findViewById(R.id.registerbtn);
        skip=(Button)myView.findViewById(R.id.skipbtn);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        skip.setOnClickListener(this);

        return myView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginbtn:
                if(validate(new EditText[]{username})) {
                    listener.userLogin(username.getText().toString());
                }
                else
                {
                    username.setError("Both fields Required");
                }
                break;
//            case R.id.registerbtn:
//                Log.d("DEBUG", "Register Button Clicked");
//                listener.userReg(username.getText().toString(),pass.getText().toString());
//                break;
            case R.id.skipbtn:
                listener.openHome();
                break;

        }

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
