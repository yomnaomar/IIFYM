package com.karimchehab.IIFYM.Activities.UserLoginAuthentication;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karimchehab.IIFYM.Activities.Main.activityHome;
import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.Models.User;
import com.karimchehab.IIFYM.R;

public class activityLogin2 extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    // GUI
    private View loginLinearLayout;
    private ProgressDialog progressDialog;
    private EditText etxtEmail, etxtPassword;

    // Variables
    private boolean isRegistered;
    private Context context;
    BroadcastReceiver broadcast_reciever;

    // Database
    private SharedPreferenceHelper myPrefs;
    private SQLiteConnector DB_SQLite;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDbRef;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGUI();
        initializeConnections();
    }

    private void initializeGUI() {
        // GUI
        setContentView(R.layout.activity_login_2);

        // Views
        loginLinearLayout = findViewById(R.id.loginLinearLayout);
        etxtEmail = (EditText) findViewById(R.id.email_edittext);
        etxtPassword = (EditText) findViewById(R.id.password_textview);

        // Buttons
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.txtRegister).setOnClickListener(this);
        findViewById(R.id.txtForgotPassword).setOnClickListener(this);
    }

    /**
     * Initializes database connections:
     * 1. SharedPreferences
     * 2. SQLite
     * 3. FirebaseAuth - UserPass
     * 4. FirebaseAuth - Google
     */
    private void initializeConnections() {
        context = getApplicationContext();
        myPrefs = new SharedPreferenceHelper(context);
        DB_SQLite = new SQLiteConnector(context);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDbRef = FirebaseDatabase.getInstance().getReference();

        setupFirebaseAuthListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.btnLogin:
                signInUserPass(etxtEmail.getText().toString().trim(), etxtPassword.getText().toString().trim());
                break;
            case R.id.txtRegister:
                createAccount(etxtEmail.getText().toString().trim(), etxtPassword.getText().toString().trim());
                break;
            case R.id.txtForgotPassword:
                if (isNetworkStatusAvialable(getApplicationContext())) {
                    final String email = etxtEmail.getText().toString().trim();
                    handleForgotPasswordButton(email);
                } else {
                    showAlertDialog("No Internet Connection", "Please check your internet connection and try again.");
                }
                break;
        }
    }

    /**
     * ------ Creating a New User ------
     * Firebase SetValue ->
     * {
     * "users" :
     * {
     * "UIO9XpMc3BR2sNTzNcdlDJ7rrtD3" :
     * {
     * "uid"         :   "UIO9XpMc3BR2sNTzNcdlDJ7rrtD3",
     * "email"       :   "example@gmail.com",
     * "registered"  :   true
     * }
     * }
     * }
     * <p>
     * ------ User Login ------
     * if(Authentication Successful)
     * {
     * if(Is Registered)
     * {
     * myPrefs.addPreference("session_uid", uid);
     * Go to Main
     * }
     * else if (Not Registered)
     * {
     * intent.putExtra("uid", uid);
     * intent.putExtra("email", email);
     * Go to UserInfo
     * }
     * }
     * else if (Email is not Verified)
     * {
     * Send Verification Email
     * }
     * }
     */
    private void setupFirebaseAuthListener() {
        // Handles Signing in
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                // User is signed in
                if (firebaseUser != null) {

                    final String uid = firebaseUser.getUid();
                    final String email = firebaseUser.getEmail();

                    // Get User data to check if Registered
                    firebaseDbRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Log.d("onDataChange", "Retrieving User");

                            User userPost = dataSnapshot.getValue(User.class);

                            if (userPost != null) {
                                isRegistered = userPost.getIsRegistered();
                                Log.d("onDataChange", "User " + userPost);

                                // User is Registered
                                if (isRegistered) {
                                    // Create new User, does nothing if user already exists
                                    DB_SQLite.createUser(userPost);
                                    // Store user session in Preferences
                                    myPrefs.addPreference("session_uid", uid);

                                    // Go to activityHome
                                    Intent intent = new Intent();
                                    intent.setClass(context, activityHome.class);
                                    startActivity(intent);
                                    finish();
                                }

                                // User is not Registered
                                else {
                                    // Go to activityRegisterProfile
                                    Intent intent = new Intent();
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("email", email);
                                    intent.setClass(context, activityRegisterProfile.class);
                                    startActivity(intent);
                                }
                            } else {
                                Log.d("onDataChange", "User is null, creating user");

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
                                                } else {
                                                    showAlertDialog("Oops!", "There was an error sending the verification email");
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    // --------------------------------- USER PASS -------------------------------------------------

    private void createAccount(String email, String password) {
        if (!validateForms()) {
            return;
        }
        showProgressDialog();

        Log.d("createAccount", "Creating Firebase account");

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
                                            } else {
                                                showAlertDialog("Oops!", "There was an error sending the verification email");
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
                            } catch (Exception E) {
                                Log.e("createUser", E.getMessage());
                            }
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void sendForgotPasswordEmail(final String email) {
        showProgressDialog();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(
                                    loginLinearLayout,
                                    "Email has been delivered to " + email,
                                    Snackbar.LENGTH_LONG
                            );
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar.make(
                                    loginLinearLayout,
                                    "Something went wrong, we could not send the email to " + email,
                                    Snackbar.LENGTH_LONG
                            );
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            snackbar.show();
                        }
                    }
                });
    }

    private void signInUserPass(String email, String password) {
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
                            } catch (FirebaseNetworkException E) {
                                showAlertDialog("Failed to sign in", "Network connection not found");

                            } catch (FirebaseAuthInvalidUserException E) {
                                showAlertDialog("Failed to sign in", "User does not exist");

                            } catch (FirebaseAuthInvalidCredentialsException E) {
                                // TODO if(getProvider() != Email), check his providers, and tell him his options
                                // TODO else -his provider is email but he typed wrong password-
                                showAlertDialog("Failed to sign in", "Invalid username or password");

                            } catch (Exception e) {
                                Log.e("createUser", e.getMessage());
                            }
                        }
                        hideProgressDialog();
                    }
                });
    }

    /**
     * Sends mail to a given `email` prompting to reset password.
     *
     * @param email Email address to send mail to.
     */
    private void handleForgotPasswordButton(final String email) {
        if (!validateEmail()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setMessage("Would you like to send an email to reset your password?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendForgotPasswordEmail(email);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    // ---------------------------------------------------------------------------------------------

    private void signOut() {
        Log.d("UserInfo", "Signed out");
        firebaseAuth.signOut();
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityLogin2.this);
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
        return validateEmail() && validatePassword();
    }

    /**
     * Returns whether or not the email address field is valid.
     * <p>
     * If invalid, it also notifies the user visually.
     *
     * @return
     */
    private boolean validateEmail() {
        String email = etxtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etxtEmail.setError("Required");
            return false;
        } else {
            etxtEmail.setError(null);
            return true;
        }
    }

    /**
     * Returns whether or not the password field is valid.
     * <p>
     * If invalid, it also notifies the user visually.
     *
     * @return
     */
    private boolean validatePassword() {
        String password = etxtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etxtPassword.setError("Required");
            return false;
        } else {
            etxtPassword.setError(null);
            return true;
        }
    }

    @Override
    public void onStop() {
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
        hideProgressDialog();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myPrefs.addPreference("temp_email_login", etxtEmail.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcast_reciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    unregisterReceiver(this);
                    finish();
                }
            }
        };
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));
        etxtEmail.setText(myPrefs.getStringValue("temp_email_login"));
    }

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(activityLogin2.this, "Connection failed.",
                Toast.LENGTH_SHORT).show();
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
}
