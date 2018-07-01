package com.example.android.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by SHOW on 6/30/2018.
 */

public class SignOutActivity extends AppCompatActivity {

    Button signOutBtn;
    TextView registerBtn;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user1;


    @Override
    protected void onStart() {
        super.onStart();

        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        signOutBtn = findViewById(R.id.sign_out_button);
        registerBtn = findViewById(R.id.register2);




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user1= firebaseAuth.getCurrentUser();
                String email = user1.getEmail();
                if (email == null) {
                    startActivity(new Intent(SignOutActivity.this, GoogleSignInActivity.class));
                }
            }
        };

        signOutBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // signs out the current user.
                //mAuth.addAuthStateListener(mAuthListener);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SignOutActivity.this, GoogleSignInActivity.class));
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // signs out the current user.
                startActivity(new Intent(SignOutActivity.this, FireBaseRegisterActivity.class));
            }
        });



    }
}
