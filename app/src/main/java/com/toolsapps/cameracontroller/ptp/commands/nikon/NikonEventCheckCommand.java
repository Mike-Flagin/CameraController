package com.toolsapps.cameracontroller.ptp.commands.nikon;

import android.util.Log;

import com.toolsapps.cameracontroller.AppConfig;
import com.toolsapps.cameracontroller.ptp.NikonCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Event;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Operation;

import java.nio.ByteBuffer;

public class NikonEventCheckCommand extends NikonCommand {

    private static final String TAG = NikonEventCheckCommand.class.getSimpleName();

    public NikonEventCheckCommand(NikonCamera camera) {
        super(camera);
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, Operation.NikonGetEvent);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        int count = b.getShort();

        while (count > 0) {
            --count;

            int eventCode = b.getShort();
            int eventParam = b.getInt();

            if (AppConfig.LOG) {
                Log.i(TAG,
                        String.format("event %s value %s(%04x)", PtpConstants.eventToString(eventCode),
                                PtpConstants.propertyToString(eventParam), eventParam));
            }

            switch (eventCode) {
            case Event.ObjectAdded:
                camera.onEventObjectAdded(eventParam);
                break;
            case Event.DevicePropChanged:
                camera.onEventDevicePropChanged(eventParam);
                break;
            case Event.CaptureComplete:
                camera.onEventCaptureComplete();
                break;
            case Event.NikonCaptureCompleteRecInSdram:
                camera.onEventCpatureCompleteRecInSdram();
                break;
            }
        }
    }
}
