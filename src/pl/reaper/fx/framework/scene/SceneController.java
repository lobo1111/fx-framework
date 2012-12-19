package pl.reaper.fx.framework.scene;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import pl.reaper.fx.framework.scene.annotations.FXMLController;
import pl.reaper.fx.framework.scene.annotations.SceneEventHandler;
import pl.reaper.fx.framework.scene.events.SceneEvent;
import pl.reaper.fx.framework.scene.events.stage.CloseStage;
import pl.reaper.fx.framework.scene.exceptions.ForbiddenSingletonInitialization;
import pl.reaper.fx.framework.scene.helpers.SceneLoader;

public abstract class SceneController implements Initializable {

    private UUID id = UUID.randomUUID();
    private Parent fxml;
    private Stage stage;

    public UUID getId() {
        return id;
    }

    public Parent getFxml() {
        return fxml;
    }

    public void setFxml(Parent fxml) {
        this.fxml = fxml;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void fireEvent(SceneEvent event) {
        ControllersManager.getInstance().fireEvent(event);
    }

    @SceneEventHandler
    public void closeEventHandler(CloseStage event) {
        if (event.getController().equals(this)) {
            close();
        }
    }

    public void close() {
        dispose();
        handleStage();
    }

    public void dispose() {
        if (!SceneController.isSingleton(this.getClass())) {
            Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Disposing controller {0} id:{1}", new Object[]{getClass(), getId()});
            for (Field field : this.getClass().getDeclaredFields()) {
                checkField(field);
            }
            ControllersManager.getInstance().removeController(this);
        } else {
            Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller {0} id:{1} is a singleton, will not be disposed", new Object[]{getClass(), getId()});
        }
    }

    public static boolean isSingleton(Class<? extends SceneController> requestedController) {
        return requestedController.isAnnotationPresent(FXMLController.class)
                && requestedController.getAnnotation(FXMLController.class).singleton();
    }

    private void checkField(Field field) {
        if (SceneController.class.isAssignableFrom(field.getType())) {
            try {
                field.setAccessible(true);
                Method disposeMethod = field.getType().getMethod("dispose");
                disposeMethod.invoke(field.get(this));
            } catch (Exception ex) {
                Logger.getLogger(SceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void handleStage() {
        if (stage != null) {
            if (SceneController.isSingleton(this.getClass())) {
                stage.hide();
            } else {
                stage.close();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SceneController other = (SceneController) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SceneController.isSingleton(getClass()) && ControllersManager.getInstance().isInitialized(getClass())) {
            throw new ForbiddenSingletonInitialization("Singleton can't be included by fx:include tag.");
        }
        ControllersManager.getInstance().addController(this);
        onInit();
    }

    protected void onInit() {
    }
}
