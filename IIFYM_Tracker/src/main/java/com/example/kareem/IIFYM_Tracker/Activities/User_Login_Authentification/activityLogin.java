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

import com.example.kareem.IIFYM_Tracker.Activities.Main.MainActivity;
import com.example.kareem.IIFYM_Tracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activityLogin extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;
    private EditText etxtEmail;
    private EditText etxtPassword;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // User is signed in
                if (user != null) {
                    Log.d("User is Signed In", "onAuthStateChanged: signed_in:" + user.getUid());
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        // Email is verified
                        Log.d("Email is verified", "isEmailVerified: verified");
                        // Go to main activity
                        Context context = getApplicationContext();
                        Intent intent = new Intent();
                        intent.setClass(context, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Email is not verified
                        // Display that user needs to verify email
                        AlertDialog.Builder builder = new AlertDialog.Builder(activityLogin.this);
                        builder.setMessage( "An email has been sent to you.\n " +
                                            "Please verify your email in order to log in.")
                                            .setTitle("Email Verification");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    // User is signed out
                    Log.d("User is not Signed In", "onAuthStateChanged: signed_out");
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
        Log.d("Account created", "createAccount: account_created" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in Success
                        if(task.isSuccessful()) {
                            Log.d("User created:", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Auth Email", "Email sent.");
                                                showAlertDialog("Email Verification","An email has been sent to you.\n" +
                                                                "Please verify your email in order to log in.");
                                            }
                                            else {
                                                showAlertDialog("Oops!","There was an error sending the verification email.");
                                            }
                                        }
                                    });

                            // TODO: Create flag in Firebase under user name to indicate that registration is incomplete
                        }
                        // Sign in Failed
                        if (!task.isSuccessful()) {
                            showAlertDialog("User Registration Failed","Unable to create user.");
                        }
                        hideProgressDialog();
                    }
                });

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in Success
                        if(task.isSuccessful()) {
                            Log.d("tag", "signInWithEmail:onComplete:" + task.isSuccessful());
                        }
                        // Sign in Failed
                        if (!task.isSuccessful()) {
                            Log.w("tag", "signInWithEmail:failed", task.getException());
                            Log.w("Sign in with Email", "signInWithEmail: failed", task.getException());
                            showAlertDialog("Sign in failed","Unable to Sign In.");
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
            etxtEmail.setError("Required.");
            valid = false;
        } else {
            etxtEmail.setError(null);
        }

        String password = etxtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etxtPassword.setError("Required.");
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

    //Unused
    private void updateUI() {
        hideProgressDialog();
    }

    private void signOut() {
        mAuth.signOut();
    }
}
