package com.toolsapps.cameracontroller.ptp.commands;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.toolsapps.cameracontroller.ptp.PtpCamera;
import com.toolsapps.cameracontroller.ptp.PtpCamera.IO;
import com.toolsapps.cameracontroller.ptp.PtpConstants;

/**
 * Read file data from camera with specified {@code objectHandle}.
 */
public class GetObjectCommand extends Command {

    private static final String TAG = GetObjectCommand.class.getSimpleName();

    private final int objectHandle;

    private final BitmapFactory.Options options;
    private Bitmap inBitmap;

    private boolean outOfMemoryError;

    public GetObjectCommand(PtpCamera camera, int objectHandle, int sampleSize) {
        super(camera);
        this.objectHandle = objectHandle;
        options = new BitmapFactory.Options();
        if (sampleSize >= 1 && sampleSize <= 4) {
            options.inSampleSize = sampleSize;
        } else {
            options.inSampleSize = 2;
        }
    }

    public Bitmap getBitmap() {
        return inBitmap;
    }

    public boolean isOutOfMemoryError() {
        return outOfMemoryError;
    }

    @Override
    public void exec(IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        super.reset();
        inBitmap = null;
        outOfMemoryError = false;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.GetObject, objectHandle);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        try {
            // 12 == offset of data header
            inBitmap = BitmapFactory.decodeByteArray(b.array(), 12, length - 12, options);
        } catch (RuntimeException e) {
            Log.i(TAG, "exception on decoding picture : " + e.toString());
        } catch (OutOfMemoryError e) {
            System.gc();
            outOfMemoryError = true;
        }
    }
}
