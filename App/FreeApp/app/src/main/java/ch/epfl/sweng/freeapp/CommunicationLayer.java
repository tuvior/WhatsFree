package ch.epfl.sweng.freeapp;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//- See more at: http://ictstars.com/en/how-to-make-http-post-request-with-android-studio/#sthash.UqWoZBGa.dpuf

public class CommunicationLayer implements  DefaultCommunicationLayer {
    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private NetworkProvider networkProvider;
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private String cookieSession;
    private final static String COOKIE_TEST = "BEY4L9lVSlA0hHQQ1ClTXYVUn5xwcr0BfYSKc7sw0Y54XYzWObTAsJ6PHQWPQVzO";

    /**
     * Creates a new CommunicationLayer instance that communicates with the
     * server at a given location, through a NetworkProvider object.
     * <p/>
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
     *
     * @param logInInfo
     * @return "ok" if the operation was successful, or "failed" otherwise
     */
    public ResponseStatus sendLogInInfo(LogInInfo logInInfo) throws CommunicationLayerException {
        try {
            //FIXME: refactor code related to connection + create a FakeNetworkProvider

            URL url = new URL(SERVER_URL + "/login?user=" + logInInfo.getUsername() + "&password=" + logInInfo.getPassword());

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
            if (logInJson.getString("status").equals("failure")) {
                switch (logInJson.getString("reason")) {
                    case "user":
                        return ResponseStatus.USERNAME;
                    case "password":
                        return ResponseStatus.PASSWORD;
                    default:
                        throw new CommunicationLayerException();
                }
            } else if (logInJson.getString("status").equals("invalid")) {
                return ResponseStatus.EMPTY;
            } else {

                assert (logInJson.get("status").equals("ok"));
                this.cookieSession = logInJson.getString("cookie");
                return ResponseStatus.OK;
            }
        } catch (IOException | JSONException e) {
            throw new CommunicationLayerException();
        }
    }

