package com.toolsapps.cameracontroller.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.toolsapps.cameracontroller.AppSettings;
import com.toolsapps.cameracontroller.MainActivity;
import com.toolsapps.cameracontroller.R;

public class IntervalsActivity extends Activity {

    private AppSettings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new AppSettings(this);

        if(settings.isDarkMode()) {
            setTheme(R.style.Theme_RYC_Dark);
        } else {
            setTheme(R.style.Theme_RYC);
        }
        setContentView(R.layout.intervals);

        if(settings.isDarkMode()) {
            ((EditText)findViewById(R.id.shotsAmount)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            ((EditText)findViewById(R.id.periodOfShots)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            ((Button)findViewById(R.id.ButtonIntervals)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.ButtonIntervals)).setBackgroundColor(getResources().getColor(R.color.darkWindowBackground));

            ((TextView)findViewById(R.id.shotsAmountView)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            ((TextView)findViewById(R.id.periodOfShotsView)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
        } else {
            ((EditText)findViewById(R.id.shotsAmount)).setTextColor(getResources().getColor(R.color.darkSelectedValueBackground));
            ((EditText)findViewById(R.id.periodOfShots)).setTextColor(getResources().getColor(R.color.darkSelectedValueBackground));
            ((Button)findViewById(R.id.ButtonIntervals)).setTextColor(getResources().getColor(R.color.darkSelectedValueBackground));
            (findViewById(R.id.ButtonIntervals)).setBackgroundColor(getResources().getColor(R.color.propertyListBackground));

            ((TextView)findViewById(R.id.shotsAmountView)).setTextColor(getResources().getColor(R.color.darkSelectedValueBackground));
            ((TextView)findViewById(R.id.periodOfShotsView)).setTextColor(getResources().getColor(R.color.darkSelectedValueBackground));
        }
    }

    public void SetIntervalButtonClicked(View view) {
        if (((EditText)findViewById(R.id.shotsAmount)).getText().toString().equals("")) {
            MainActivity.shotsAmount = 1;
        } else {
            MainActivity.shotsAmount = Integer.parseInt(((EditText)findViewById(R.id.shotsAmount)).getText().toString());
        }

        if (((EditText)findViewById(R.id.periodOfShots)).getText().toString().equals("")) {
            MainActivity.shotsPeriod = 0;
        } else {
            MainActivity.shotsPeriod = Integer.parseInt(((EditText)findViewById(R.id.periodOfShots)).getText().toString());
        }

        finish();
    }
}
