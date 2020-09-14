package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zolad.zoominimageview.ZoomInImageViewAttacher;

public class timetableActivity extends AppCompatActivity {

    DatabaseReference myRef;
    String urlImage;
    ProgressBar timetableProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        //Setting dark mode for the activity (Not used since it does not work in some phones)
       // getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        timetableProgressBar = (ProgressBar) findViewById(R.id.timetableProgressBar);

        //Recieving course from first activity (loggedInActivity)
        Intent intent = getIntent();
        String Course = intent.getExtras().getString("course");
        String year = intent.getExtras().getString("year");

        //Using the course and year send from previous activity to get the users timetable
        myRef =  FirebaseDatabase.getInstance().getReference().child("Courses").child(Course)
                .child("Timetable").child("Year"+year);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Initializing the urlImage with the url of the picture stored in the firebase storage
                urlImage = dataSnapshot.child("url").getValue().toString();
                //Using photoview with glide to display the zoomable image from URL
                PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
                Glide.with(timetableActivity.this)
                        .load(urlImage)
                        .into(photoView);
                timetableProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(timetableActivity.this,databaseError.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
