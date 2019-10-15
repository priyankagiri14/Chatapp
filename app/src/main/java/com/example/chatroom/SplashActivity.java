package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    private String token,refreshtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences=getApplicationContext().getSharedPreferences("tokenpref", 0);
        editor=preferences.edit();

        token=preferences.getString("token",null);
        refreshtoken=preferences.getString("refreshtoken",null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(refreshtoken!=null){
                    Intent i = new Intent(SplashActivity.this, Userslist.class);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

}
