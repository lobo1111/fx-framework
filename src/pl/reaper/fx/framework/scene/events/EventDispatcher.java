package pl.reaper.fx.framework.scene.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.reaper.fx.framework.scene.SceneController;
import pl.reaper.fx.framework.scene.annotations.SceneEventHandler;

public class EventDispatcher {

    private List<Event> events = new ArrayList<>();

    private void callEvent(SceneController controller, SceneEvent event) {
        for (Method method : controller.getClass().getMethods()) {
            if (isAnnotated(method) && isParameterTypeValid(method, event)) {
                call(method, controller, event);
            }
        }
    }

    private void call(Method method, SceneController controller, SceneEvent event) {
        try {
            method.invoke(controller, event);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(EventDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isAnnotated(Method method) {
        return method.isAnnotationPresent(SceneEventHandler.class);
    }

    private boolean isParameterTypeValid(Method method, SceneEvent event) {
        return method.getParameterTypes().length == 1
                && method.getParameterTypes()[0].isAssignableFrom(event.getClass());
    }

    public void prepareEventCall(SceneController controller, SceneEvent event) {
        events.add(new Event(controller, event));
    }

    public void callEvents() {
        for(Event event: events) {
            callEvent(event.controller, event.event);
        }
    }

    private class Event {

        private SceneController controller;
        private SceneEvent event;

        public Event(SceneController controller, SceneEvent event) {
            this.controller = controller;
            this.event = event;
        }
    }
}
