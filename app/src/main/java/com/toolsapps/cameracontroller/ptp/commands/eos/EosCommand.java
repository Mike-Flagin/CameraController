package com.toolsapps.cameracontroller.ptp.commands.eos;

import com.toolsapps.cameracontroller.ptp.EosCamera;
import com.toolsapps.cameracontroller.ptp.commands.Command;

public abstract class EosCommand extends Command {

    protected EosCamera camera;

    public EosCommand(EosCamera camera) {
        super(camera);
        this.camera = camera;
    }
}
