package com.toolsapps.cameracontroller.view;

import com.toolsapps.cameracontroller.AppSettings;
import com.toolsapps.cameracontroller.ptp.Camera;

public abstract class SessionFragment extends BaseFragment implements SessionView {

    protected boolean inStart;

    protected Camera camera() {
        if (getActivity() == null) {
            return null;
        }
        return ((SessionActivity) getActivity()).getCamera();
    }

    protected AppSettings getSettings() {
        return ((SessionActivity) getActivity()).getSettings();
    }

    @Override
    public void onStart() {
        super.onStart();
        inStart = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        inStart = false;
    }
}
