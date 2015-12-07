package ch.epfl.sweng.freeapp.communication;

/**
 * Created by francisdamachi on 29/11/15.
 */
public class ProvideCommunicationLayer {


    private static DefaultCommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

    public static DefaultCommunicationLayer getCommunicationLayer() {
        return communicationLayer;
    }

    public static void setCommunicationLayer(DefaultCommunicationLayer communicationLayer1) {

        communicationLayer = communicationLayer1;

    }

}
