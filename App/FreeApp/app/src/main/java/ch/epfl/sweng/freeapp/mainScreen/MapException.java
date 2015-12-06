package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Created by lois on 11/26/15.
 */
public class MapException extends Exception {

    private String message;

    public MapException() {
        super();
    }

    public MapException(String message) {
        super(message);
    }

    public MapException(Throwable throwable) {
        super(throwable);

    }
}
