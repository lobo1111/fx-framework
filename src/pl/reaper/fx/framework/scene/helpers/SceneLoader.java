package pl.reaper.fx.framework.scene.helpers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.annotations.FXMLController;

public class SceneLoader {

    public SceneController initController(Class<? extends SceneController> requestedController) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            String fxmlPath = requestedController.getAnnotation(FXMLController.class).fxmlPath();
            Parent parent = (Parent) fxmlLoader.load(getClass().getResource(fxmlPath).openStream());
            SceneController controller = (SceneController) fxmlLoader.getController();
            controller.setFxml(parent);
            return controller;
        } catch (IOException ex) {
            Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "SceneController init exception", ex);
        }
        return null;

    }
}
