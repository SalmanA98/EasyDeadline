package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class loggedInActivity extends AppCompatActivity {

    //Declaring variable needed to access the design components
    TextView edName;
    TextView edAge;
    TextView edCourse;
    TextView edHeading;
    TextView edYear;
    ProgressBar LoggedInprogressBar;
    Button signOutBT;
    Button deadlinesViewbt;
    Button timetableViewbt;
    CharSequence loggedOutMsg;


    //Declaring ariables needed to process the data
    String name;
    String age;
    String Course;
    String heading[];
    String edMail;
    String year;

    //Declaring firebase user, firebase authentication and database reference instances
    DatabaseReference myRef;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        //Setting dark mode for the activity
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        //Initializing the design components variables
        edName = (TextView) findViewById(R.id.nameView);
        edAge = (TextView) findViewById(R.id.ageView);
        edCourse = (TextView) findViewById(R.id.courseView);
        edHeading = (TextView) findViewById(R.id.headingView);
        edYear = (TextView) findViewById(R.id.year);
        deadlinesViewbt = (Button) findViewById(R.id.deadlinesViewButton);
        timetableViewbt = (Button) findViewById(R.id.timetableViewButton);
        LoggedInprogressBar = (ProgressBar) findViewById(R.id.progressBarLoggedIn);
        signOutBT = (Button) findViewById(R.id.signOutView);

        //Initializing the authentication and user instances with getIntance and getCurrent user to get the instance and user currently logged in
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();






        //Retreiving mail of current user
        edMail = firebaseUser.getEmail();

        //Replacing characters to match database rules
        edMail = edMail.replace("@","x");
        edMail = edMail.replace(".", "p");


        //Using the mail ID to get the course they have enrolled in using database
        myRef = FirebaseDatabase.getInstance().getReference().child("Students").child(edMail);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Checking if the logged in user has data in the database
                if(dataSnapshot.child("Name").exists()) {
                    name = dataSnapshot.child("Name").getValue().toString();
                    age = dataSnapshot.child("Age").getValue().toString();
                    Course = dataSnapshot.child("Course").getValue().toString();
                    year = dataSnapshot.child("Year").getValue().toString();
                    heading = name.split(" ");

                    edName.setText("Full Name: " + name);
                    edAge.setText("Your Age: " + age);
                    edCourse.setText("Your Course: " + Course);
                    edHeading.setText("Welcome back " + heading[0] + "!");
                    edYear.setText("Year: "+year);

                    timetableViewbt.setVisibility(View.VISIBLE);
                    deadlinesViewbt.setVisibility(View.VISIBLE);
                    signOutBT.setVisibility(View.VISIBLE);

                    LoggedInprogressBar.setVisibility(View.GONE);
                }

                //If they do not have data in database
                else{
                    edHeading.setText("Sorry your data has not been uploaded/updated");
                    LoggedInprogressBar.setVisibility(View.GONE);
                    deadlinesViewbt.setVisibility(View.GONE);
                    timetableViewbt.setVisibility(View.GONE);
                    edName.setText("Please try again later.");
                    edAge.setText("");
                    edCourse.setText("");
                    edYear.setText("");
                    signOutBT.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(loggedInActivity.this,databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });


        //When user clicks the timetable button
        timetableViewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start the timetable activity
                Intent timetableIntent = new  Intent(getBaseContext(), timetableActivity.class);
                //Send Course and year to the activity to access the firebase db of the logged in user
                timetableIntent.putExtra("course",Course);
                timetableIntent.putExtra("year", year);
                startActivity(timetableIntent);
            }
        });

        //When user clicks the deadlines button
        deadlinesViewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the deadlines activity
                Intent deadlinesIntent = new  Intent(getBaseContext(), deadlinesActivity.class);
                //Send Course and year to the activity to access the firebase db of the logged in user
                deadlinesIntent.putExtra("course",Course);
                deadlinesIntent.putExtra("year", year);
                startActivity(deadlinesIntent);
            }
        });



        //When user clicks sign out button
        signOutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the current user and sign out using firebase auth and redirect to the log in activity(main activity)
                //Show a "sign out successful" toast
                FirebaseAuth.getInstance().signOut();
                Intent intentLogOut = new Intent(loggedInActivity.this, MainActivity.class);
                intentLogOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentLogOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogOut);
                finish();
                loggedOutMsg = "Sign Out Successful";
                Toast.makeText(getApplicationContext(), loggedOutMsg, Toast.LENGTH_SHORT).show();


            }
        });







    }


    //If the user clicks back
    @Override
    public void onBackPressed()
    {

        //Show alert box with three buttons: log out, cancel and okay
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit the app?")

                .setNeutralButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If the user clicks log out, show another alert box with two buttons,
                        // ok and cancel
                  new AlertDialog.Builder(loggedInActivity.this)
                          .setTitle("Log Out?")
                          .setMessage("Are you sure you want to log out?")

                          //If the user clicks ok, log the user out
                          .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  FirebaseAuth.getInstance().signOut();
                                  Intent intentLogOut = new Intent(loggedInActivity.this,
                                          MainActivity.class);
                                  intentLogOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                  intentLogOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                  startActivity(intentLogOut);
                                  finish();
                                  loggedOutMsg = "Sign Out Successful";
                                  Toast.makeText(getApplicationContext(), loggedOutMsg,
                                          Toast.LENGTH_SHORT).show();
                              }
                          })

                          //If the user clicks cancel, dismiss the alert box and return to the activity
                          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  dialog.dismiss();
                              }
                          })
                          .create().show();

                    }
                })


                //If the user clicks ok (first alert box), close the app
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent CloseIntent = new Intent(Intent.ACTION_MAIN);
                        CloseIntent.addCategory(Intent.CATEGORY_HOME);
                        CloseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(CloseIntent);

                    }
                })
                //If the user clicks cancel (first alert box), dismiss the dialogue box
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();


    }
}
