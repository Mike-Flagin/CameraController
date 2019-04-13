package com.toolsapps.cameracontroller.ptp.commands;

import com.toolsapps.cameracontroller.ptp.PtpAction;
import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

public class RetrieveAddedObjectInfoAction implements PtpAction {

    private final PtpCamera camera;
    private final int objectHandle;

    public RetrieveAddedObjectInfoAction(PtpCamera camera, int objectHandle) {
        this.camera = camera;
        this.objectHandle = objectHandle;
    }

    @Override
    public void exec(IO io) {
        GetObjectInfoCommand getInfo = new GetObjectInfoCommand(camera, objectHandle);
        io.handleCommand(getInfo);

        if (getInfo.getResponseCode() != Response.Ok) {
            return;
        }
        if (getInfo.getObjectInfo() == null) {
            return;
        }

        camera.onEventObjectAdded(objectHandle, getInfo.getObjectInfo().objectFormat);
    }

    @Override
    public void reset() {
    }
}
