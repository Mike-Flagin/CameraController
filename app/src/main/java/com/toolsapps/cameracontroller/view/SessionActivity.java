package com.toolsapps.cameracontroller.view;

import android.support.v7.app.AppCompatActivity;

import com.toolsapps.cameracontroller.AppSettings;
import com.toolsapps.cameracontroller.ptp.Camera;

public abstract class SessionActivity extends AppCompatActivity {

    public abstract Camera getCamera();

    public abstract void setSessionView(SessionView view);

    public abstract AppSettings getSettings();
}
