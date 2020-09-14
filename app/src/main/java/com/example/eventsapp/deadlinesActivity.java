package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Html;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class deadlinesActivity extends AppCompatActivity {

    //Declaring design component variables to access the components
    TextView edDeadline;
    TextView edHeading;
    TextView edToday;
    DatabaseReference myRef;
    Button reminderbt;
    Button grantPermissionBt;
    ProgressBar deadlineProgressBar;
    SimpleDateFormat formatter;
    Date date;

    //Declaring and initializing an integer variable to check the permission
    private int CALENDAR_PERMISSION_CODE = 1;

    //Arraylists to store the deadlines, their names and dates
    ArrayList<String> deadlinesList = new ArrayList<String>();
    ArrayList<String> deadlinesNames = new ArrayList<String>();
    ArrayList<String> deadlinesDates = new ArrayList<String>();
    ArrayList<String> dateList = new ArrayList<String>();
    String year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadlines);

        //Starting this activity in night mode (dark mode)
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        //Recieving course from first activity
        Intent intent = getIntent();
        String Course = intent.getExtras().getString("course");
        year = intent.getExtras().getString("year");

        edHeading = (TextView) findViewById(R.id.headingDView);
        edDeadline = (TextView) findViewById(R.id.deadlineView);
        reminderbt = (Button) findViewById(R.id.reminderbt);
        edToday = (TextView) findViewById(R.id.todayView);
        grantPermissionBt = (Button) findViewById(R.id.grantPermissionBt);
        deadlineProgressBar = (ProgressBar) findViewById(R.id.deadlinesProgressBar);

        //Formatting the date to YYYY-MM-dd format to display the current date
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date();
        edToday.setText("Today's date: " + formatter.format(date));

        //edHeading is set to the following text
        edHeading.setText("View and Manage" + "\n" + "Your Deadlines Easily!");




        //On Launching activity, check permissions
        checkPermission();


        //Accessing the database using the course and year variable sent already
        myRef = FirebaseDatabase.getInstance().getReference().child("Courses").child(Course)
                .child("Deadlines").child("Year"+year);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Looping 15 times assuming 15 is the max number of possible deadlines in a year
                for (int i = 1; i <= 15; i++) {
                    //If the deadline exists add it to the deadlinesList adn replace the curly brackets. This stores both the name and the date in this arraylist.
                    if (dataSnapshot.child("deadline" + i).exists()) {
                        deadlinesList.add(dataSnapshot.child("deadline" + i).getValue().toString()
                                .replace("{", "").replace("}", ""));
                    }
                }


                //Splitting the retrieved arraylist containing deadlines to name arraylist
                // and date arraylist
                for (String item : deadlinesList) {
                    String[] splitted = item.split("=");
                    //All the 0th position ones (left side to the '=' sign in deadlinesList) is stored in deadlineNames arraylist
                    deadlinesNames.add(splitted[0]);
                    //All 1st position ones (right side to the '=' sign in deadlinesList) is stored in deadlinesDates arraylist
                    deadlinesDates.add(splitted[1]);

                }


                //Displaying the deadlines usinng viewDeadlines function
                edDeadline.setText("Your deadlines for this year are: ");
                for (int i = 0; i < deadlinesNames.size(); i++) {
                    //Splitting the deadlineDates elements at the '-' sign to store the year, month and date seperately
                    String item = deadlinesDates.get(i);
                    String[] split = item.split("-");
                    //Stored in dateList arraylist
                    dateList.add(split[0]);
                    dateList.add(split[1]);
                    dateList.add(split[2]);
                    try {
                        viewDeadlines(edDeadline, deadlinesNames, deadlinesDates, i, Integer.parseInt(dateList.get(0)),
                                Integer.parseInt(dateList.get(1)), Integer.parseInt(dateList.get(2)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Clearing dateList for next use
                    dateList.clear();
                    deadlineProgressBar.setVisibility(View.GONE);
                }


                //On button click set reminder
                reminderbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Splitting the splitted date arraylist to use in calender
                        //It is splited and stored in datelist arraylist which is used as temporary arraylist
                        //datelist is then used to call the calender function (setReminder)
                        // to set the calander event
                        for (int i = 0; i < deadlinesDates.size(); i++) {
                            String item = deadlinesDates.get(i);
                            String[] split = item.split("-");
                            dateList.add(split[0]);
                            dateList.add(split[1]);
                            dateList.add(split[2]);

                            try {
                                setReminder(deadlinesActivity.this,Integer.parseInt(dateList.get(0)),
                                        Integer.parseInt(dateList.get(1)) - 1,
                                        Integer.parseInt(dateList.get(2)) - 7,
                                        Integer.parseInt(dateList.get(0)),
                                        Integer.parseInt(dateList.get(1)) - 1,
                                        Integer.parseInt(dateList.get(2)), i, deadlinesNames);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            dateList.clear();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(deadlinesActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    //Function to display the deadlines
    public void viewDeadlines(TextView deadlineView, ArrayList<String> reminderNames,
                              ArrayList<String> reminderDates,
                              int flag, int endYear, int endMonth, int endDay) throws ParseException {


        //To show how many days left
        Calendar endTime = Calendar.getInstance();
        endTime.set(endYear, endMonth, endDay, 10, 0);

        //Finding difference between current date and the deadline
        String StringDeadlineDays = String.valueOf(endYear) + "-0" + String.valueOf(endMonth) + "-0"
                + String.valueOf(endDay);
        String todaysDate = formatter.format(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        Date currentDay = sdf.parse(todaysDate);
        Date deadlineDay = sdf.parse(StringDeadlineDays);
        long daysLeft = (deadlineDay.getTime() - currentDay.getTime()) / (24 * 60 * 60 * 1000);


        //Color coding the days according to days left
        if (daysLeft < 15 && daysLeft > 7) {
            deadlineView.append(Html.fromHtml("<br><br><br>" + reminderNames.get(flag) + ": " +
                    reminderDates.get(flag) + "\n" + "<br><font color=\"#964B00\">" + daysLeft
                    + "</font> more days left"));

        } else if (daysLeft < 8 && daysLeft >= 0) {
            if (daysLeft == 1) {
                deadlineView.append(Html.fromHtml("<br><br><br>" + reminderNames.get(flag) + ": " +
                        reminderDates.get(flag) + "\n" + "<br><font color=\"#ff0000\">" + daysLeft
                        + "</font> more day left"));

            } else if (daysLeft == 0) {
                deadlineView.append(Html.fromHtml("<br><br><br>" + reminderNames.get(flag) + ": " +
                        reminderDates.get(flag) + "\n" + "<br><font color=\"#ff0000\">" + daysLeft
                        + "</font>. The deadline is today!"));

            } else {
                deadlineView.append(Html.fromHtml("<br><br><br>" + reminderNames.get(flag) + ": " +
                        reminderDates.get(flag) + "\n" + "<br><font color=\"#ff0000\">" + daysLeft
                        + "</font> more days left"));
            }
        } else if (daysLeft < 0) {

            deadlineView.append("\n\n\n" + reminderNames.get(flag) + ": " + reminderDates.get(flag) + "\n"
                    + "This deadline has passed");

        } else {
            deadlineView.append(Html.fromHtml("<br><br><br>" + reminderNames.get(flag) + ": " +
                    reminderDates.get(flag) + "\n" + "<br><font color=\"#67B826\">" + daysLeft
                    + "</font> more days left"));
        }
    }






    //Function to set the reminders
    public void setReminder(Context ctx,int startYear, int startMonth, int startDay, int endYear, int endMonth,
                            int endDay, int flag, ArrayList<String> reminderNames) throws ParseException{

        //Getting the start day and end day of events and converting them into date format used for Calendar variable
        Calendar start = Calendar.getInstance();
        start.set(startYear, startMonth, startDay, 10, 0);
        Calendar end = Calendar.getInstance();
        end.set(endYear, endMonth, endDay, 10, 0);

        ContentResolver contentResolver = ctx.getContentResolver();

        //To insert the event into the calendar app
        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1); // XXX pick)
        calEvent.put(CalendarContract.Events.TITLE, reminderNames.get(flag));
        calEvent.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
        calEvent.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, "Gulf Standard Time");

        //To insert reminder for the event
        ContentValues reminders = new ContentValues();



        //If the deadline is after the current date, set the reminder
        if (new SimpleDateFormat("yyy-MM-dd").parse(formatter.format(date)).before(end.getTime())) {

            //If the user has granted permission, set the deadline
        if (ContextCompat.checkSelfPermission(deadlinesActivity.this,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(deadlinesActivity.this,
                    Manifest.permission.READ_CALENDAR)==PackageManager.PERMISSION_GRANTED) {

                //To check if event exists in the calendar already
                String[] proj = new String[]{ CalendarContract.Instances._ID,
                        CalendarContract.Instances.BEGIN, CalendarContract.Instances.END,
                                CalendarContract.Instances.EVENT_ID};

                Cursor cursor =  CalendarContract.Instances.query(getContentResolver(), proj,
                        start.getTimeInMillis(), end.getTimeInMillis(), reminderNames.get(flag));

                //If event does not exist, create it
                if (cursor.getCount() <= 0) {
                    Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, calEvent);
                    //Use the uri to get its id
                    int id = Integer.parseInt(uri.getLastPathSegment());
                    //Use the id to set reminder for the event
                    reminders.put(CalendarContract.Reminders.EVENT_ID,id);
                    reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    reminders.put(CalendarContract.Reminders.MINUTES, 0);
                    Uri uri2 = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
                    Toast.makeText(ctx, "Created Calendar Event " + reminderNames.get(flag),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        //After setting all the reminders display the message
           if(flag==reminderNames.size()-1) {
               Toast.makeText(ctx, "Reminders set successfully",
                       Toast.LENGTH_SHORT).show();
           }
    }
    }


    //Function to request calendar access permission from the user
    private void requestCalendarPermission(){
        //If android has shown the allow deny option for permission already and user has denied
        //Show user why the permission is needed
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CALENDAR)
          && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to set reminder in your calendar after checking if " +
                            "it already exists.")
                    //If the user clicks ok, ask for permission again
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(deadlinesActivity.this,
                                    new String[]{Manifest.permission.WRITE_CALENDAR,
                                            Manifest.permission.READ_CALENDAR}, CALENDAR_PERMISSION_CODE);

                    //To check if the user has gicen it now
                    checkPermission();

                        }
                    })
                    //If the user clicks cancel, dismiss the dialogue box
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else{
            //If android has not already  shown the allow deny option, show it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR},
                    CALENDAR_PERMISSION_CODE);

            //After this check if user has given the permission
            checkPermission();
        }
    }

//Function to check for permission
    public void checkPermission(){

//If both write and ready permissions are given show the reminder button and dont show the permisson button
        if(ContextCompat.checkSelfPermission(deadlinesActivity.this,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(deadlinesActivity.this,
                    Manifest.permission.READ_CALENDAR)==PackageManager.PERMISSION_GRANTED){
                reminderbt.setVisibility(View.VISIBLE);
                grantPermissionBt.setVisibility(View.GONE);
                Toast.makeText(deadlinesActivity.this, "You have granted the permission",
                        Toast.LENGTH_SHORT).show();

            }
        }
        else {
            //Else (permission not given) show the permission button and dont show the reminder button
            reminderbt.setVisibility(View.GONE);
            grantPermissionBt.setVisibility(View.VISIBLE);
            //On clicking the permission the button, call function for requesting permission
            grantPermissionBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestCalendarPermission();

                }
            });
        }
    }
    }