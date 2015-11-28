package ch.epfl.sweng.freeapp.communication;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

import ch.epfl.sweng.freeapp.BuildConfig;
import ch.epfl.sweng.freeapp.loginAndRegistration.LogInInfo;
import ch.epfl.sweng.freeapp.loginAndRegistration.RegistrationInfo;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;


public class CommunicationLayer implements  DefaultCommunicationLayer {
    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private NetworkProvider networkProvider;
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private String cookieSession;

    //FixMe:  REMEMBER TO CHANGE TO COOKIE SESSION STRING !!!!!!!
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
     * @param logInInfo login information entered by the user
     * @return "ok" if the operation was successful, or "failed" otherwise
     */
    public ResponseStatus sendLogInInfo(LogInInfo logInInfo) throws CommunicationLayerException {

        if(logInInfo == null ){
            throw new CommunicationLayerException("LogInfo null");
        }

        try {

            String serverResponseString = fetchStringFrom(SERVER_URL + "/login?user=" + logInInfo.getUsername() + "&password=" + logInInfo.getPassword());
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

                if(BuildConfig.DEBUG && !(logInJson.get("status").equals("ok"))){
                    throw new AssertionError();
                }
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
     * @param registrationInfo registration information entered by the user
     * @return "ok" if the operation was successful, or "failed" otherwise
     */
    public ResponseStatus sendRegistrationInfo(RegistrationInfo registrationInfo) throws CommunicationLayerException {

        if (registrationInfo == null) {
            throw new CommunicationLayerException("null registrationInfo");
        }
        try {

            //Get Response
            String serverResponse = fetchStringFrom(SERVER_URL + "/register?user=" + registrationInfo.getUsername() + "&password=" + registrationInfo.getPassword() + "&email=" + registrationInfo.getEmail());
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
            if (BuildConfig.DEBUG && !(serverResponseJson.getString("status").equals("ok"))){
                throw new AssertionError();
            }
            return ResponseStatus.OK;
        } catch (IOException e) {
            throw new CommunicationLayerException("Server Unresponsive");
        } catch (JSONException e) {
            throw new CommunicationLayerException();
        }

    }

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
                if(BuildConfig.DEBUG && !(serverResponseJson.getString("status").equals("ok"))){
                    throw new AssertionError();
                }

                //Decide if server responds with OK or with Submission in JSON format
                return ResponseStatus.OK;
            }

        }catch(IOException | JSONException e ){
            throw new CommunicationLayerException();
        }
    }

    @Override
    public ArrayList<Submission> sendSubmissionsRequest() throws CommunicationLayerException {

        try{
            String content = fetchStringFrom(SERVER_URL+"/retrieve?cookie=" + COOKIE_TEST + "&flag=2");
            JSONArray contentArray = new JSONArray(content);

            return jsonArrayToArrayList(contentArray);

        }catch(IOException | JSONException e){
            throw new CommunicationLayerException();
        }

    }

    @Override
    public Submission fetchSubmission(String name) throws CommunicationLayerException {

        Submission submission;

        try{
            String content = fetchStringFrom(SERVER_URL+"/retrieve?cookie=" + COOKIE_TEST + "&flag=1&name=" + name);
            System.out.println(content);
            JSONObject jsonSubmission = new JSONObject(content);

            //Retrieve specific submission fields
            if(BuildConfig.DEBUG && !(name.equals(jsonSubmission.getString("name")))){
                throw new AssertionError();
            }
            String description = jsonSubmission.getString("description");

            SubmissionCategory submissionCategory;
            if(SubmissionCategory.contains(jsonSubmission.getString("category"))) {
                 submissionCategory = SubmissionCategory.valueOf(jsonSubmission.getString("category"));
            } else {
                submissionCategory = SubmissionCategory.Miscellaneous;
            }
            String image = jsonSubmission.getString("image");
            String location = jsonSubmission.getString("location");

            submission = new Submission(name, description, submissionCategory, location, image);

        }catch(IOException | JSONException e){
            throw new CommunicationLayerException();
        }

        return submission;
    }

    @Override
    public ArrayList<Submission> sendCategoryRequest(SubmissionCategory category) throws CommunicationLayerException {

        try{
            String content = fetchStringFrom(SERVER_URL+"/retrieve?cookie=" + COOKIE_TEST + "&flag=4&category=" + category.toString().toUpperCase());
            //if there is no submission corresponding to the category, then the server will return a failure
            if(!content.contains("failure")) {
                JSONArray contentArray = new JSONArray(content);
                return jsonArrayToArrayList(contentArray);
            } else {
                //return an empty array if no submissions corresponding to the category
                return new ArrayList<>();
            }

        }catch(IOException | JSONException e){
            throw new CommunicationLayerException();
        }
    }

    @Override
    public ArrayList<Submission> sendAroundYouRequest(LatLng userLocation) throws JSONException, CommunicationLayerException {
        try{
            String content = fetchStringFrom(SERVER_URL+"/retrieve?cookie=" + COOKIE_TEST + "&flag=3&longitude=" + userLocation.longitude + "&latitude=" + userLocation.latitude);
            //if there is no submission corresponding to the category, then the server will return a failure
            if(!content.contains("failure")) {
                JSONArray contentArray = new JSONArray(content);
                return jsonArrayToArrayList(contentArray);
            } else {
                //return an empty array if no submissions corresponding to the category
                return new ArrayList<>();
            }

        }catch(IOException | JSONException e){
            throw new CommunicationLayerException();
        }
    }


    //TODO: documentation
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
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

    /**
     * Fetches the information given by the provided connection
     * @param conn A connection to a provided URL
     * @return The String given by connecting to the given URL
     * @throws IOException
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

    /**
     * The server sends the submissions as a JSONArray, and the communication layer
     * conveys those to the tabs as an ArrayList
     * @param jsonSubmissions the submissions returned by the server in a JSONArray format
     * @return the same submissions but in an ArrayList
     * @throws JSONException
     */
    public ArrayList<Submission> jsonArrayToArrayList(JSONArray jsonSubmissions) throws JSONException {

        ArrayList<Submission> submissionsList = new ArrayList<>();

        for(int i = 0; i < jsonSubmissions.length(); i++){

            //Only names and images are required since those are the only
            //fields displayed in the tabs
            JSONObject jsonSubmission = jsonSubmissions.getJSONObject(i);
            String name = jsonSubmission.getString("name");
            String image = jsonSubmission.getString("image");

            Submission.Builder builder = new Submission.Builder().name(name).image(image);
            Submission submission = builder.build();
            submissionsList.add(submission);
        }

        if (BuildConfig.DEBUG && (jsonSubmissions.length() != submissionsList.size())){
            throw new AssertionError();
        }
        return submissionsList;

    }

    private String fetchStringFrom(String urlString) throws IOException, CommunicationLayerException {
        URL url = new URL(urlString);
        HttpURLConnection conn = networkProvider.getConnection(url);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        int response = conn.getResponseCode();

        if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
            throw new CommunicationLayerException("Invalid HTTP response code");
        }

        return fetchContent(conn);
    }

}
