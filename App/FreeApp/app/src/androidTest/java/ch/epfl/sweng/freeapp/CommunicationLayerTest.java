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
    private static String JSON_RESPONSE_USERNAME = buildJson("register", "user").toString();
    private static String JSON_RESPONSE_EMAIL =  buildJson("register", "email").toString();
    private static String JSON_RESPONSE_PASSWORD =  buildJson("register", "password").toString();
    private static String JSON_RESPONSE_GOOD = buildOkJSON("ok").toString();
    private static String JSON_LOG_IN_GOOD = buildSuccessfulLogInJSON().toString();
    private static String JSON_LOG_IN_USERNAME = buildJson("login", "user").toString();
    private static String JSON_LOG_IN_PASSWORD = buildJson("login", "password").toString();
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
    public void testLogInSuccessful() throws CommunicationLayerException, IOException {
        configureResponse(JSON_LOG_IN_GOOD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.OK, communicationLayer.sendLogInInfo(new LogInInfo("test", "test")));
    }
    @Test
    public void testLogInUserProblem() throws CommunicationLayerException, IOException {
        configureResponse(JSON_LOG_IN_USERNAME, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.USERNAME, communicationLayer.sendLogInInfo(new LogInInfo("test", "test")));
    }
    @Test
    public void testLogInPasswordProblem() throws CommunicationLayerException, IOException {
        configureResponse(JSON_LOG_IN_PASSWORD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.PASSWORD, communicationLayer.sendLogInInfo(new LogInInfo("test", "test")));
    }
    @Test
    public void testCorrectDataSuccessRegister () throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_GOOD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.OK, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }
    @Test
    public void testCorrectDataFailureUsername() throws IOException, CommunicationLayerException{
        configureResponse(JSON_RESPONSE_USERNAME,HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.USERNAME, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }
    @Test
    public void testCorrectDataFailureEmail() throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_EMAIL,HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.EMAIL, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }
    @Test
    public void testCorrectDataFailurePassword() throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_PASSWORD,HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.PASSWORD, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }
    @Test
    public void testInvalidJson() throws IOException {
        try {
            configureResponse("this is rubbish JSON", HttpURLConnection.HTTP_OK);
            communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test"));
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
            communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test"));
            fail("Did not raise QuizClientException");
        } catch (CommunicationLayerException e) {
            // PERFECT
        }
    }
    private static JSONObject buildJson(String testType, String reason){
        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try {
            inner.put("status", "failure");
            inner.put("reason", reason);
            outerObject.put(testType, inner);
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
    private static JSONObject buildSuccessfulLogInJSON(){
        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try{
            inner.put("status", "ok");
            inner.put("cookie", "TXeh4vQVdNOccioCdM6ZweQH5QK5T1j3pIwhdZkn9dSfqHiRSXScqn0TeAq3A4CE");
            outerObject.put("login",inner);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  outerObject;
    }
}
