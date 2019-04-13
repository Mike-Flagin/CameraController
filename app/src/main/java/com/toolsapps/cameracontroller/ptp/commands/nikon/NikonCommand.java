package com.toolsapps.cameracontroller.ptp.commands.nikon;

import com.toolsapps.cameracontroller.ptp.NikonCamera;
import com.toolsapps.cameracontroller.ptp.commands.Command;

public abstract class NikonCommand extends Command {

    protected NikonCamera camera;

    public NikonCommand(NikonCamera camera) {
        super(camera);
        this.camera = camera;
    }
}
