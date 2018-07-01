package com.example.android.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.ProviderQueryResult;

/**
 * Created by SHOW on 6/28/2018.
 */

public class GoogleSignInActivity extends AppCompatActivity {

    Button signInBtn;
    Button signOutBtn;
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    TextView mRegister;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        signInBtn = findViewById(R.id.sign_in_button);
        mRegister = findViewById(R.id.register_link);
        mAuth = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // triggers signIn() when signInBtn is clicked
                signIn();
                //verifyUser();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent registerIntent = new Intent(GoogleSignInActivity.this, FireBaseRegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //startActivity(new Intent(GoogleSignInActivity.this, FireBaseRegisterActivity.class));

                }
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Configure Google Sign In
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(GoogleSignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Views
//        mStatusTextView = findViewById(R.id.status);
//        mDetailTextView = findViewById(R.id.detail);
//
//        // Button listeners
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
//        findViewById(R.id.sign_out_button).setOnClickListener(this);
//        findViewById(R.id.disconnect_button).setOnClickListener(this);
    }

    // Configure Google Sign In

//    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build();

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                //String email = account.toString().trim();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(GoogleSignInActivity.this, "Auth went wrong", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle2(String email) {

//        if (mGoogleApiClient.isConnected()) {
//            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            mAuth.fetchProvidersForEmail(email).addOnCompleteListener(
                    GoogleSignInActivity.this, new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (task.isSuccessful()) {
                                //Check to see if user is existing
                                ProviderQueryResult result = task.getResult();

                                if (result != null && result.getProviders() != null
                                        && result.getProviders().size() > 0) {

                                    //if user already exists, redirect user to sign in page
                                    Intent journalIntent = new Intent(GoogleSignInActivity.this, MainActivity.class);
                                    startActivity(journalIntent);

                                } else {
                                    //If user does not exist, then register user
                                    Toast.makeText(GoogleSignInActivity.this,
                                            "You need to register", Toast.LENGTH_SHORT).show();

                                    Intent registerIntent = new Intent(GoogleSignInActivity.this, FireBaseRegisterActivity.class);
                                    startActivity(registerIntent);

                                       }
                                } else {
                                    //failed while verifying user
                                    Toast.makeText(GoogleSignInActivity.this,
                                            "An error occurred while connecting, please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }

                        }
                    });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(GoogleSignInActivity .this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            Toast.makeText(GoogleSignInActivity.this,
                                    "You are logged in as: " + email,
                                    Toast.LENGTH_SHORT).show();
                            firebaseAuthWithGoogle2(email);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}


//    public void verifyUser() {
//
//        if (mGoogleApiClient.isConnected()) {
//            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//            mAuth.fetchProvidersForEmail(email).addOnCompleteListener(
//                    GoogleSignInActivity.this, new OnCompleteListener<ProviderQueryResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
//                            if (task.isSuccessful()) {
//                                //Check to see if user is existing
//                                ProviderQueryResult result = task.getResult();
//
//                                if (result != null && result.getProviders() != null
//                                        && result.getProviders().size() > 0) {
//
//                                    //if user already exists, redirect user to sign in page
//                                    Intent journalIntent = new Intent(GoogleSignInActivity.this, MainActivity.class);
//                                    startActivity(journalIntent);
//
//                                } else {
//                                    //If user does not exist, then register user
//                                    Toast.makeText(GoogleSignInActivity.this,
//                                            "You need to register", Toast.LENGTH_SHORT).show();
//
//                                    Intent registerIntent = new Intent(GoogleSignInActivity.this, FireBaseRegisterActivity.class);
//                                    startActivity(registerIntent);
//
//                                       }
//                                } else {
//                                    //failed while verifying user
//                                    Toast.makeText(GoogleSignInActivity.this,
//                                            "There is a problem, please try again.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//
//                        }
//                    });
//        }
//        Log.i("TAG", "I ran");
//        }

