package com.toolsapps.cameracontroller.ptp.model;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

public class LiveViewData {

    public Bitmap bitmap;

    public int zoomFactor;
    public int zoomRectLeft;
    public int zoomRectTop;
    public int zoomRectRight;
    public int zoomRectBottom;

    public boolean hasHistogram;
    public ByteBuffer histogram;

    // dimensions are in bitmap size
    public boolean hasAfFrame;
    public int nikonAfFrameCenterX;
    public int nikonAfFrameCenterY;
    public int nikonAfFrameWidth;
    public int nikonAfFrameHeight;

    public int nikonWholeWidth;
    public int nikonWholeHeight;
}
