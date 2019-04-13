package com.toolsapps.cameracontroller.ptp;

import android.content.Context;
import android.content.Intent;

import com.toolsapps.cameracontroller.ptp.Camera.CameraListener;

public interface PtpService {

    void setCameraListener(CameraListener listener);

    void initialize(Context context, Intent intent);

    void shutdown();

    void lazyShutdown();

    public static class Singleton {
        private static PtpService singleton;

        public static PtpService getInstance(Context context) {
            if (singleton == null) {
                singleton = new PtpUsbService(context);
            }
            return singleton;
        }

        public static void setInstance(PtpService service) {
            singleton = service;
        }
    }
}
