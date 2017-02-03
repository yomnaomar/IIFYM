package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.kareem.IIFYM_Tracker.Activities.Main.activityMain;
import com.example.kareem.IIFYM_Tracker.Custom_Objects.User;
import com.example.kareem.IIFYM_Tracker.Database.DatabaseConnector;
import com.example.kareem.IIFYM_Tracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activityLogin extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;
    private EditText etxtEmail;
    private EditText etxtPassword;
    private SharedPreferences myPrefs;
    private DatabaseConnector My_DB;
    private boolean isRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        My_DB = new DatabaseConnector(getApplicationContext());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseuser = firebaseAuth.getCurrentUser();

                // User is signed in
                if (firebaseuser != null) {
                    final String uid = firebaseuser.getUid();
                    String email = firebaseuser.getEmail();
                    Log.d("onAuthStateChanged", "User with UID " + uid + " signed_in: " + firebaseuser.getUid());

                    // Email is verified
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        Log.d("onAuthStateChanged", "User with UID " + uid + " email is verified");

                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User userPost = dataSnapshot.getValue(User.class);
                                isRegistered = userPost.isRegistered();
                                if (isRegistered)
                                    Log.d("onAuthStateChanged", "User with UID " + uid + " is Registered");
                                else
                                    Log.d("onAuthStateChanged", "User with UID " + uid + " is not Registered");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if(isRegistered) {
                            // Go to main activity
                            Context context = getApplicationContext();
                            Intent intent = new Intent();
                            intent.setClass(context, activityMain.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Context context = getApplicationContext();
                            Intent intent = new Intent();
                            intent.putExtra("uid",uid);
                            intent.putExtra("email",email);
                            intent.setClass(context, activityUserInfo.class);
                            startActivity(intent);
                            finish();
                        }


                    } else {
                        // Email is not verified
                        // Display that user needs to verify email
                        Log.d("onAuthStateChanged", "User with UID " + uid + " email is not verified");
                        showAlertDialog("Email Verification","An email has been sent to you.\n " +
                                            "Please verify your email in order to log in.");
                    }
                } else {
                    // User is signed out
                    Log.d("onAuthStateChanged", "onAuthStateChanged: signed_out");
                }
            }
        };

        // Views
        etxtEmail = (EditText) findViewById(R.id.email_edittext);
        etxtPassword = (EditText) findViewById(R.id.password_textview);

        // Buttons
        findViewById(R.id.Button_Login).setOnClickListener(this);
        findViewById(R.id.Button_Register).setOnClickListener(this);

        // Prefs
        myPrefs = getPreferences(AppCompatActivity.MODE_PRIVATE);
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // User Created Success
                        if(task.isSuccessful()) {
                            Log.d("User created:", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            //Save UID, Email and isRegistered to Firebase and SQLite
                            FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
                            String newUID = firebaseuser.getUid();
                            String newEmail = firebaseuser.getEmail();
                            User user = new User(newUID,newEmail,false);
                            mDatabase.child("users").child(newUID).setValue(user);
                            My_DB.createUser(user);

                            firebaseuser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Auth Email", "Email sent.");
                                                /*showAlertDialog("Email Verification","An email has been sent to you.\n" +
                                                                "Please verify your email in order to log in.");*/
                                            }
                                            else {
                                                showAlertDialog("Oops!","There was an error sending the verification email.");
                                            }
                                        }
                                    });
                        }
                        // User Create Failed
                        if (!task.isSuccessful()) {
                            showAlertDialog("User Registration Failed","Unable to create user.");
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d("User pressed Sign In", "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Sign in Success
                        if(task.isSuccessful()) {
                            Log.d("Sign in with Email", "signInWithEmail: onComplete:" + task.isSuccessful());
                        }
                        //Sign in Fail
                        if (!task.isSuccessful()) {
                            Log.w("Sign in with Email", "signInWithEmail: failed", task.getException());
                            showAlertDialog("Sign in failed", "Unable to sign in.");
                        }
                        hideProgressDialog();
                    }
                });
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        hideProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("temp_email",etxtEmail.getText().toString());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        etxtEmail.setText(myPrefs.getString("temp_email",""));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.Button_Register) {
            createAccount(etxtEmail.getText().toString(), etxtPassword.getText().toString());
        } else if (i == R.id.Button_Login) {
            signIn(etxtEmail.getText().toString(), etxtPassword.getText().toString());
        }
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activityLogin.this);
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

    private boolean validateForm() {
        boolean valid = true;

        String email = etxtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etxtEmail.setError("Required");
            valid = false;
        } else {
            etxtEmail.setError(null);
        }

        String password = etxtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etxtPassword.setError("Required");
            valid = false;
        } else {
            etxtPassword.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
