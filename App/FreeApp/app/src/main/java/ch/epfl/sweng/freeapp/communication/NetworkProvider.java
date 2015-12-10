package ch.epfl.sweng.freeapp.communication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Just a basic basic Network Provider
 */
public interface NetworkProvider {


    HttpURLConnection getConnection(URL url) throws IOException;

}
