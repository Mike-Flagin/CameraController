package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

public class OpenSessionCommand extends Command {

    public OpenSessionCommand(PtpCamera camera) {
        super(camera);
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
        if (responseCode == Response.Ok) {
            camera.onSessionOpened();
        } else {
            camera.onPtpError(String.format("Couldn't open session, error code \"%s\"",
                    PtpConstants.responseToString(responseCode)));
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        camera.resetTransactionId();
        encodeCommand(b, Operation.OpenSession, 1);
    }
}
