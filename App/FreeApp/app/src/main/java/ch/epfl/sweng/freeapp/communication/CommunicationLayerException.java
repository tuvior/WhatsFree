package ch.epfl.sweng.freeapp.communication;

/**
 * An exception class created each time there is a problem with the server or communication layer
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
