package ch.epfl.sweng.freeapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by francisdamachi on 22/10/15.
 */
public interface NetworkProvider {


    public HttpURLConnection getConnection(URL url) throws IOException;

}
