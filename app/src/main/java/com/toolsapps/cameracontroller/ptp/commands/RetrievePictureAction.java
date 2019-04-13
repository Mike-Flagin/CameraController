package com.toolsapps.cameracontroller.ptp.commands;

import android.graphics.Bitmap;

import com.toolsapps.cameracontroller.ptp.PtpAction;
import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants;
import com.toolsapps.cameracontroller.ptp.PtpConstants.Response;
import com.toolsapps.cameracontroller.ptp.model.ObjectInfo;

public class RetrievePictureAction implements PtpAction {

    private final PtpCamera camera;
    private final int objectHandle;
    private final int sampleSize;

    public RetrievePictureAction(PtpCamera camera, int objectHandle, int sampleSize) {
        this.camera = camera;
        this.objectHandle = objectHandle;
        this.sampleSize = sampleSize;
    }

    @Override
    public void exec(IO io) {
        GetObjectInfoCommand getInfo = new GetObjectInfoCommand(camera, objectHandle);
        io.handleCommand(getInfo);

        if (getInfo.getResponseCode() != Response.Ok) {
            return;
        }

        ObjectInfo objectInfo = getInfo.getObjectInfo();
        if (objectInfo == null) {
            return;
        }

        Bitmap thumbnail = null;
        if (objectInfo.thumbFormat == PtpConstants.ObjectFormat.JFIF
                || objectInfo.thumbFormat == PtpConstants.ObjectFormat.EXIF_JPEG) {
            GetThumb getThumb = new GetThumb(camera, objectHandle);
            io.handleCommand(getThumb);
            if (getThumb.getResponseCode() == Response.Ok) {
                thumbnail = getThumb.getBitmap();
            }
        }

        GetObjectCommand getObject = new GetObjectCommand(camera, objectHandle, sampleSize);
        io.handleCommand(getObject);

        if (getObject.getResponseCode() != Response.Ok) {
            return;
        }
        if (getObject.getBitmap() == null) {
            if (getObject.isOutOfMemoryError()) {
                camera.onPictureReceived(objectHandle, getInfo.getObjectInfo().filename, thumbnail, null);
            }
            return;
        }

        if (thumbnail == null) {
            // TODO resize real picture?
        }

        camera.onPictureReceived(objectHandle, getInfo.getObjectInfo().filename, thumbnail, getObject.getBitmap());
    }

    @Override
    public void reset() {
    }
}
