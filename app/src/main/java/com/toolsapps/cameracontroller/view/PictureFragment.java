package com.toolsapps.cameracontroller.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.toolsapps.cameracontroller.GestureDetector;
import com.toolsapps.cameracontroller.GestureDetector.GestureHandler;
import com.toolsapps.cameracontroller.PictureView;
import com.toolsapps.cameracontroller.R;
import com.toolsapps.cameracontroller.ptp.Camera;
import com.toolsapps.cameracontroller.ptp.model.LiveViewData;

public class PictureFragment extends SessionFragment implements Camera.RetrieveImageListener, GestureHandler {

    public static PictureFragment newInstance(int objectHandle) {
        Bundle args = new Bundle();
        args.putInt("handle", objectHandle);
        PictureFragment f = new PictureFragment();
        f.setArguments(args);
        return f;
    }

    private Handler handler;
    private PictureView pictureView;
    private int objectHandle;
    private Bitmap picture;
    private GestureDetector gestureDetector;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        handler = new Handler();
        objectHandle = getArguments().getInt("handle");

        View view = inflater.inflate(R.layout.picture_frag, container, false);
        pictureView = (PictureView) view.findViewById(R.id.image1);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        gestureDetector = new GestureDetector(getActivity(), this);
        pictureView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouch(event);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getActionBar().hide();
        if (camera() == null) {

        } else if (picture == null) {
            camera().retrieveImage(this, objectHandle);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRemoving()) {
            getActivity().getActionBar().show();
        }
    }

    @Override
    public void enableUi(boolean enabled) {
    }

    @Override
    public void cameraStarted(Camera camera) {
    }

    @Override
    public void cameraStopped(Camera camera) {
    }

    @Override
    public void propertyChanged(int property, int value) {
    }

    @Override
    public void propertyDescChanged(int property, int[] values) {
    }

    @Override
    public void setCaptureBtnText(String text) {
    }

    @Override
    public void focusStarted() {
    }

    @Override
    public void focusEnded(boolean hasFocused) {
    }

    @Override
    public void liveViewStarted() {
    }

    @Override
    public void liveViewStopped() {
    }

    @Override
    public void liveViewData(LiveViewData data) {
    }

    @Override
    public void capturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap) {
    }

    @Override
    public void objectAdded(int handle, int format) {
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
                    progressBar.setVisibility(View.GONE);
                    pictureView.setPicture(image);
                }
            }
        });
    }

    @Override
    public void onLongTouch(float posx, float posy) {

    }

    @Override
    public void onPinchZoom(float pX, float pY, float distInPixel) {
        pictureView.zoomAt(pX, pY, distInPixel);
    }

    @Override
    public void onTouchMove(float dx, float dy) {
        pictureView.pan(dx, dy);
    }

    @Override
    public void onFling(float velx, float vely) {
        pictureView.fling(velx, vely);
    }

    @Override
    public void onStopFling() {
        pictureView.stopFling();
    }
}
