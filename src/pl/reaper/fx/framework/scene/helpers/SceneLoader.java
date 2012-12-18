package pl.reaper.fx.framework.scene.helpers;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.annotations.FXMLController;

public class SceneLoader {

    public SceneController initController(Class<? extends SceneController> requestedController) {
        try {
            String fxmlPath = requestedController.getAnnotation(FXMLController.class).fxmlPath();
            URL location = getClass().getResource(fxmlPath);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent parent = (Parent) fxmlLoader.load(location.openStream());
            SceneController controller = (SceneController) fxmlLoader.getController();
            controller.setFxml(parent);
            return controller;
        } catch (IOException ex) {
            Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "SceneController init exception", ex);
        }
        return null;

    }
}
