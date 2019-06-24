package com.toolsapps.cameracontroller.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.toolsapps.cameracontroller.MainActivity;
import com.toolsapps.cameracontroller.R;

public class AppSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        getPreferenceManager().setSharedPreferencesName("app_settings");
        PreferenceManager.setDefaultValues(this, R.xml.app_settings_preferences, false);
        addPreferencesFromResource(R.xml.app_settings_preferences);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startActivity(new Intent(this, MainActivity.class));
    }
}
//TODO:update