package pl.reaper.fx.framework.scene.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.reaper.fx.framework.scene.beans.SceneController;
import pl.reaper.fx.framework.scene.beans.annotations.SceneEventHandler;

public class EventDispatcher {

    public static void callEvent(SceneController controller, SceneEvent event) {
        for (Method method : controller.getClass().getMethods()) {
            if (isAnnotated(method) && isParameterTypeValid(method, event)) {
                call(method, controller, event);
            }
        }
    }

    private static void call(Method method, SceneController controller, SceneEvent event) {
        try {
            method.invoke(controller, event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(EventDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isAnnotated(Method method) {
        return method.isAnnotationPresent(SceneEventHandler.class);
    }

    private static boolean isParameterTypeValid(Method method, SceneEvent event) {
        return method.getParameterTypes().length == 1
                && method.getParameterTypes()[0].isAssignableFrom(event.getClass());
    }
}
