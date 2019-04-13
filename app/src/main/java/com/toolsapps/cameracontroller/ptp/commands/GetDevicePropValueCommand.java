package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Datatype;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;

public class GetDevicePropValueCommand extends Command {

    private final int property;
    private final int datatype;
    private int value;

    public GetDevicePropValueCommand(PtpCamera camera, int property, int datatype) {
        super(camera);
        this.property = property;
        this.datatype = datatype;
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
        if (responseCode == Response.DeviceBusy) {
            camera.onDeviceBusy(this, true);
        }
        if (responseCode == Response.Ok) {
            camera.onPropertyChanged(property, value);
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.GetDevicePropValue, property);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        if (datatype == Datatype.int8) {
            value = b.get();
        } else if (datatype == Datatype.uint8) {
            value = b.get() & 0xFF;
        } else if (datatype == Datatype.uint16) {
            value = b.getShort() & 0xFFFF;
        } else if (datatype == Datatype.int16) {
            value = b.getShort();
        } else if (datatype == Datatype.int32 || datatype == Datatype.uint32) {
            value = b.getInt();
        }
    }
}
