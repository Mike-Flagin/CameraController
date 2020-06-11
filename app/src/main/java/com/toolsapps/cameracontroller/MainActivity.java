package com.toolsapps.cameracontroller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.toolsapps.cameracontroller.activities.AppSettingsActivity;
import com.toolsapps.cameracontroller.activities.IntervalsActivity;
import com.toolsapps.cameracontroller.ptp.Camera;
import com.toolsapps.cameracontroller.ptp.Camera.CameraListener;
import com.toolsapps.cameracontroller.ptp.PtpService;
import com.toolsapps.cameracontroller.ptp.model.LiveViewData;
import com.toolsapps.cameracontroller.util.PackageUtil;
import com.toolsapps.cameracontroller.view.GalleryFragment;
import com.toolsapps.cameracontroller.view.SessionActivity;
import com.toolsapps.cameracontroller.view.SessionView;
import com.toolsapps.cameracontroller.view.TabletSessionFragment;

public class MainActivity extends SessionActivity implements CameraListener {

    private static final int DIALOG_PROGRESS = 1;
    private static final int DIALOG_NO_CAMERA = 2;
    private static int GALLERY_SESS = 0; // 0 - sess, 1 - gallery

    private static class MyTabListener {

        private final Fragment fragment;

        public MyTabListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void Select(FragmentTransaction ft) {
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
    }

    private final String TAG = MainActivity.class.getSimpleName();

    private PtpService ptp;
    private Camera camera;

    private boolean isInStart;
    private boolean isInResume;
    private SessionView sessionFrag;
    private boolean isLarge;
    private AppSettings settings;

    public static int shotsAmount = 1;
    public static int shotsPeriod = 0;

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setSessionView(SessionView view) {
        sessionFrag = view;
    }

