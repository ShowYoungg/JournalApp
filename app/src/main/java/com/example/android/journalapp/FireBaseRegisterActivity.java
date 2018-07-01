package com.example.android.journalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

/**
 * Created by SHOW on 6/29/2018.
 */

public class FireBaseRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterBtn;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    //getting email and password from edit texts
    //String email = mEmailField.getText().toString().trim();
    //String password  = mPasswordField.getText().toString().trim();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_register);

        mAuth = FirebaseAuth.getInstance();

        //initializing views
        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);

        mRegisterBtn = (Button) findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);

        //attaching listener to signInBtn
        mRegisterBtn.setOnClickListener(this);


    }

    private void registerUser() {
        String email = mEmailField.getText().toString().trim();
        String password  = mPasswordField.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(FireBaseRegisterActivity.this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(FireBaseRegisterActivity.this,"Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(FireBaseRegisterActivity.this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {

                  //checking if successful
                  if (task.isSuccessful()) {
                      Toast.makeText(FireBaseRegisterActivity.this,"Successfully registered, please sign in",Toast.LENGTH_LONG).show();
                      Intent journalIntent = new Intent(FireBaseRegisterActivity.this, GoogleSignInActivity.class);
                      startActivity(journalIntent);

                  }
                progressDialog.dismiss();
              }
        });
    }

    @Override
    public void onClick(View v) {
        verifyUser();
        registerUser();
        //startActivity(new Intent(FireBaseRegisterActivity.this, MainActivity.class));
    }


    public void verifyUser() {
        String email = mEmailField.getText().toString().trim();
        String password  = mPasswordField.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            return;
        }

        if(TextUtils.isEmpty(password)){
            return;
        }

        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(
                FireBaseRegisterActivity.this, new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if (task.isSuccessful()) {
                            //Check to see if user is existing
                            ProviderQueryResult result = task.getResult();

                            if (result != null && result.getProviders() != null
                                    && result.getProviders().size() > 0) {

                                //if user already exists, redirect user to sign in page
                                Toast.makeText(FireBaseRegisterActivity.this,
                                        "User Already exist, please sign in",
                                        Toast.LENGTH_SHORT).show();

                                Intent signInIntent = new Intent(FireBaseRegisterActivity.this, MainActivity.class);
                                startActivity(signInIntent);

                            } else {
                                //If user does not exist, then register user
                                registerUser();
                            }
                        } else {
                            //failed while verifying user
                            Toast.makeText(FireBaseRegisterActivity.this,
                                    "There is a problem, please try again.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}


