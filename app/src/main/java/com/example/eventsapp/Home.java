package com.example.eventsapp;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//This class is to keep track of user session
public class Home extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Create firebase instance to get current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //If there is a user logged in, launch loggedInActivity directly
        if(firebaseUser!=null){
                Intent loggedIn = new Intent(Home.this, loggedInActivity.class);
                loggedIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loggedIn);

        }
        else{
            //Else (there is no user logged in), launch SplashScreen that shows the splash screen
            Intent splash = new Intent(this, SplashScreen.class);
            splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(splash);
        }

    }
}
