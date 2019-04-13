package com.toolsapps.cameracontroller.ptp.commands.eos;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.ptp.EosCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

public class EosTakePictureCommand extends EosCommand {

    public EosTakePictureCommand(EosCamera camera) {
        super(camera);
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
        if (responseCode == Response.DeviceBusy) {
            camera.onDeviceBusy(this, true);
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, Operation.EosTakePicture);
    }
}
