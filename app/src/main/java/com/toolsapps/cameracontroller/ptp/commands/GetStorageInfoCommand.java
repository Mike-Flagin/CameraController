package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.model.StorageInfo;

public class GetStorageInfoCommand extends Command {

    private StorageInfo storageInfo;
    private final int storageId;

    public StorageInfo getStorageInfo() {
        return storageInfo;
    }

    public GetStorageInfoCommand(PtpCamera camera, int storageId) {
        super(camera);
        this.storageId = storageId;
    }

    @Override
    public void exec(IO io) {
        io.handleCommand(this);
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        super.encodeCommand(b, PtpConstants.Operation.GetStorageInfo, storageId);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        storageInfo = new StorageInfo(b, length);
    }
}