    /**
     * Used by the app to
     * send user-entered registration information to the user
     *
     * @param registrationInfo
     * @return "ok" if the operation was successful, or "failed" otherwise
     */
    public ResponseStatus sendRegistrationInfo(RegistrationInfo registrationInfo) throws CommunicationLayerException {
        //Note: needs to use a method from the server (such as myServer.createAccount(registrationInfo))
        if (registrationInfo == null) {
            throw new CommunicationLayerException("null registrationInfo");
        }
        try {
            URL url = new URL(SERVER_URL + "/register?user=" + registrationInfo.getUsername() + "&password=" + registrationInfo.getPassword() + "&email=" + registrationInfo.getEmail());


            HttpURLConnection conn = networkProvider.getConnection(url);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                throw new CommunicationLayerException("Invalid HTTP response code");
            }

            //Get Response
            String serverResponse = fetchContent(conn);
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONObject serverResponseJson = jsonObject.getJSONObject("register");
            if (serverResponseJson.getString("status").equals("failure")) {
                switch (serverResponseJson.getString("reason")) {
                    case "email":
                        return ResponseStatus.EMAIL;
                    case "password":
                        return ResponseStatus.PASSWORD;
                    case "user":
                        return ResponseStatus.USERNAME;
                    default:
                        throw new CommunicationLayerException();
                }
            }
            assert (serverResponseJson.getString("status").equals("ok"));
            return ResponseStatus.OK;
        } catch (IOException e) {
            throw new CommunicationLayerException("Server Unresponsive");
        } catch (JSONException e) {
            throw new CommunicationLayerException();
        }

    }


    /*
            //Must check which fields are missing and if they are allowed to not be specified before creating the URL
            URL url = new URL(SERVER_URL + "/add_submission?name=" + submission.getName() + "&category=" + submission.getCategory() +
                    "&location=" + submission.getLocation() + "&description=" + submission.getDescription() + "&keywords=" +
                    submission.getKeywords() + "&image=" + submission.getImage() + "&submitted=" + submission.getSubmitted() +
                    "&from=" + submission.getStartOfEvent() + "&to=" + submission.getEndOfEvent());
                    */


    @Override
        public ResponseStatus sendAddSubmissionRequest(Submission param) throws CommunicationLayerException {
            URL url;

            HttpURLConnection conn;

            try {

                url = new URL(SERVER_URL+"/submission");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("name", param.getName()));
                params.add(new BasicNameValuePair("category", param.getCategory()));
                params.add(new BasicNameValuePair("location", param.getLocation()));
                params.add(new BasicNameValuePair("description", param.getDescription()));
                params.add(new BasicNameValuePair("keywords", param.getKeywords()));
                params.add(new BasicNameValuePair("image", param.getImage()));
                params.add(new BasicNameValuePair("submitted", Long.toString(param.getSubmitted())));
                params.add(new BasicNameValuePair("from", Long.toString(param.getStartOfEvent())));
                params.add(new BasicNameValuePair("to", Long.toString(param.getEndOfEvent())));
                params.add(new BasicNameValuePair("cookie",COOKIE_TEST));

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                int response = conn.getResponseCode();

                if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                    throw new CommunicationLayerException("Invalid HTTP response code");
                }

                String serverResponse = fetchContent(conn);
                JSONObject jsonObject = new JSONObject(serverResponse);
                JSONObject serverResponseJson = jsonObject.getJSONObject("submission");
                if(serverResponseJson.getString("status").equals("failure")){
                    switch(serverResponseJson.getString("reason")){
                        case "name" : return ResponseStatus.NAME;
                        case "location"  : return ResponseStatus.LOCATION;
                        case "image" : return ResponseStatus.IMAGE;
                        case "category": return ResponseStatus.CATEGORY;
                        case "cookie": return ResponseStatus.COOKIE;
                        case "session": return ResponseStatus.SESSION;
                        default: throw new CommunicationLayerException();
                    }
                }else {
                    assert (serverResponseJson.getString("status").equals("ok"));

                    //Decide if server responds with OK or with Submission in JSON format
                    return ResponseStatus.OK;
                }

            }catch(IOException e ){
                throw new CommunicationLayerException();

            } catch (JSONException e) {
                throw new CommunicationLayerException();

            }



        }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }



    //================================================================================================
    //  1



  /*  public ResponseStatus sendAddSubmissionRequest(Submission submission) throws CommunicationLayerException {
        if (submission == null) {

            throw new CommunicationLayerException();
        }


        String urlParameters = null;
        try {
            urlParameters = "submission?name=" + URLEncoder.encode(submission.getName(), "UTF-8") +
                    "&category=" + URLEncoder.encode(submission.getCategory(), "UTF-8") +
                    "&location=" + URLEncoder.encode(submission.getLocation(), "UTF-8") +
                    "&description=" + URLEncoder.encode(submission.getDescription(), "UTF-8") +
                    "&keywords=" + URLEncoder.encode(submission.getKeywords(), "UTF-8") +
                    "&from=" + URLEncoder.encode(Long.toString(submission.getStartOfEvent()), "UTF-8") +
                    "&image=" + URLEncoder.encode(submission.getImage(), "UTF-8") +
                    "&submitted=" + URLEncoder.encode(Long.toString(submission.getSubmitted()), "UTF-8") +
                    "&to=" + URLEncoder.encode(Long.toString(submission.getEndOfEvent()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CommunicationLayerException();
        }

        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            //connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();


            int response = connection.getResponseCode();

            if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                throw new CommunicationLayerException("Invalid HTTP response code");
            }

            String serverResponse = fetchContent(connection);
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONObject serverResponseJson = jsonObject.getJSONObject("submission");
            if (serverResponseJson.getString("status").equals("failure")) {
                switch (serverResponseJson.getString("reason")) {
                    case "name":
                        return ResponseStatus.NAME;
                    case "location":
                        return ResponseStatus.LOCATION;
                    case "image":
                        return ResponseStatus.IMAGE;
                    default:
                        throw new CommunicationLayerException();
                }
            } else if (serverResponseJson.getString("status").equals("invalid")) {
                return null;
            } else {
                assert (serverResponseJson.getString("status").equals("ok"));

                //Decide if server responds with OK or with Submission in JSON format
                return ResponseStatus.OK;
            }
        } catch (IOException | JSONException e) {
            throw new CommunicationLayerException();
        }

    }

*/

    //  2
    //===================================================================================================================

    /*
        try{

        /*
            //Must check which fields are missing and if they are allowed to not be specified before creating the URL
            URL url = new URL(SERVER_URL + "/add_submission?name=" + submission.getName() + "&category=" + submission.getCategory() +
                    "&location=" + submission.getLocation() + "&description=" + submission.getDescription() + "&keywords=" +
                    submission.getKeywords() + "&image=" + submission.getImage() + "&submitted=" + submission.getSubmitted() +
                    "&from=" + submission.getStartOfEvent() + "&to=" + submission.getEndOfEvent());

*/
    /*
            HttpURLConnection conn = networkProvider.getConnection(url);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();

            if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                throw new CommunicationLayerException("Invalid HTTP response code");
            }

            String serverResponse = fetchContent(conn);
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONObject serverResponseJson = jsonObject.getJSONObject("submission");
            if(serverResponseJson.getString("status").equals("failure")){
                switch(serverResponseJson.getString("reason")){
                    case "name" : return ResponseStatus.NAME;
                    case "location"  : return ResponseStatus.LOCATION;
                    case "image" : return ResponseStatus.IMAGE;
                    default: throw new CommunicationLayerException();
                }
            }else if ( serverResponseJson.getString("status").equals("invalid")){
                return null;
            }else {
                assert (serverResponseJson.getString("status").equals("ok"));

                //Decide if server responds with OK or with Submission in JSON format
                return ResponseStatus.OK;
            }
        } catch(IOException | JSONException e) {
            throw new CommunicationLayerException();
        }

*/






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



}
