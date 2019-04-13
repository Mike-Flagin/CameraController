package com.toolsapps.cameracontroller.ptp.commands.nikon;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.ptp.NikonCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

public class NikonAfDriveCommand extends NikonCommand {

    public NikonAfDriveCommand(NikonCamera camera) {
        super(camera);
    }

    @Override
    public void exec(IO io) {
        //        if (camera.isInActivationTypePhase()) {
        //            return;
        //        }
        io.handleCommand(this);
        if (getResponseCode() == Response.Ok) {
            camera.onFocusStarted();
            camera.enqueue(new NikonAfDriveDeviceReadyCommand(camera), 200);
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, Operation.NikonAfDrive);
    }
}
