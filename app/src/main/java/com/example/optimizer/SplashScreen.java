package com.example.optimizer;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.optimizer.Utils;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferencesHelper preferencesHelper;
    private View img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        img_logo = findViewById(R.id.img_logo);

        preferencesHelper = new SharedPreferencesHelper(this);
        Utils.blackIconStuatusBar(SplashScreen.this, R.color.light_Background);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferencesHelper.isFirstTime()) {
                    // If it's the first time, launch the RegistrationActivity
                    startActivity(new Intent(SplashScreen.this, GetStarted.class));
                    finish(); // Close this activity so the user can't go back to it
                }
                else {
                    // If not the first time, launch the MainActivity with a shared element transition animation
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                            Pair.create(img_logo, "logo"));
                    startActivity(intent, options.toBundle());
                    finish(); // Close this activity so the user can't go back to it
                }
            }
        }, 2000);
    }
}





