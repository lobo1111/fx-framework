package pl.reaper.fx.framework.scene.events.stage;

import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.events.SceneEvent;

public class StageClosed extends SceneEvent {

    private SceneController controller;

    public StageClosed(SceneController controller) {
        this.controller = controller;
    }

    public SceneController getController() {
        return controller;
    }
}
