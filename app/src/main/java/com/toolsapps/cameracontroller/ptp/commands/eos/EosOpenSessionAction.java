package com.toolsapps.cameracontroller.ptp.commands.eos;

import com.toolsapps.cameracontroller.ptp.EosCamera;
import com.toolsapps.cameracontroller.ptp.PtpAction;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.commands.OpenSessionCommand;

public class EosOpenSessionAction implements PtpAction {

    private final EosCamera camera;

    public EosOpenSessionAction(EosCamera camera) {
        this.camera = camera;
    }

    @Override
    public void exec(IO io) {
        OpenSessionCommand openSession = new OpenSessionCommand(camera);
        io.handleCommand(openSession);
        if (openSession.getResponseCode() == PtpConstants.Response.Ok) {
            EosSetPcModeCommand setPcMode = new EosSetPcModeCommand(camera);
            io.handleCommand(setPcMode);
            if (setPcMode.getResponseCode() == PtpConstants.Response.Ok) {
                EosSetExtendedEventInfoCommand c = new EosSetExtendedEventInfoCommand(camera);
                io.handleCommand(c);
                if (c.getResponseCode() == PtpConstants.Response.Ok) {
                    camera.onSessionOpened();
                    return;
                } else {
                    camera.onPtpError(String.format(
                            "Couldn't open session! Setting extended event info failed with error code \"%s\"",
                            PtpConstants.responseToString(c.getResponseCode())));
                }
            } else {
                camera.onPtpError(String.format(
                        "Couldn't open session! Setting PcMode property failed with error code \"%s\"",
                        PtpConstants.responseToString(setPcMode.getResponseCode())));
            }
        } else {
            camera.onPtpError(String.format(
                    "Couldn't open session! Open session command failed with error code \"%s\"",
                    PtpConstants.responseToString(openSession.getResponseCode())));
        }
    }

    @Override
    public void reset() {
    }
}
