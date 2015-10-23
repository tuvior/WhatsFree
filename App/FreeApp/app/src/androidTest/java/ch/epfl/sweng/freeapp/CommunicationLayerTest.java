package ch.epfl.sweng.freeapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;


public class CommunicationLayerTest {

    private CommunicationLayer communicationLayer;
    private NetworkProvider networkProvider;
    private HttpURLConnection connection;
    private static final int ASCII_SPACE = 0x20;

    private static String JSON_RESPONSE_USERNAME = buildJson("failure", "user").toString();
    private static String JSON_RESPONSE_EMAIL =  buildJson("failure", "email").toString();
    private static String JSON_RESPONSE_PASSWORD =  buildJson("failure", "password").toString();
    private static String JSON_RESPONSE_GOOD = buildOkJSON("ok").toString();


    @Before
    public void setUp() throws Exception {
        this.connection = Mockito.mock(HttpURLConnection.class);
        this.networkProvider = Mockito.mock(NetworkProvider.class);
        Mockito.doReturn(connection).when(networkProvider).getConnection(Mockito.any(URL.class));
        this.communicationLayer = new CommunicationLayer(networkProvider);


    }
    private void configureCrash(int status) throws IOException {
        InputStream dataStream = Mockito.mock(InputStream.class);
        Mockito.when(dataStream.read())
                .thenReturn(ASCII_SPACE, ASCII_SPACE, ASCII_SPACE, ASCII_SPACE)
                .thenThrow(new IOException());

        Mockito.doReturn(status).when(connection).getResponseCode();
        Mockito.doReturn(dataStream).when(connection).getInputStream();
    }


    private void configureResponse (String content, int status ) throws IOException {
        InputStream dataStream = new ByteArrayInputStream(content.getBytes());
        Mockito.doReturn(dataStream).when(connection).getInputStream();
        Mockito.doReturn(status).when(connection).getResponseCode();
    }

    @Test
    public void testCorrectDataSuccessRegister () throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_GOOD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.OK, communicationLayer.sendRegistrationInfo(new RegistrationInfo()));

    }
    @Test
    public void testCorrectDataFailureUsername() throws IOException, CommunicationLayerException{
        configureResponse(JSON_RESPONSE_USERNAME,HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.USERNAME, communicationLayer.sendRegistrationInfo(new RegistrationInfo()));

    }

    @Test
    public void testCorrectDataFailureEmail() throws IOException, CommunicationLayerException {

        configureResponse(JSON_RESPONSE_EMAIL,HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.EMAIL, communicationLayer.sendRegistrationInfo(new RegistrationInfo()));

    }

    @Test
    public void testCorrectDataFailurePassword() throws IOException, CommunicationLayerException {

        configureResponse(JSON_RESPONSE_PASSWORD,HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.PASSWORD, communicationLayer.sendRegistrationInfo(new RegistrationInfo()));

    }

    @Test
    public void testInvalidJson() throws IOException {
        try {
            configureResponse("this is rubbish JSON", HttpURLConnection.HTTP_OK);
            communicationLayer.sendRegistrationInfo(new RegistrationInfo());
            fail("exception wasn't thrown");
        }catch (CommunicationLayerException e) {
            //PERFECT exception thrown
        }
    }



    /** Test that communication throws CommunicationLayerException  when connection is broken */
    @Test
    public void testResponseConnectionBroken() throws IOException {

        configureCrash(HttpURLConnection.HTTP_OK);
        try {
            communicationLayer.sendRegistrationInfo(new RegistrationInfo());
            fail("Did not raise QuizClientException");
        } catch (CommunicationLayerException e) {
            // PERFECT
        }


    }

    private static JSONObject buildJson(String status, String reason){

        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();

        try {
            inner.put("status", status);
            inner.put("reason", reason);
            outerObject.put("register", inner);

        }catch (JSONException e){
            e.printStackTrace();
        }



        return outerObject;
    }


    private static JSONObject buildOkJSON(String status){

        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();

        try{
            inner.put("status",status);
            outerObject.put("register",inner);

        }catch (JSONException e){
            e.printStackTrace();
        }

        return  outerObject;
    }






}
