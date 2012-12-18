package pl.reaper.fx.framework.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.reaper.fx.framework.scene.annotations.FXMLController;
import pl.reaper.fx.framework.scene.events.EventDispatcher;
import pl.reaper.fx.framework.scene.events.SceneEvent;
import pl.reaper.fx.framework.scene.exceptions.NoFXMLControllerAnnotation;
import pl.reaper.fx.framework.scene.helpers.SceneLoader;

public class ControllersManager {

    private List<SceneController> controllers = new ArrayList<>();

    public SceneController getController(Class<? extends SceneController> requestedScene) {
        checkForFXMLControllerAnnotation(requestedScene);
        SceneController controller;
        if (SceneController.isSingleton(requestedScene) && isInitialized(requestedScene)) {
            controller = findController(requestedScene);
            Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller loaded {0} id:{1}", new Object[]{requestedScene.getCanonicalName(), controller.getId()});
        } else {
            controller = new SceneLoader().initController(requestedScene);
            Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller initialized {0} id:{1}", new Object[]{requestedScene.getCanonicalName(), controller.getId()});
        }
        return controller;
    }

    public void removeController(SceneController controller) {
        Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller {0} removed from cache id:{1}", new Object[]{controller.getClass().getCanonicalName(), controller.getId()});
        controllers.remove(controller);
    }

    public synchronized void fireEvent(SceneEvent event) {
        EventDispatcher dispatcher = new EventDispatcher();
        for (SceneController controller : controllers) {
            dispatcher.prepareEventCall(controller, event);
        }
        dispatcher.callEvents();
    }

    public boolean isInitialized(Class<? extends SceneController> requestedScene) {
        return findController(requestedScene) != null;
    }

    public synchronized void addController(SceneController controller) {
        Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller {0} cached id:{1}", new Object[]{controller.getClass().getCanonicalName(), controller.getId()});
        controllers.add(controller);
    }

    private synchronized SceneController findController(Class<? extends SceneController> requestedController) {
        for (SceneController controller : controllers) {
            if (controller.getClass().isAssignableFrom(requestedController)) {
                Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller {0} found - {1}", new Object[]{requestedController.getCanonicalName(), controller.getId()});
                return controller;
            }
        }
        Logger.getLogger(SceneLoader.class.getName()).log(Level.INFO, "Controller {0} not found", requestedController.getCanonicalName());
        return null;
    }

    private void checkForFXMLControllerAnnotation(Class<? extends SceneController> requestedController) {
        if (!requestedController.isAnnotationPresent(FXMLController.class)) {
            throw new NoFXMLControllerAnnotation("Controller " + requestedController.getCanonicalName() + " does not have FXMLController annotation !");
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
