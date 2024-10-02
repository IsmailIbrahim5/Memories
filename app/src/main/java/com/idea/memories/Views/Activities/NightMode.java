package com.idea.memories.Views.Activities;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class NightMode extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = getSharedPreferences("nightMode", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("isNightModeOn", 1);

        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    public static void day() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void night() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
