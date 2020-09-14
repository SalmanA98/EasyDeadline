package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //Declaring variables to access the user input
    private EditText mailID;
    private EditText password;
    private Button loginButton;
    private TextView errorMessage;
    private ProgressBar progressBar;

    //String to use for Email/Password authentication
    private String mailString;
    private String pwdString;


    //Firebase authentication instance
    private FirebaseAuth firebaseAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Turning night mode off in this activity
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);


            //Assigning values to the variables
            //Needed to the design components using findViewByID
            mailID = (EditText) findViewById(R.id.emailView);
            password = (EditText) findViewById(R.id.passwordView);
            loginButton = findViewById(R.id.btView);
            errorMessage = (TextView) findViewById(R.id.erMsgView);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            firebaseAuth = FirebaseAuth.getInstance();








        //Do the following on sign in button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting the mail ID and converting it to lower case annd trimming the trailing spaces
                mailString = mailID.getText().toString();
                mailString = mailString.toLowerCase();
                mailString = mailString.trim();

                //Getting the entered password
                pwdString  = password.getText().toString();


                //If mail ID is empty
                if(TextUtils.isEmpty(mailString)){

                    errorMessage.setText("");
                    errorMessage.setText("Please Enter a Mail ID");
                }

                //If password is empty
                else if(TextUtils.isEmpty(pwdString)){

                    errorMessage.setText("");
                    errorMessage.setText("Please Enter the Password");
                }

                //If both are entered
                else {

                    //Progress bar is shown until the login is complete
                    progressBar.setVisibility(View.VISIBLE);
                    errorMessage.setText("");

                        //Authenticating using firebase auth with the mail and password
                        firebaseAuth.signInWithEmailAndPassword(mailString, pwdString)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            // Sign in success, go to loggedInActivity
                                            Intent loggedInIntnet = new Intent(getBaseContext(), loggedInActivity.class);
                                            startActivity(loggedInIntnet);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            errorMessage.setText("Login Failed, Try Agian");
                                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                }

            }
        });




    }



    //If the user clicks back
    @Override
    public void onBackPressed()
    {
        //Close the app and set flag activity new task to start it as a new task
        Intent CloseIntent = new Intent(Intent.ACTION_MAIN);
        CloseIntent.addCategory(Intent.CATEGORY_HOME);
        CloseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(CloseIntent);
    }

    }
