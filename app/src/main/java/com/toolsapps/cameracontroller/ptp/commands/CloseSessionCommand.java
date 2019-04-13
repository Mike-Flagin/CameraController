package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.AppConfig;
import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

import android.util.Log;

public class CloseSessionCommand extends Command {

    private final String TAG = CloseSessionCommand.class.getSimpleName();

    public CloseSessionCommand(PtpCamera camera) {
        super(camera);
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
        // Can this even happen?
        if (responseCode == Response.DeviceBusy) {
            camera.onDeviceBusy(this, true);
            return;
        }
        // close even when error happened
        camera.onSessionClosed();
        if (responseCode != Response.Ok) {
            // TODO error report
            if (AppConfig.LOG) {
                Log.w(TAG,
                        String.format("Error response when closing session, response %s",
                                PtpConstants.responseToString(responseCode)));
            }
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, Operation.CloseSession);
    }
}
