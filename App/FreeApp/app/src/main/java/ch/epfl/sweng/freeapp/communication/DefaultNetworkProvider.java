package ch.epfl.sweng.freeapp.communication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by francisdamachi on 22/10/15.
 */
public class DefaultNetworkProvider implements NetworkProvider {
    @Override
    public HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection)url.openConnection();
    }
}
