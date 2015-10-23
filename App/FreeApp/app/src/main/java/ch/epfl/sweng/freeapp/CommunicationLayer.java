package ch.epfl.sweng.freeapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by lois on 10/20/15.
 */
public class CommunicationLayer {

    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private NetworkProvider networkProvider;
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private String cookieSession;





    /**
     * Creates a new CommunicationLayer instance that communicates with the
     * server at a given location, through a NetworkProvider object.
     *
     * The communication layer is similar to the concept of quizClient presented in hw2
     * Its purposes are:
     * 1.Establish a connection with the server
     * 2.Get responses from the server and return them to the app
     */
    public CommunicationLayer(NetworkProvider networkProvider) {
        this.networkProvider = networkProvider;
    }

    /**
     * Used by the app to
     * send user-entered log in information to the user
     * @param logInInfo
     * @return "ok" if the operation was successful, or "failed" otherwise
     */
    public ResponseStatus sendLogInInfo(LogInInfo logInInfo) throws CommunicationLayerException {
        //Note: needs to use a method from the server (such as myServer.checkLogInInfo(logInInfo))

        try {

            URL url = new URL(SERVER_URL + "/login?user=" + logInInfo.getUsername() +"&password=" + logInInfo.getPassword() + ")");

            HttpURLConnection conn = networkProvider.getConnection(url);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                throw new CommunicationLayerException("Invalid HTTP response code");
            }

            String serverResponseString = fetchContent(conn);
            JSONObject serverResponse = new JSONObject(serverResponseString);
            JSONObject logInJson = serverResponse.getJSONObject("login");

            if(serverResponse.getString("status").equals("failure")){

                switch (logInJson.getString("reason")) {

                    case "username": return ResponseStatus.USERNAME;

                    case "password": return ResponseStatus.PASSWORD;

                    default: throw new CommunicationLayerException();
                }
            }

            assert(logInJson.get("status").equals("ok"));

            this.cookieSession = logInJson.getString("cookie");



            return ResponseStatus.OK;

        } catch (IOException e) {
            throw new CommunicationLayerException();

        } catch (JSONException e) {
            throw new CommunicationLayerException();

        }

    }

    /**
     * Used by the app to
     * send user-entered registration information to the user
     * @param registrationInfo
     * @return "ok" if the operation was successful, or "failed" otherwise
     */

    public ResponseStatus sendRegistrationInfo(RegistrationInfo registrationInfo)  throws CommunicationLayerException {

        //Note: needs to use a method from the server (such as myServer.createAccount(registrationInfo))

        if(registrationInfo == null ){
            throw new CommunicationLayerException("null registrationInfo");
        }

        try {

            URL url = new URL(SERVER_URL + "register?user="+registrationInfo.getUsername()+"&password="+registrationInfo.getPassword()+"&email="+registrationInfo.getEmail());
            HttpURLConnection conn = networkProvider.getConnection(url);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                throw new CommunicationLayerException("Invalid HTTP response code");
            }

            String serverResponse = fetchContent(conn);
            JSONObject jsonObject = new JSONObject(serverResponse);

            JSONObject serverResponseJson = jsonObject.getJSONObject("register");

            if(serverResponseJson.getString("status").equals("failure")){

                switch(serverResponseJson.getString("reason")){

                    case "email" : return ResponseStatus.EMAIL;

                    case "password": return ResponseStatus.PASSWORD;

                    case "user"  : return ResponseStatus.USERNAME;

                    default: throw new CommunicationLayerException();
                }
            }

            assert(serverResponseJson.getString("status").equals("ok"));

            return  ResponseStatus.OK;

        }catch(IOException e ){
            throw new CommunicationLayerException();
        }catch(JSONException e ){
            throw new CommunicationLayerException();
        }


    }


    private String fetchContent(HttpURLConnection conn) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

            String result = out.toString();
            Log.d("HTTPFetchContent", "Fetched string of length "
                    + result.length());
            return result;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    /*
    public ResponseStatus getOK(){

        return  ResponseStatus.OK;

    }
    public ResponseStatus getPass(){
        return ResponseStatus.PASSWORD;

    }
    public ResponseStatus getUser(){
        return ResponseStatus.USERNAME;

    }
    public ResponseStatus getMail(){
        return ResponseStatus.EMAIL;

    }

    */



}


