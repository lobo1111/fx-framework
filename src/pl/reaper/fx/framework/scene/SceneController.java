package pl.reaper.fx.framework.scene;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import pl.reaper.fx.framework.scene.annotations.FXMLController;
import pl.reaper.fx.framework.scene.annotations.SceneEventHandler;
import pl.reaper.fx.framework.scene.events.SceneEvent;
import pl.reaper.fx.framework.scene.events.stage.CloseStage;
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
    public void close(CloseStage event) {
        if (event.getController().equals(this)) {
            dispose();
            handleStage();
        }
    }

    public void dispose() {
        Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "Disposing controller {0} id:{1}", new Object[]{getClass(), getId()});
        if (!SceneController.isSingleton(this.getClass())) {
            checkFields(fxml.getChildrenUnmodifiable());
        }
    }

    public static boolean isSingleton(Class<? extends SceneController> requestedController) {
        return requestedController.isAnnotationPresent(FXMLController.class)
                && requestedController.getAnnotation(FXMLController.class).singleton();
    }

    public String getRootId() {
        if (this.getClass().isAnnotationPresent(FXMLController.class)) {
            return this.getClass().getAnnotation(FXMLController.class).fxmlRootId();
        } else {
            return null;
        }
    }

    private void checkFields(ObservableList<Node> children) {
        for (Node child : children) {
            if (ControllersManager.getInstance().isParent(child.getId())) {
                SceneController controller = ControllersManager.getInstance().getController(child.getId());
                checkFields(controller.getFxml().getChildrenUnmodifiable());
            }
        }
        ControllersManager.getInstance().removeController(this);
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
}
