package com.karimchehab.IIFYM.Activities.Authentication;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karimchehab.IIFYM.Activities.Application.ActivityHome;
import com.karimchehab.IIFYM.Database.Credentials;
import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Database.SharedPreferenceHelper;
import com.karimchehab.IIFYM.Models.User;
import com.karimchehab.IIFYM.R;


public class ActivitySelectAuthentication extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    // GUI
    private View            loginLinearLayout;
    private ProgressDialog  progressDialog;
    TextView                lblPrivacyPolicyLink;

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
        setContentView(R.layout.activity_select_authentication);

        // Views
        loginLinearLayout = findViewById(R.id.loginLinearLayout);

        // Buttons
        findViewById(R.id.btnEmailLoginIn).setOnClickListener(this);
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(this);

        // Privacy Policy
        lblPrivacyPolicyLink = (TextView) findViewById(R.id.lblPrivacyPolicyLink);
        lblPrivacyPolicyLink.setOnClickListener(this);
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

        setupGoogleSignIn();
        setupFirebaseAuthListener();
    }

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

                                    // Go to ActivityHome
                                    Intent intent = new Intent();
                                    intent.setClass(context, ActivityHome.class);
                                    startActivity(intent);
                                    finish();
                                }

                                // User is not Registered
                                else {
                                    // Go to ActivityRegisterProfile
                                    Intent intent = new Intent();
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("email", email);
                                    intent.setClass(context, ActivityRegisterProfile.class);
                                    startActivity(intent);
                                }
                            }
                            else {
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
                                                }
                                                else {
                                                    showAlertDialog("Oops!","There was an error sending the verification email");
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.btnEmailLoginIn:
                Intent emailIntent = new Intent();
                emailIntent.setClass(context, ActivityLoginEmail.class);
                startActivity(emailIntent);
                break;
            case R.id.btnGoogleSignIn:
                signInGoogle();
                break;
            case R.id.lblPrivacyPolicyLink:
                Intent privacypolicyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://karim-chehab.weebly.com/iifym-privacy-policy.html"));
                startActivity(privacypolicyIntent);
                break;
        }
    }

    // -------------------------------- GOOGLE -----------------------------------------------------
    private void setupGoogleSignIn() {
        Log.d("setupGoogleSignIn", "Setting up Google Sign In");

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Credentials.IIFYM_SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Log.d("setupGoogleSignIn", "Google Sign In set up complete");
    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        Log.d("signInGoogle", "Google sign in Intent");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.d("onActivityResult", "Success");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(ActivitySelectAuthentication.this,
                    "Google sign in failed.",
                    Toast.LENGTH_SHORT)
                        .show();
                Log.d("onActivityResult", "Failed " + result.getStatus().toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("firebaseAuthWithGoogle", "Account ID " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInWithCredential", "Success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.updateEmail(acct.getEmail());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signInWithCredential", "Failed", task.getException());
                            Toast.makeText(ActivitySelectAuthentication.this, "Firebase authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ---------------------------------------------------------------------------------------------

    private void signOut() {
        Log.d("UserInfo", "Signed out");
        firebaseAuth.signOut();
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySelectAuthentication.this);
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
        Toast.makeText(ActivitySelectAuthentication.this, "Connection failed.",
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
