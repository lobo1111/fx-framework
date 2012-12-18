package pl.reaper.fx.framework.scene;

import java.util.HashMap;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.reaper.fx.framework.scene.events.stage.CloseStage;

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
                        ControllersManager.getInstance().fireEvent(new CloseStage(controller));
                    }
                });
        stage.setScene(new Scene(controller.getFxml()));
        return stage;
    }

    private StagesManager() {
    }

    private static class StagesManagerHolder {

        private static final StagesManager INSTANCE = new StagesManager();
    }

    public static StagesManager getInstance() {
        return StagesManagerHolder.INSTANCE;
    }
}
