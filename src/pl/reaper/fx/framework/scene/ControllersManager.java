package pl.reaper.fx.framework.scene;

import java.util.ArrayList;
import java.util.List;
import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.helpers.SceneLoader;
import pl.reaper.fx.framework.scene.events.EventDispatcher;
import pl.reaper.fx.framework.scene.events.SceneEvent;

public class ControllersManager {

    private List<SceneController> controllers = new ArrayList<>();

    public SceneController getController(Class<? extends SceneController> requestedScene) {
        if (SceneController.isSingleton(requestedScene) && isInitialized(requestedScene)) {
            return findController(requestedScene);
        } else {
            SceneController controller = new SceneLoader().initController(requestedScene);
            addController(controller);
            return controller;
        }
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
        for (SceneController controller : controllers) {
            EventDispatcher.callEvent(controller, event);
        }
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
                return controller;
            }
        }
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
