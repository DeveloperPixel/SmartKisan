package com.example.optimizer;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_FIRST_TIME = "isFirstTime";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTime(boolean isFirstTime) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).apply();
    }
}

