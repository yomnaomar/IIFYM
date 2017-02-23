package com.example.kareem.IIFYM_Tracker.Activities.User_Login_Authentification;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.kareem.IIFYM_Tracker.Activities.Main.New.activityMain;
import com.example.kareem.IIFYM_Tracker.Models.User;
import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Database.SharedPreferenceHelper;
import com.example.kareem.IIFYM_Tracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activityLogin extends AppCompatActivity implements View.OnClickListener {
//    ------ Creating a New User ------
//    Firebase SetValue ->
//    {
//        "users" :
//        {
//            "UIO9XpMc3BR2sNTzNcdlDJ7rrtD3" :
//            {
//                "uid"         :   "UIO9XpMc3BR2sNTzNcdlDJ7rrtD3",
//                "email"       :   "example@gmail.com",
//                "registered"  :   true
//            }
//        }
//    }
//
//    ------ User Login ------
//    if(Authentication Successful)
//    {
//        if(Email Verified)
//        {
//            if(Is Registered)
//            {
//                myPrefs.addPreference("session_uid", uid);
//                Go to Main
//            }
//            else if (Not Registered)
//            {
//                intent.putExtra("uid", uid);
//                intent.putExtra("email", email);
//                Go to UserInfo
//            }
//        }
//        else if (Email is not Verified)
//        {
//            Send Verification Email
//        }
//    }

    // GUI
    private ProgressDialog  progressDialog;
    private EditText        etxtEmail, etxtPassword;

    // Variables
    private boolean         isRegistered;
    private Context         context;
    BroadcastReceiver       broadcast_reciever;

    // Database
    private SharedPreferenceHelper          myPrefs;
    private SQLiteConnector                 DB_SQLite;
    private FirebaseAuth                    firebaseAuth;
    private DatabaseReference               firebaseDbRef;
    private FirebaseAuth.AuthStateListener  firebaseAuthListener;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        DB_SQLite = new SQLiteConnector(context);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDbRef = FirebaseDatabase.getInstance().getReference();

        // Handles Signing in
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                // User is signed in
                if (firebaseUser != null) {

                    // Email verified
                    if (firebaseUser.isEmailVerified()) {
                        final String uid = firebaseUser.getUid();
                        final String email = firebaseUser.getEmail();
                        // Get User data to check if Registered
                        firebaseDbRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User userPost = dataSnapshot.getValue(User.class);
                                isRegistered = userPost.isRegistered();

                                // User is Registered
                                if (isRegistered) {
                                    // Create new User, does nothing if user already exists
                                    DB_SQLite.createUser(userPost);
                                    // Store user session in Preferences
                                    myPrefs.addPreference("session_uid", uid);

                                    // Go to activityMain
                                    Intent intent = new Intent();
                                    intent.setClass(context, activityMain.class);
                                    startActivity(intent);
                                    finish();
                                }

                                // User is not Registered
                                else {
                                    // Go to activityUserInfo
                                    Intent intent = new Intent();
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("email", email);
                                    intent.setClass(context, activityUserInfo.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    // Email not verified
                    else {
                        showAlertDialog("Email Verification", "Please verify your email before continuing");
                    }
                }
            }
        };

        // GUI
        setContentView(R.layout.activity_login);
        // Views
        etxtEmail = (EditText) findViewById(R.id.email_edittext);
        etxtPassword = (EditText) findViewById(R.id.password_textview);

        // Buttons
        findViewById(R.id.Button_Login).setOnClickListener(this);
        findViewById(R.id.Button_Register).setOnClickListener(this);
    }

    private void createAccount(String email, String password) {
        if (!validateForms()) {
            return;
        }
        showProgressDialog();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // User Created Success
                        if (task.isSuccessful()) {

                            //Save UID, Email and isRegistered to Firebase
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String newUID = firebaseUser.getUid();
                            String newEmail = firebaseUser.getEmail();
                            User user = new User(newUID, newEmail, false);
                            firebaseDbRef.child("users").child(newUID).setValue(user);

                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Email verification sent to user
                                                signOut();
                                            }
                                            else {
                                                showAlertDialog("Oops!","There was an error sending the verification email");
                                            }
                                        }
                                    });
                        }
                        //  Failed to Create User
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException E) {
                                showAlertDialog("Failed to create user", "User already exists");
                            } catch (FirebaseAuthWeakPasswordException E) {
                                showAlertDialog("Failed to create user", "Password should be at least 6 characters");
                            } catch (FirebaseNetworkException E) {
                                showAlertDialog("Failed to create user", "Network connection not found");
                            }catch (Exception E) {
                                Log.e("createUser", E.getMessage());
                            }
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!validateForms())
            return;

        showProgressDialog();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Sign in Success
                        if (task.isSuccessful()) {
                            // onAuthStateChanged will be called
                        }
                        //Sign in Fail
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException E) {
                                showAlertDialog("Failed to sign in", "Invalid username or password");
                            } catch (FirebaseAuthInvalidUserException E) {
                                showAlertDialog("Failed to sign in", "User does not exist");
                            } catch (FirebaseNetworkException E) {
                                showAlertDialog("Failed to sign in", "Network connection not found");
                            } catch (Exception e) {
                                Log.e("createUser", e.getMessage());
                            }
                        }
                        hideProgressDialog();
                    }
                });
    }

    @Override public void onStop(){
        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
        hideProgressDialog();
        super.onStop();
    }

    @Override public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcast_reciever);
        myPrefs.addPreference("temp_email_login",etxtEmail.getText().toString());
    }

    @Override protected void onResume() {
        super.onResume();
        broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));
        etxtEmail.setText(myPrefs.getStringValue("temp_email_login"));
    }

    @Override public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.Button_Register) {
            createAccount(etxtEmail.getText().toString().trim(), etxtPassword.getText().toString().trim());
        } else if (i == R.id.Button_Login) {
            signIn(etxtEmail.getText().toString().trim(), etxtPassword.getText().toString().trim());
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

    private boolean validateForms() {
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
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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

    private void signOut() {
        Log.d("UserInfo","Signed out");
        firebaseAuth.signOut();
    }
}
