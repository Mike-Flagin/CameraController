package com.toolsapps.cameracontroller.ptp.commands.nikon;

import com.toolsapps.cameracontroller.ptp.NikonCamera;
import com.toolsapps.cameracontroller.ptp.PtpAction;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;
import com.toolsapps.cameracontroller.ptp.commands.SimpleCommand;

public class NikonStartLiveViewAction implements PtpAction {

    private final NikonCamera camera;

    public NikonStartLiveViewAction(NikonCamera camera) {
        this.camera = camera;
    }

    @Override
    public void exec(IO io) {
        SimpleCommand simpleCmd = new SimpleCommand(camera, Operation.NikonStartLiveView);
        io.handleCommand(simpleCmd);

        if (simpleCmd.getResponseCode() != Response.Ok) {
            return;
        }

        SimpleCommand deviceReady = new SimpleCommand(camera, Operation.NikonDeviceReady);
        for (int i = 0; i < 10; ++i) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // nop
            }

            deviceReady.reset();
            io.handleCommand(deviceReady);
            if (deviceReady.getResponseCode() == Response.DeviceBusy) {
                // still waiting
            } else if (deviceReady.getResponseCode() == Response.Ok) {
                camera.onLiveViewStarted();
                return;
            } else {
                return;
            }
        }
    }

    @Override
    public void reset() {
    }
}
