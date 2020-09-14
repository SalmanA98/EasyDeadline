package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class SplashScreen extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView welcomeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        welcomeText = (TextView) findViewById(R.id.mdx_welcome);
        welcomeImage = (ImageView) findViewById(R.id.mdx_image);

        Animation welcomeAnimation = AnimationUtils.loadAnimation(this, R.anim.transition);
        welcomeImage.startAnimation(welcomeAnimation);
        welcomeText.startAnimation(welcomeAnimation);

        final Intent intent = new Intent(this, MainActivity.class);

        Thread animationTimer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    startActivity(intent);
                    finish();

                }
            }
        };

        animationTimer.start();
    }
}
