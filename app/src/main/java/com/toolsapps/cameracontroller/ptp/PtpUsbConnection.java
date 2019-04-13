package com.toolsapps.cameracontroller.ptp;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;

public class PtpUsbConnection {

    private final UsbDeviceConnection connection;
    private final UsbEndpoint bulkOut;
    private final UsbEndpoint bulkIn;
    private final int vendorId;
    private final int productId;

    public PtpUsbConnection(UsbDeviceConnection connection, UsbEndpoint bulkIn, UsbEndpoint bulkOut, int vendorId,
            int productId) {
        this.connection = connection;
        this.bulkIn = bulkIn;
        this.bulkOut = bulkOut;
        this.vendorId = vendorId;
        this.productId = productId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public int getProductId() {
        return productId;
    }

    public void close() {
        connection.close();
    }

    public int getMaxPacketInSize() {
        return bulkIn.getMaxPacketSize();
    }

    public int getMaxPacketOutSize() {
        return bulkOut.getMaxPacketSize();
    }

    public UsbRequest createInRequest() {
        UsbRequest r = new UsbRequest();
        r.initialize(connection, bulkIn);
        return r;
    }

    public int bulkTransferOut(byte[] buffer, int length, int timeout) {
        return connection.bulkTransfer(bulkOut, buffer, length, timeout);
    }

    public int bulkTransferIn(byte[] buffer, int maxLength, int timeout) {
        return connection.bulkTransfer(bulkIn, buffer, maxLength, timeout);
    }

    public void requestWait() {
        connection.requestWait();
    }
}
