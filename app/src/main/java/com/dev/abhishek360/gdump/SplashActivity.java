package com.dev.abhishek360.gdump;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity
{
    private static final int SPLASH_TIME_OUT=1500;
   private SharedPreferences sharedPreferences;
   private boolean isUserLoggedIn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);

        sharedPreferences = getSharedPreferences("gdumpPref",MODE_PRIVATE);
        isUserLoggedIn=sharedPreferences.getBoolean("IS_USER_LOGGED_IN",false);


        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                if(isUserLoggedIn)
                {
                    Intent in = new Intent(SplashActivity.this,HomeActivity.class);

                    startActivity(in);
                }
                else
                    {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                }

            }
        },SPLASH_TIME_OUT);
    }

    protected void onStart()
    {
        super.onStart();


    }
}
