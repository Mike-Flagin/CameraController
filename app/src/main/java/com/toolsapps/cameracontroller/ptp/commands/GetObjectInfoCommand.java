package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.AppConfig;
import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;
import com.toolsapps.cameracontroller.ptp.model.ObjectInfo;

import android.util.Log;

public class GetObjectInfoCommand extends Command {

    private final String TAG = GetObjectInfoCommand.class.getSimpleName();

    private final int outObjectHandle;
    private ObjectInfo inObjectInfo;

    public GetObjectInfoCommand(PtpCamera camera, int objectHandle) {
        super(camera);
        this.outObjectHandle = objectHandle;
    }

    public ObjectInfo getObjectInfo() {
        return inObjectInfo;
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
        if (responseCode == Response.DeviceBusy) {
            camera.onDeviceBusy(this, true);
        }
        if (inObjectInfo != null) {
            if (AppConfig.LOG) {
                Log.i(TAG, inObjectInfo.toString());
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        inObjectInfo = null;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.GetObjectInfo, outObjectHandle);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        inObjectInfo = new ObjectInfo(b, length);
    }
}
