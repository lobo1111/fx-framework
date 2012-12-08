package pl.reaper.fx.framework.scene.events;

public abstract class SceneEvent {

    private String message;

    public SceneEvent() {
    }

    public SceneEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
