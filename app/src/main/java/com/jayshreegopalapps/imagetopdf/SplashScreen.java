package com.jayshreegopalapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getSupportActionBar().hide();
        FragmentContainerView fragmentContainerView = findViewById(R.id.splash_fragment);
        prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);
        boolean isPasswordSet = prefs.getBoolean("isPasswordSet", false);
        if(isPasswordSet) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.splash_fragment,new PasswordFragment(), "ask_password").commit();
        } else {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.splash_fragment,new SplashFragment(), "splash_screen").commit();
        }

    }
}
