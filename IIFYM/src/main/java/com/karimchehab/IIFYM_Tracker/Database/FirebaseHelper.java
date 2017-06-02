package com.karimchehab.IIFYM_Tracker.Database;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kareem on 16-May-17.
 */

public class FirebaseHelper {

    // Database
    private FirebaseAuth                    firebaseAuth;
    private DatabaseReference               firebaseDbRef;
    private FirebaseAuth.AuthStateListener  firebaseAuthListener;

    private GoogleApiClient                 mGoogleApiClient;
    private static final int                RC_SIGN_IN = 9001;

    // Variables
    private Context     context;
    private boolean     isRegistered;

    public FirebaseHelper(Context context) {
        this.context = context;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDbRef = FirebaseDatabase.getInstance().getReference();

        //setupGoogleSignIn();

        //setupFirebaseAuthListener();
    }

    /**
     ------ Creating a New User ------
     Firebase SetValue ->
     {
     "users" :
     {
     "UIO9XpMc3BR2sNTzNcdlDJ7rrtD3" :
     {
     "uid"         :   "UIO9XpMc3BR2sNTzNcdlDJ7rrtD3",
     "email"       :   "example@gmail.com",
     "registered"  :   true
     }
     }
     }

     ------ User Login ------
     if(Authentication Successful)
     {
     if(Is Registered)
     {
     myPrefs.addPreference("session_uid", uid);
     Go to Main
     }
     else if (Not Registered)
     {
     intent.putExtra("uid", uid);
     intent.putExtra("email", email);
     Go to UserInfo
     }
     }
     else if (Email is not Verified)
     {
     Send Verification Email
     }
     }
     */
    /*private void setupFirebaseAuthListener() {
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

                        @Override public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        };
    }*/
}
