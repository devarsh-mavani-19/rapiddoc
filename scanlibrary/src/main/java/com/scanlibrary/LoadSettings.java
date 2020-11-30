package com.scanlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import java.util.Objects;

public class LoadSettings {
    public static void load(Activity activity){
        SharedPreferences preferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int r = preferences.getInt("red", 0);
        int g = preferences.getInt("green", 39);
        int b = preferences.getInt("blue", 38);

        if(activity.getActionBar()!=null) {
            Objects.requireNonNull(activity.getActionBar()).setBackgroundDrawable(
                    new ColorDrawable(Color.rgb(r, g, b)));
        }

        Window window = activity.getWindow();
        int statusBarColor = Color.rgb(r,g,b);
        if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        window.setStatusBarColor(statusBarColor);

    }
    public static void setViewTheme(View v, Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int r = preferences.getInt("red", 0);
        int g = preferences.getInt("green", 39);
        int b = preferences.getInt("blue", 38);
        v.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(r, g, b)));
    }
}
