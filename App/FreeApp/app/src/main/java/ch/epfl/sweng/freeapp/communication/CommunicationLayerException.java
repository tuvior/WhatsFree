package ch.epfl.sweng.freeapp.communication;

/**
 * Created by francisdamachi on 22/10/15.
 */
public class CommunicationLayerException extends Exception {

    private String message;

    public CommunicationLayerException() {
        super();
    }

    public CommunicationLayerException(String message) {
        super(message);
    }

    public CommunicationLayerException(Throwable throwable) {
        super(throwable);

    }
}
