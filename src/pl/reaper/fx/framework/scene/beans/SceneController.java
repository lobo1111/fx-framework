package pl.reaper.fx.framework.scene.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import pl.reaper.fx.framework.scene.beans.annotations.SceneEventHandler;
import pl.reaper.fx.framework.scene.events.SceneEvent;
import pl.reaper.fx.framework.scene.events.stage.StageClosed;

public abstract class SceneController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ControllersManager.getInstance().addController(this);
        onInit();
    }

    public void fireEvent(SceneEvent event) {
        ControllersManager.getInstance().fireEvent(event);
    }

    protected abstract void onInit();

    @SceneEventHandler
    public void stageClosed(StageClosed event) {
        if (event.getController().getCanonicalName().equals(this.getClass().getCanonicalName())) {
            disposeController();
        }
    }

    public void disposeController() {
        if (!SceneLoader.isSingleton(this.getClass())) {
            disposeChildControllers();
            ControllersManager.getInstance().removeController(this);
        }

    }

    private void disposeChildControllers() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (SceneController.class.isAssignableFrom(field.getType())) {
                disposeChildController(field);
            }
        }
    }

    private void disposeChildController(Field field) {
        try {
            field.setAccessible(true);
            Method disposeMethod = field.getType().getMethod("disposeController");
            disposeMethod.invoke(field.get(this));
        } catch (Exception ex) {
            Logger.getLogger(SceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().getCanonicalName().equals(this.getClass().getCanonicalName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
