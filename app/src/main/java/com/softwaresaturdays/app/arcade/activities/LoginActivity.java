package com.softwaresaturdays.app.arcade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.softwaresaturdays.app.arcade.MyApplication;
import com.softwaresaturdays.app.arcade.R;
import com.softwaresaturdays.app.arcade.models.User;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;
import com.softwaresaturdays.app.arcade.utilities.Util;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 2;
    public static final String TAG = "LOGIN_ACTIVITY";
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mSignIn;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("29323717902-ja0lnf82mmg2oljjba6f8tuimbfo660s.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignIn = findViewById(R.id.sign_in_button);
        mSignIn.setSize(SignInButton.SIZE_WIDE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        MyApplication.isForeground = true;

        DatabaseHelper.getAllUsersInfo(new DatabaseHelper.OnUserInfoFetchListener() {
            @Override
            public void onUserInfoFetched(User user) {

            }

            @Override
            public void onAllUsersInfoFetched(ArrayList<User> allUsers) {
                Util.storeUserData(allUsers, LoginActivity.this);
            }
        });

        if (account == null) {
            mSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        } else {
            // Assign global user object
            updateUserInfo(mAuth.getCurrentUser());

            // User already signed in, go to chat activity
            startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Signed in successfully
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.w(TAG, "SUCCESSFUL SIGN IN EMAIL: " + account.getEmail());
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            updateUserInfo(mAuth.getCurrentUser());

                            // Ready to go to chat activity
                            startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.sign_in_button), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserInfo(FirebaseUser user) {
        assert user != null;

        User newUser = new User(user.getEmail(), user.getDisplayName(), user.getPhotoUrl(), user.getUid());
        MyApplication.currUser = newUser;
        if (MyApplication.fcmToken != null && !MyApplication.fcmToken.isEmpty()) {
            MyApplication.currUser.setFcmToken(MyApplication.fcmToken);
        }
        // Add user info on Database
        DatabaseHelper.uploadUserInfo(newUser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.isForeground = false;
    }
}
