package pl.reaper.fx.framework.scene.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.reaper.fx.framework.scene.beans.annotations.SceneEventHandler;
import pl.reaper.fx.framework.scene.events.SceneEvent;

public class ControllersManager {

    private List<SceneController> controllers = new ArrayList<>();

    public void addController(SceneController controller) {
        Logger.getLogger(ControllersManager.class.getName()).log(Level.INFO, "Controller added: {0}", controller.getClass().getCanonicalName());
        controllers.add(controller);
    }

    public void removeController(SceneController controller) {
        Logger.getLogger(ControllersManager.class.getName()).log(Level.INFO, "Controller removed: {0}", controller.getClass().getCanonicalName());
        controllers.remove(controller);
    }

    public void fireEvent(SceneEvent event) {
        for (int i = 0; i < controllers.size(); i++) {
            callEvents(controllers.get(i), event);
        }
    }

    private void callEvents(SceneController controller, SceneEvent event) {
        for (Method method : controller.getClass().getMethods()) {
            if (method.isAnnotationPresent(SceneEventHandler.class)
                    && method.getName().equalsIgnoreCase(event.getClass().getSimpleName())) {
                call(method, controller, event);
            }
        }
    }

    private void call(Method method, SceneController controller, SceneEvent event) {
        try {
            method.invoke(controller, event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ControllersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ControllersManager() {
    }

    private static class ControllersManagerHolder {

        private static final ControllersManager INSTANCE = new ControllersManager();
    }

    public static ControllersManager getInstance() {
        return ControllersManagerHolder.INSTANCE;
    }
}
