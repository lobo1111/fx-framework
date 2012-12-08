package pl.reaper.fx.framework.scene.beans;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.reaper.fx.framework.scene.beans.annotations.FXMLController;
import pl.reaper.fx.framework.scene.events.stage.StageClosed;

public class SceneLoader {

    private Map<Class<? extends SceneController>, Parent> singletons = new HashMap<>();
    private Map<Class<? extends SceneController>, Stage> stages = new HashMap<>();

    public Parent getRoot(Class<? extends SceneController> controller) {
        if (!isSingleton(controller) || !singletons.containsKey(controller)) {
            return initParent(controller);
        } else {
            return singletons.get(controller);
        }
    }

    private Parent initParent(Class<? extends SceneController> controller) {
        try {
            String fxmlPath = controller.getAnnotation(FXMLController.class).fxmlPath();
            Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Initiating root: {0}", fxmlPath);
            URL resource = getClass().getResource(fxmlPath);
            Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Resource path: {0}", resource);
            Parent root = FXMLLoader.load(resource);
            if (isSingleton(controller)) {
                singletons.put(controller, root);
            }
            return root;
        } catch (IOException ex) {
            Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "FXML resource file not found", ex);
        }
        return null;
    }

    public Stage createNewStage(final Class<? extends SceneController> controller, String title, boolean resizable) {
        if (!isSingleton(controller) || !singletons.containsKey(controller)) {
            Parent root = getRoot(controller);
            Scene scene = new Scene(root);
            Stage stage = initStage(controller, scene, title, resizable);
            if (isSingleton(controller)) {
                stages.put(controller, stage);
            }
            return stage;
        } else {
            Stage stage = stages.get(controller);
            stage.show();
            return stage;
        }
    }

    public void closeStage(Class<? extends SceneController> controller) {
        Stage stage = stages.get(controller);
        if (stage != null) {
            if (isSingleton(controller)) {
                stage.hide();
            } else {
                stages.remove(controller);
                stage.close();
            }
        }
    }

    public static boolean isSingleton(Class<? extends SceneController> controller) {
        return controller.isAnnotationPresent(FXMLController.class)
                && controller.getAnnotation(FXMLController.class).singleton();
    }

    private Stage initStage(final Class<? extends SceneController> controller, Scene scene, String title, boolean resizable) {
        Stage stage = new Stage();
        stage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(final WindowEvent event) {
                        ControllersManager.getInstance().fireEvent(new StageClosed(controller));
                    }
                });
        stage.setScene(scene);
        stage.setTitle(title);
        stage.resizableProperty().set(resizable);
        stage.show();
        return stage;
    }

    private SceneLoader() {
    }

    public static SceneLoader getInstance() {
        return SceneLoaderHolder.INSTANCE;
    }

    private static class SceneLoaderHolder {

        private static final SceneLoader INSTANCE = new SceneLoader();
    }
}
