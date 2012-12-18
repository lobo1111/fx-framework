package pl.reaper.fx.framework.scene.exceptions;

public class ForbiddenSingletonInitialization extends RuntimeException {

    public ForbiddenSingletonInitialization(String message) {
        super(message);
    }
    
}
