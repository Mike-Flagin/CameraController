package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

public class InitiateCaptureCommand extends Command {

    public InitiateCaptureCommand(PtpCamera camera) {
        super(camera);
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
        if (responseCode == Response.DeviceBusy) {
            camera.onDeviceBusy(this, true); // TODO when nikon live view is enabled this stalls
            return;
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, Operation.InitiateCapture, 0, 0);
    }
}
