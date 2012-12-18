package pl.reaper.fx.framework.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.reaper.fx.framework.scene.events.EventDispatcher;
import pl.reaper.fx.framework.scene.events.SceneEvent;
import pl.reaper.fx.framework.scene.helpers.SceneLoader;

public class ControllersManager {

    private List<SceneController> controllers = new ArrayList<>();

    public SceneController getController(Class<? extends SceneController> requestedScene) {
        SceneController controller;
        if (SceneController.isSingleton(requestedScene) && isInitialized(requestedScene)) {
            controller = findController(requestedScene);
            Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "Controller loaded {0} id:{1}", new Object[]{requestedScene.getCanonicalName(), controller.getId()});

        } else {
            controller = new SceneLoader().initController(requestedScene);
            Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "Controller initialized {0} id:{1}", new Object[]{requestedScene.getCanonicalName(), controller.getId()});
            addController(controller);
        }
        return controller;
    }

    public SceneController getController(String parentRootId) {
        for (SceneController controller : controllers) {
            if (controller.getRootId().equals(parentRootId)) {
                return controller;
            }
        }
        return null;
    }

    public void removeController(SceneController controller) {
        controllers.remove(controller);
    }

    public synchronized void fireEvent(SceneEvent event) {
        EventDispatcher dispatcher = new EventDispatcher();
        for (SceneController controller : controllers) {
            dispatcher.prepareEventCall(controller, event);
        }
        dispatcher.callEvents();
    }

    private boolean isInitialized(Class<? extends SceneController> requestedScene) {
        return findController(requestedScene) != null;
    }

    private synchronized void addController(SceneController controller) {
        controllers.add(controller);
    }

    private synchronized SceneController findController(Class<? extends SceneController> requestedController) {
        for (SceneController controller : controllers) {
            if (controller.getClass().isAssignableFrom(requestedController)) {
                Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "Controller {0} found - {1]", new Object[]{requestedController.getCanonicalName(), controller.getId()});
                return controller;
            }
        }
        Logger.getLogger(SceneLoader.class.getName()).log(Level.SEVERE, "Controller {0} not found", requestedController.getCanonicalName());
        return null;
    }

    public synchronized boolean isParent(String id) {
        for (SceneController controller : controllers) {
            if (controller.getRootId().equals(id)) {
                return true;
            }
        }
        return false;
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
