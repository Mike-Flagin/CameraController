package com.toolsapps.cameracontroller.view;

import android.app.Activity;

import com.toolsapps.cameracontroller.AppSettings;
import com.toolsapps.cameracontroller.ptp.Camera;

public abstract class SessionActivity extends Activity {

    public abstract Camera getCamera();

    public abstract void setSessionView(SessionView view);

    public abstract AppSettings getSettings();
}