    @Override
    public AppSettings getSettings() {
        return settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfig.LOG) {
            Log.i(TAG, "onCreate");
        }
        MyTabListener tab = new MyTabListener(new TabletSessionFragment());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        tab.Select(ft);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE)) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            isLarge = true;
        }

        settings = new AppSettings(this);

        if(settings.isDarkMode()) {
            setTheme(R.style.Theme_RYC_Dark);
        } else {
            setTheme(R.style.Theme_RYC);
        }

        setContentView(R.layout.session);

        ptp = PtpService.Singleton.getInstance(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (AppConfig.LOG) {
            Log.i(TAG, "onNewIntent " + intent.getAction());
        }
        this.setIntent(intent);
        if (isInStart) {
            ptp.initialize(this, intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppConfig.LOG) {
            Log.i(TAG, "onStart");
        }
        isInStart = true;
        ptp.setCameraListener(this);
        ptp.initialize(this, getIntent());

        if(settings.isDarkMode()) {
            ((PropertyToggle) findViewById(R.id.liveViewToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.liveViewToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.histogramToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.histogramToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.driveLensToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.driveLensToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.shutterSpeedToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.shutterSpeedToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.apertureValueToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.apertureValueToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.isoSpeedToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.isoSpeedToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            //Change color
//            (findViewById(R.id.whitebalanceToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.colorTemperatureToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.colorTemperatureToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.pictureStyleToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.pictureStyleToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            //Change color
//            (findViewById(R.id.meteringModeToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            //Change color
//            (findViewById(R.id.focusMeteringModeToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.focusPointsToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.focusPointsToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.exposureCompensationToggle)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.exposureCompensationToggle)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((PropertyToggle) findViewById(R.id.setIntervals)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.setIntervals)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            ((Button) findViewById(R.id.takePictureBtn)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
            (findViewById(R.id.takePictureBtn)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));

            try {
                ((Button) findViewById(R.id.focusBtn)).setTextColor(getResources().getColor(R.color.selectedValueBackground));
                (findViewById(R.id.focusBtn)).setBackgroundColor(getResources().getColor(R.color.darkPropertyListBackground));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInResume = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppConfig.LOG) {
            Log.i(TAG, "onStop");
        }
        isInStart = false;
        ptp.setCameraListener(null);
        if (isFinishing()) {
            ptp.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppConfig.LOG) {
            Log.i(TAG, "onDestroy");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_PROGRESS:
            return ProgressDialog.show(this, "", "Generating information. Please wait...", true);
        case DIALOG_NO_CAMERA:
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.dialog_no_camera_title);
            b.setMessage(R.string.dialog_no_camera_message);
            b.setNeutralButton(R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            return b.create();
        }
        return super.onCreateDialog(id);
    }

    public void onMenuSettingsClicked(MenuItem item) {
        startActivity(new Intent(this, AppSettingsActivity.class));
        finish();
    }

    public void onMenuAboutClicked(MenuItem item) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setNeutralButton(R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
        ((TextView) view.findViewById(R.id.about_dialog_version)).setText(getString(R.string.about_dialog_version,
                PackageUtil.getVersionName(this)));
        b.setView(view);
        b.show();
    }

    public void onSetIntervalsClicked(View v) {
        startActivity(new Intent(this, IntervalsActivity.class));
    }

    public void onMenuGallerySessClicked(MenuItem item) {
        MyTabListener tab;
        if(GALLERY_SESS == 0) {
            GALLERY_SESS = 1;
            tab = new MyTabListener(new GalleryFragment());
            item.setTitle(R.string.session);
        } else {
            GALLERY_SESS = 0;
            item.setTitle(R.string.gallery);
            tab = new MyTabListener(new TabletSessionFragment());
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        tab.Select(ft);
    }

    @Override
    public void onCameraStarted(Camera camera) {
        this.camera = camera;
        if (AppConfig.LOG) {
            Log.i(TAG, "camera started");
        }
        try {
            dismissDialog(DIALOG_NO_CAMERA);
        } catch (IllegalArgumentException e) {
        }
        getSupportActionBar().setTitle(camera.getDeviceName());
        camera.setCapturedPictureSampleSize(settings.getCapturedPictureSampleSize());
        sessionFrag.cameraStarted(camera);
    }

    @Override
    public void onCameraStopped(Camera camera) {
        if (AppConfig.LOG) {
            Log.i(TAG, "camera stopped");
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.camera = null;
        sessionFrag.cameraStopped(camera);
    }

    @Override
    public void onNoCameraFound() {
        showDialog(DIALOG_NO_CAMERA);
    }

    @Override
    public void onError(String message) {
        sessionFrag.enableUi(false);
        sessionFrag.cameraStopped(null);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPropertyChanged(int property, int value) {
        sessionFrag.propertyChanged(property, value);
    }

    @Override
    public void onPropertyStateChanged(int property, boolean enabled) {
        // TODO
    }

    @Override
    public void onPropertyDescChanged(int property, int[] values) {
        sessionFrag.propertyDescChanged(property, values);
    }

    @Override
    public void onLiveViewStarted() {
        sessionFrag.liveViewStarted();
    }

    @Override
    public void onLiveViewStopped() {
        sessionFrag.liveViewStopped();
    }

    @Override
    public void onLiveViewData(LiveViewData data) {
        if (!isInResume) {
            return;
        }
        sessionFrag.liveViewData(data);
    }

    @Override
    public void onCapturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap) {
        if (thumbnail != null) {
            sessionFrag.capturedPictureReceived(objectHandle, filename, thumbnail, bitmap);
        } else {
            Toast.makeText(this, "No thumbnail available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBulbStarted() {
        sessionFrag.setCaptureBtnText("0");
    }

    @Override
    public void onBulbExposureTime(int seconds) {
        sessionFrag.setCaptureBtnText("" + seconds);
    }

    @Override
    public void onBulbStopped() {
        sessionFrag.setCaptureBtnText("Fire");
    }

    @Override
    public void onFocusStarted() {
        sessionFrag.focusStarted();
    }

    @Override
    public void onFocusEnded(boolean hasFocused) {
        sessionFrag.focusEnded(hasFocused);
    }

    @Override
    public void onFocusPointsChanged() {
        // TODO onFocusPointsToggleClicked(null);
    }

    @Override
    public void onObjectAdded(int handle, int format) {
        sessionFrag.objectAdded(handle, format);
    }
}

//TODO save images to SDCARD