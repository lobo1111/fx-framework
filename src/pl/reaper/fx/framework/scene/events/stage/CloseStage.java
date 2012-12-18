package pl.reaper.fx.framework.scene.events.stage;

import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.events.SceneEvent;

public class CloseStage extends SceneEvent {

    private SceneController controller;

    public CloseStage(SceneController controller) {
        this.controller = controller;
    }

    public SceneController getController() {
        return controller;
    }
}
