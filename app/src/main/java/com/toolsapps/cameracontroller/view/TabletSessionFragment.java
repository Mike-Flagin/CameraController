package com.toolsapps.cameracontroller.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.toolsapps.cameracontroller.AppSettings;
import com.toolsapps.cameracontroller.GestureDetector;
import com.toolsapps.cameracontroller.MainActivity;
import com.toolsapps.cameracontroller.PictureView;
import com.toolsapps.cameracontroller.PropertyDisplayer;
import com.toolsapps.cameracontroller.R;
import com.toolsapps.cameracontroller.ptp.Camera;
import com.toolsapps.cameracontroller.ptp.Camera.DriveLens;
import com.toolsapps.cameracontroller.ptp.Camera.Property;
import com.toolsapps.cameracontroller.ptp.FocusPoint;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.model.LiveViewData;
import com.toolsapps.cameracontroller.ptp.model.ObjectInfo;
import com.toolsapps.cameracontroller.util.DimenUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabletSessionFragment extends SessionFragment implements GestureDetector.GestureHandler,
        Camera.RetrieveImageInfoListener, Camera.StorageInfoListener, Camera.RetrieveImageListener {

    private final Camera.StorageInfoListener storageInfoListener = this;

    private final Handler handler = new Handler();

    private LinearLayout leftPropertiesView;

    private LayoutInflater inflater;
    private String currentFileName = "";
    private ToggleButton liveViewToggle;
    private ToggleButton histogramToggle;
    private ToggleButton driveLensToggle;
    private ImageView shootingModeView;

    private AppSettings settings;

    private Button focusBtn;
    private Button takePictureBtn;
    private PictureView liveView;
    private TextView availableShotsText;
    private TextView focusModeText;
    private TextView exposureIndicatorText;
    private ImageView batteryLevelView;
    private ToggleButton focusPointsToggle;

    private final Map<Integer, PropertyDisplayer> properties = new HashMap<Integer, PropertyDisplayer>();

    private LinearLayout driveLensPane;

    private StorageAdapter storageAdapter;

    private LiveViewData currentLiveViewData;
    private LiveViewData currentLiveViewData2;
    private Toast focusToast;

    private Bitmap currentCapturedBitmap;
    private SharedPreferences prefs;
    private GestureDetector gestureDetector;

    private boolean showsCapturedPicture;

    private boolean isPro;

    private ThumbnailAdapter thumbnailAdapter;

    private long showCapturedPictureDuration;
    private boolean showCapturedPictureNever;
    private boolean showCapturedPictureDurationManual;

    private View pictureStreamContainer;

    private Button btnLiveview;

    private ImageView streamToggle;

    private Runnable liveViewRestarterRunner;

    private boolean justCaptured;

    private final Runnable justCapturedResetRunner = new Runnable() {
        @Override
        public void run() {
            justCaptured = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.session_frag, container, false);

        this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        leftPropertiesView = (LinearLayout) view.findViewById(R.id.leftPropertiesLayout);
        focusBtn = (Button) view.findViewById(R.id.focusBtn);
        takePictureBtn = (Button) view.findViewById(R.id.takePictureBtn);
        liveView = (PictureView) view.findViewById(R.id.liveView);
        availableShotsText = (TextView) view.findViewById(R.id.availableShotsText);
        batteryLevelView = (ImageView) view.findViewById(R.id.batteryLevelIcon);
        focusModeText = (TextView) view.findViewById(R.id.focusModeText);
        exposureIndicatorText = (TextView) view.findViewById(R.id.exposureIndicatorText);
        driveLensPane = (LinearLayout) view.findViewById(R.id.driveLensPane);
        driveLensToggle = (ToggleButton) view.findViewById(R.id.driveLensToggle);
        liveViewToggle = (ToggleButton) view.findViewById(R.id.liveViewToggle);
        histogramToggle = (ToggleButton) view.findViewById(R.id.histogramToggle);
        shootingModeView = (ImageView) view.findViewById(R.id.shootingModeView);
        btnLiveview = (Button) view.findViewById(R.id.btn_liveview);

        storageAdapter = new StorageAdapter(getActivity());
        settings = new AppSettings(getContext());

        btnLiveview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLiveview.setVisibility(View.GONE);
                startLiveViewAgain();
            }
        });

        focusPointsToggle = (ToggleButton) view.findViewById(R.id.focusPointsToggle);

        isPro = focusPointsToggle != null;

        setupProperty(view, Camera.Property.ShutterSpeed, R.id.shutterSpeedToggle, "Tv");
        if (isPro) {
            setupProperty(view, Camera.Property.ApertureValue, R.id.apertureValueToggle, "Av");
        }
        setupProperty(view, Camera.Property.IsoSpeed, R.id.isoSpeedToggle, "ISO");
        setupProperty(view, Camera.Property.Whitebalance, R.id.whitebalanceToggle, "WB");
        if (isPro) {
            setupProperty(view, Camera.Property.ColorTemperature, R.id.colorTemperatureToggle, "Temp");
            setupProperty(view, Camera.Property.PictureStyle, R.id.pictureStyleToggle, "Pic Style");
            setupProperty(view, Camera.Property.ExposureMeteringMode, R.id.meteringModeToggle, "Metering");
            setupProperty(view, Camera.Property.FocusMeteringMode, R.id.focusMeteringModeToggle, "Focus");
            setupProperty(view, Camera.Property.ExposureCompensation, R.id.exposureCompensationToggle, "Exp Comp");
        }

        focusToast = Toast.makeText(getActivity(), "Focused", Toast.LENGTH_SHORT);

        prefs = getActivity().getSharedPreferences("settings.xml", Context.MODE_PRIVATE);
        for (Map.Entry<Integer, PropertyDisplayer> e : properties.entrySet()) {
            e.getValue().setAutoHide(prefs.getBoolean("property.id" + e.getKey().intValue() + ".autohide", false));
        }

        focusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFocusClicked(v);
            }
        });
        takePictureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePictureClicked(v);
            }
        });

        if (isPro) {
            gestureDetector = new GestureDetector(getActivity(), this);

            liveView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (camera() == null) {
                        return true;
                    }
                    gestureDetector.onTouch(event);
                    return true;
                }
            });

            liveViewToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLiveViewToggleClicked(v);
                }
            });

            driveLensToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensToggleClicked(v);
                }
            });
            focusPointsToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFocusPointsToggleClicked(v);
                }
            });

            driveLensPane.findViewById(R.id.driveFocusFar1).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensFar1(v);
                }
            });
            driveLensPane.findViewById(R.id.driveFocusFar2).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensFar2(v);
                }
            });
            driveLensPane.findViewById(R.id.driveFocusFar3).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensFar3(v);
                }
            });
            driveLensPane.findViewById(R.id.driveFocusNear1).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensNear1(v);
                }
            });
            driveLensPane.findViewById(R.id.driveFocusNear2).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensNear2(v);
                }
            });
            driveLensPane.findViewById(R.id.driveFocusNear3).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDriveLensNear3(v);
                }
            });

            pictureStreamContainer = view.findViewById(R.id.picture_stream_container);
            thumbnailAdapter = new ThumbnailAdapter(getActivity());
            final ListView pictureStream = (ListView) view.findViewById(R.id.picture_stream);
            pictureStream.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (camera() == null) {
                        return;
                    }
                    liveView.setPicture(null);
                    camera().retrievePicture(thumbnailAdapter.getItemHandle(position));
                }
            });
            pictureStream.setVisibility(View.GONE);
            pictureStream.setAdapter(thumbnailAdapter);
            streamToggle = (ImageView) view.findViewById(R.id.picture_stream_toggle);
            streamToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean showStream = pictureStream.getVisibility() == View.GONE;
                    pictureStream.setVisibility(showStream ? View.VISIBLE : View.GONE);
                    streamToggle.setRotation(showStream ? 180f : 0f);
                }
            });

            liveViewRestarterRunner = new Runnable() {
                @Override
                public void run() {
                    startLiveViewAgain();
                }
            };
        }

        enableUi(false);

        ((SessionActivity) getActivity()).setSessionView(this);

        return view;
    }

    private void setupProperty(View container, int virtualProperty, int btnId, String text) {
        PropertyDisplayer displayer = new PropertyDisplayer(getActivity(), container, inflater, virtualProperty, btnId,
                text);
        properties.put(virtualProperty, displayer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.rightMargin = (int) DimenUtil.dpToPx(getActivity(), 2);
        displayer.getList().setLayoutParams(params);
        leftPropertiesView.addView(displayer.getList());
    }

    @Override
    public void onLongTouch(float posx, float posy) {
        if (camera() == null) {
            return;
        }
        if (camera().isLiveViewOpen()) {
            if (camera().isLiveViewAfAreaSupported()) {
                camera().setLiveViewAfArea(liveView.calculatePictureX(posx), liveView.calculatePictureY(posy));
            }
        } else if (focusPointsToggle.isChecked()) {
            float x = liveView.calculatePictureX(posx);
            float y = liveView.calculatePictureY(posy);
            for (FocusPoint fp : camera().getFocusPoints()) {
                if (Math.abs(x - fp.posx) <= fp.radius && Math.abs(y - fp.posy) <= fp.radius) {
                    camera().setProperty(Property.CurrentFocusPoint, fp.id);
                    break;
                }
            }
        }
    }

    @Override
    public void onPinchZoom(float pX, float pY, float distInPixel) {
        liveView.zoomAt(pX, pY, distInPixel);
    }

    @Override
    public void onTouchMove(float dx, float dy) {
        liveView.pan(dx, dy);
    }

    @Override
    public void onFling(float velx, float vely) {
        liveView.fling(velx, vely);
    }

    @Override
    public void onStopFling() {
        liveView.stopFling();
    }

    @Override
    public void onStart() {
        super.onStart();
        showCapturedPictureDurationManual = getSettings().isShowCapturedPictureDurationManual();
        showCapturedPictureNever = getSettings().isShowCapturedPictureNever();
        showCapturedPictureDuration = Math.abs(getSettings().getShowCapturedPictureDuration() * 1000L);
        thumbnailAdapter.setMaxNumPictures(getSettings().getNumPicturesInStream());
        thumbnailAdapter.setShowFilename(getSettings().isShowFilenameInStream());
        boolean hasNoPictures = getSettings().getNumPicturesInStream() == 0;
        pictureStreamContainer.setVisibility(hasNoPictures ? View.GONE : View.VISIBLE);
        liveView.setLiveViewData(null);
        if (camera() != null) {
            cameraStarted(camera());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera() != null) {
            if (isPro && camera().isLiveViewOpen()) {
                // TODO possible that more than one calls this
                currentLiveViewData = null;
                currentLiveViewData2 = null;
                camera().getLiveViewPicture(null);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Editor editor = prefs.edit();
        for (Map.Entry<Integer, PropertyDisplayer> e : properties.entrySet()) {
            editor.putBoolean("property.id" + e.getKey().intValue() + ".autohide", e.getValue().getAutoHide());
        }
        editor.apply();
    }

    @Override
    public void enableUi(boolean enabled) {
        leftPropertiesView.setVisibility(enabled ? View.VISIBLE : View.GONE);

        batteryLevelView.getDrawable().setLevel(0);
        exposureIndicatorText.setText("");
        driveLensPane.setVisibility(View.GONE);

        for (PropertyDisplayer d : properties.values()) {
            d.setEditable(enabled);
        }

        if (isPro) {
            liveViewToggle.setEnabled(false);
            histogramToggle.setEnabled(false);
            driveLensToggle.setEnabled(false);
            focusPointsToggle.setEnabled(false);
            focusPointsToggle.setChecked(false);
        }

        focusBtn.setEnabled(enabled);
        takePictureBtn.setEnabled(enabled);
    }

    @Override
    public void cameraStarted(Camera camera) {
        for (Map.Entry<Integer, PropertyDisplayer> e : properties.entrySet()) {
            propertyDescChanged(e.getKey(), camera.getPropertyDesc(e.getKey()));
            propertyChanged(e.getKey(), camera.getProperty(e.getKey()));
            e.getValue().setCamera(camera);
        }

        if (!camera.isAutoFocusSupported()) {
            focusBtn.setVisibility(View.GONE);
        }

        enableUi(true);

        propertyChanged(Camera.Property.BatteryLevel, camera.getProperty(Camera.Property.BatteryLevel));
        propertyChanged(Camera.Property.FocusMode, camera.getProperty(Camera.Property.FocusMode));
        propertyChanged(Camera.Property.AvailableShots, camera.getProperty(Camera.Property.AvailableShots));
        propertyChanged(Camera.Property.CurrentFocusPoint, camera.getProperty(Camera.Property.CurrentFocusPoint));

        if (isPro) {
            if (camera.isLiveViewSupported()) {
                liveViewToggle.setEnabled(camera.isLiveViewSupported());
            }

            if (camera.isLiveViewOpen()) {
                liveViewStarted();
            } else if (camera.isSettingPropertyPossible(Property.FocusPoints)) {
                focusPointsToggle.setEnabled(true);
            }
        }
    }

    @Override
    public void cameraStopped(Camera camera) {
        enableUi(false);
        for (Map.Entry<Integer, PropertyDisplayer> e : properties.entrySet()) {
            e.getValue().setProperty(0, "", null);
            e.getValue().setPropertyDesc(new int[0], new String[0], null);
            e.getValue().setCamera(null);
        }
    }

    @Override
    public void propertyChanged(int property, int value) {
        if (!inStart || camera() == null) {
            return;
        }
        PropertyDisplayer displayer = properties.get(property);
        Integer icon = camera().propertyToIcon(property, value);
        if (displayer != null) {
            displayer.setProperty(value, camera().propertyToString(property, value), icon);

            if (property == Camera.Property.Whitebalance) {
                PropertyDisplayer colorTemp = properties.get(Camera.Property.ColorTemperature);
                colorTemp.setEditable(camera().isSettingPropertyPossible(Camera.Property.ColorTemperature));
            }
        } else if (property == Camera.Property.ShootingMode) {
            shootingModeView.setImageResource(camera().propertyToIcon(property, value));
            for (Map.Entry<Integer, PropertyDisplayer> e : properties.entrySet()) {
                e.getValue().setEditable(camera().isSettingPropertyPossible(e.getKey()));
            }
            if (isPro && !camera().isLiveViewOpen()) {
                focusPointsToggle.setEnabled(camera().isSettingPropertyPossible(Camera.Property.FocusPoints));
            }
        } else if (property == Camera.Property.BatteryLevel) {
            batteryLevelView.getDrawable().setLevel(value);
        } else if (property == Camera.Property.CurrentExposureIndicator) {
            if (value != 0x7fffffff) {
                exposureIndicatorText.setText(camera().propertyToString(property, value));
            } else {
                exposureIndicatorText.setText("? EV");
            }
        } else if (property == Camera.Property.FocusMode) {
            focusModeText.setText(camera().propertyToString(property, value));
        } else if (isPro) {
            if (property == Camera.Property.AvailableShots) {
                if (availableShotsText != null && value != 0x7fffffff) {
                    availableShotsText.setText("" + value);
                }
            } else if (property == Camera.Property.CurrentFocusPoint) {
                liveView.setCurrentFocusPoint(value);
            }
        }
    }

    @Override
    public void propertyDescChanged(int property, int[] values) {
        if (!inStart || camera() == null) {
            return;
        }
        PropertyDisplayer displayer = properties.get(property);
        if (displayer != null) {
            String[] labels = new String[values.length];
            for (int i = 0; i < values.length; ++i) {
                labels[i] = camera().propertyToString(property, values[i]);
            }
            Integer[] icons = null;
            if (property == Property.Whitebalance || property == Property.ExposureMeteringMode) {
                icons = new Integer[values.length];
                for (int i = 0; i < values.length; ++i) {
                    icons[i] = camera().propertyToIcon(property, values[i]);
                }
            }
            displayer.setPropertyDesc(values, labels, icons);
        }
    }

    @Override
    public void setCaptureBtnText(String text) {
        takePictureBtn.setText(text);
    }

    @Override
    public void focusStarted() {
        focusToast.cancel();
        focusBtn.setEnabled(false);
        takePictureBtn.setEnabled(false);
    }

    @Override
    public void focusEnded(boolean hasFocused) {
        if (hasFocused) {
            focusToast.show();
        }
        focusBtn.setEnabled(true);
        takePictureBtn.setEnabled(true);
    }

    @Override
    public void liveViewStarted() {
        if (!isPro) {
            return;
        }
        if (!inStart || camera() == null) {
            return;
        }
        liveViewToggle.setChecked(true);
        if (camera().isDriveLensSupported()) {
            driveLensToggle.setEnabled(true);
        }
        if (camera().isHistogramSupported()) {
            histogramToggle.setEnabled(true);
        }
        focusPointsToggle.setEnabled(false);
        liveView.setLiveViewData(null);
        showsCapturedPicture = false;
        currentLiveViewData = null;
        currentLiveViewData2 = null;
        camera().getLiveViewPicture(null);
    }

    @Override
    public void liveViewStopped() {
        if (!isPro) {
            return;
        }
        if (!inStart || camera() == null) {
            return;
        }
        liveViewToggle.setChecked(false);
        histogramToggle.setEnabled(false);
        driveLensToggle.setEnabled(false);
        focusPointsToggle.setEnabled(camera().isSettingPropertyPossible(Property.FocusPoints));
    }

    @Override
    public void liveViewData(LiveViewData data) {
        if (!isPro) {
            return;
        }
        if (!inStart || camera() == null) {
            return;
        }
        if (justCaptured || showsCapturedPicture || !liveViewToggle.isChecked()) {
            return;
        }
        if (data == null) {
            camera().getLiveViewPicture(null);
            return;
        }

        data.hasHistogram &= histogramToggle.isChecked();

        liveView.setLiveViewData(data);
        currentLiveViewData2 = currentLiveViewData;
        this.currentLiveViewData = data;
        camera().getLiveViewPicture(currentLiveViewData2);
        //        ++fps;
        //        if (last + 1000 < System.currentTimeMillis()) {
        //            Log.i(TAG, "fps " + fps);
        //            last = System.currentTimeMillis();
        //            fps = 0;
        //        }
    }

    private void startLiveViewAgain() {
        showsCapturedPicture = false;
        if (currentCapturedBitmap != null) {
            liveView.setPicture(null);
            currentCapturedBitmap.recycle();
            currentCapturedBitmap = null;
        }
        if (camera() != null && camera().isLiveViewOpen()) {
            liveView.setLiveViewData(null);
            currentLiveViewData = null;
            currentLiveViewData2 = null;
            camera().getLiveViewPicture(currentLiveViewData2);
        }
    }

    @Override
    public void capturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap) {
        if (!inStart) {
            bitmap.recycle();
            return;
        }
        showsCapturedPicture = true;
        if (isPro && liveViewToggle.isChecked()) {
            if (!showCapturedPictureDurationManual && !showCapturedPictureNever) {
                handler.postDelayed(liveViewRestarterRunner, showCapturedPictureDuration);
            } else {
                btnLiveview.setVisibility(View.VISIBLE);
            }
        }
        thumbnailAdapter.addFront(objectHandle, filename, thumbnail);
        liveView.setPicture(bitmap);
        Toast.makeText(getActivity(), filename, Toast.LENGTH_SHORT).show();
        if (currentCapturedBitmap != null) {
            currentCapturedBitmap.recycle();
        }
        currentCapturedBitmap = bitmap;
        if (bitmap == null) {
            Toast.makeText(getActivity(), "Error decoding picture. Try to reduce picture size in settings!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void objectAdded(int handle, int format) {
        if (camera() == null) {
            return;
        }
        if (format == PtpConstants.ObjectFormat.EXIF_JPEG || format == PtpConstants.ObjectFormat.JFIF) {
            if (isPro && liveViewToggle.isChecked() && showCapturedPictureNever) {
                camera().retrieveImageInfo(this, handle);
                handler.post(liveViewRestarterRunner);
            } else {
                camera().retrievePicture(handle);
            }
        }
    }

    public void onDriveLensToggleClicked(View v) {
        driveLensPane.setVisibility(driveLensToggle.isChecked() ? View.VISIBLE : View.GONE);
    }

    public void onLiveViewToggleClicked(View v) {
        camera().setLiveView(liveViewToggle.isChecked());
        if (liveViewToggle.isChecked()) {
        } else {
            handler.removeCallbacks(liveViewRestarterRunner);
            btnLiveview.setVisibility(View.GONE);
            liveView.setLiveViewData(null);
            histogramToggle.setChecked(false);
            driveLensToggle.setChecked(false);
            onDriveLensToggleClicked(null);
        }
    }

    public void onFocusClicked(View view) {
        camera().focus();
    }

    public void onTakePictureClicked(View view) {
        // TODO necessary
        //liveView.setLiveViewData(null);
        Thread thread = new Thread(new Runnable() {
        public void run() {
            for(int i = 0; i < MainActivity.shotsAmount; ++i) {
                camera().capture();
                if (MainActivity.shotsAmount > 1) {
                    //Toast.makeText(getContext(), getText(R.string.shot) + " " + (i + 1) + "/" + MainActivity.shotsAmount, Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(MainActivity.shotsPeriod * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (settings.isSaveImg()) {
                    camera().retrieveStorages(storageInfoListener);
                }

                justCaptured = true;
                handler.postDelayed(justCapturedResetRunner, 500);
            }

            MainActivity.shotsAmount = 1;
            MainActivity.shotsPeriod = 0;
        }});
        thread.start();
    }

    public void onDriveLensNear3(View v) {
        camera().driveLens(DriveLens.Near, DriveLens.Hard);
    }

    public void onDriveLensNear2(View v) {
        camera().driveLens(DriveLens.Near, DriveLens.Medium);
    }

    public void onDriveLensNear1(View v) {
        camera().driveLens(DriveLens.Near, DriveLens.Soft);
    }

    public void onDriveLensFar1(View v) {
        camera().driveLens(DriveLens.Far, DriveLens.Soft);
    }

    public void onDriveLensFar2(View v) {
        camera().driveLens(DriveLens.Far, DriveLens.Medium);
    }

    public void onDriveLensFar3(View v) {
        camera().driveLens(DriveLens.Far, DriveLens.Hard);
    }

    public void onFocusPointsToggleClicked(View view) {
        if (focusPointsToggle.isChecked()) {
            liveView.setFocusPoints(camera().getFocusPoints());
        } else {
            liveView.setFocusPoints(new ArrayList<FocusPoint>());
        }
    }

    @Override
    public void onImageInfoRetrieved(final int objectHandle, final ObjectInfo objectInfo, final Bitmap thumbnail) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded() && !isRemoving()) {
                    thumbnailAdapter.addFront(objectHandle, objectInfo.filename, thumbnail);
                }
            }
        });
        currentFileName = objectInfo.filename;
    }

    @Override
    public void onStorageFound(final int handle, final String label) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!inStart) {
                    return;
                }
                storageAdapter.add(handle, label);
            }
        });
    }

    @Override
    public void onAllStoragesFound() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!inStart || camera() == null) {
                    return;
                }
                if (storageAdapter.getCount() == 0) {
                    return;
                }
                try {
                    Thread.sleep(camera().getProperty(1) / 10); //Большой костыль - пофиксить
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                camera().retrieveImageHandles(TabletSessionFragment.this, storageAdapter.getItemHandle(0),
                        PtpConstants.ObjectFormat.EXIF_JPEG);
            }
        });
    }

    @Override
    public void onImageHandlesRetrieved(final int[] handles) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!inStart) {
                    return;
                }
                if (handles.length == 0) {
                    return;
                }
                saveImage(handles[handles.length - 1]);
            }
        });
    }

    private void saveImage(int handle) {
        camera().retrieveImageInfo(this, handle);
        camera().retrieveImage(this, handle);
    }

    @Override
    public void onImageRetrieved(int objectHandle, final Bitmap image) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (image == null) {
                    if (inStart) {
                        Toast.makeText(getActivity(), getString(R.string.error_loading_image), Toast.LENGTH_LONG)
                                .show();
                    }
                    if (isAdded()) {
                        getFragmentManager().popBackStack();
                    }
                } else {
                    saveToSdCard(image, currentFileName);
                }
            }
        });
    }

    private void saveToSdCard(Bitmap image, String currentFileName) {
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File img = new File(sdCardDirectory, "/CameraPhotos/" + currentFileName);
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(img);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
