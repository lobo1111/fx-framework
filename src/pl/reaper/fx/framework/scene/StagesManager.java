package pl.reaper.fx.framework.scene;

import java.util.HashMap;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.events.stage.StageClosed;

public class StagesManager {

    private Map<SceneController, Stage> stages = new HashMap<>();

    public Stage getStage(SceneController controller) {
        if (SceneController.isSingleton(controller.getClass())) {
            if (!stages.containsKey(controller)) {
                stages.put(controller, initStage(controller));
            }
            return stages.get(controller);
        } else {
            return initStage(controller);
        }
    }

    private Stage initStage(final SceneController controller) {
        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(final WindowEvent event) {
                        ControllersManager.getInstance().fireEvent(new StageClosed(controller));
                    }
                });
        stage.setScene(new Scene(ControllersManager.getInstance().getController(controller.getClass()).getFxml()));
        return stage;
    }
}
