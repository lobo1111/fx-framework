package pl.reaper.fx.framework.scene.events.stage;

import pl.reaper.fx.framework.scene.beans.SceneController;
import pl.reaper.fx.framework.scene.events.SceneEvent;

public class StageClosed extends SceneEvent {

    private Class<? extends SceneController> controller;

    public StageClosed(Class<? extends SceneController> controller) {
        this.controller = controller;
    }

    public Class<? extends SceneController> getController() {
        return controller;
    }
}
