package ch.epfl.sweng.freeapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private CommunicationLayer communicationLayer;

    private EditText usernameView;

    private EditText emailView;

    private EditText passwordView;
    private EditText confirmPasswordView;

    private Button signUpView;

    private final String usernamePattern = "( )";
    private final String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$";
    private final String EmailPattern = "^(.+)@(.+)\\.(.+)$"; // Loose local email validation

    protected static final int USERNAME_MIN_LENGTH = 6;
    protected static final int USERNAME_MAX_LENGTH = 30; // arbitrary

    protected static final int PASSWORD_MIN_LENGTH = 8;
    protected static final int PASSWORD_MAX_LENGTH = 30; // arbitrary

    protected static final int EMAIL_MAX_LENGTH = 64; // wikipedia






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_main);

        usernameView = (EditText) findViewById(R.id.username);

        emailView = (EditText) findViewById(R.id.email);

        passwordView = (EditText) findViewById(R.id.password);
        confirmPasswordView = (EditText) findViewById(R.id.confirmPassword);

        signUpView = (Button) findViewById(R.id.button);

        communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View v) {

        if(localValidityCheck())
        {
            String username = usernameView.getText().toString();
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();

            RegistrationInfo registrationInfo = new RegistrationInfo(username, password, email);
            new RetrieveServerResponse(this).execute(registrationInfo);
        }

    }


    protected boolean isUsernameValid(final String username){
        Pattern pattern = Pattern.compile(usernamePattern);
        Matcher matcher = pattern.matcher(username);
        return USERNAME_MIN_LENGTH <= username.length() && username.length() <= USERNAME_MAX_LENGTH && !matcher.find();
    }

    protected boolean isEmailValid(final String email) {
        Pattern generalPattern = Pattern.compile(EmailPattern);
        Matcher generalMatcher = generalPattern.matcher(email);

        String whitespace = "^(.+)\\s(.+)$";
        Pattern whitespacePattern = Pattern.compile(whitespace);
        Matcher matcherWhitespace = whitespacePattern.matcher(email);

        return generalMatcher.matches() && !matcherWhitespace.matches() && 0 < generalMatcher.group(1).length()
                && generalMatcher.group(1).length() <= EMAIL_MAX_LENGTH;
    }

    protected boolean isPasswordValid(final String password) {
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return PASSWORD_MIN_LENGTH <= password.length() && password.length() <= PASSWORD_MAX_LENGTH && matcher.find();
    }

    /**
     * Preliminary check without internet access that user's
     * entered information are valid.
     *
     *@return true if all the fields are preliminarily correct,
     *        false otherwise
     */
    private boolean localValidityCheck()
    {
        boolean valid = true;
        String empty = "";



        if(!isUsernameValid(usernameView.getText().toString())){
            usernameView.setText(empty);
            valid = false;
        }

        if(!isEmailValid(emailView.getText().toString())){
            emailView.setText(empty);
            valid = false;
        }


        if(!isPasswordValid(passwordView.getText().toString())){
            valid = false;
        }
        else if(!passwordView.getText().toString().equals(confirmPasswordView.getText().toString())){
            valid = false;
        }

        if(!valid)
        {
            passwordView.setText(empty);
            confirmPasswordView.setText(empty);
            //Toast.makeText(RegistrationActivity.this, "Invalid registration", Toast.LENGTH_LONG).show();
        }

        /*
        if(usernameView.getText().toString().trim().isEmpty() ||
                emailView.getText().toString().trim().isEmpty() ||
                    passwordView.getText().toString().trim().isEmpty() ||
                        confirmPasswordView.getText().toString().trim().isEmpty()){

            Toast.makeText(RegistrationActivity.this, "Please fill all the fields", Toast.LENGTH_LONG).show();

        }
        */

        return valid;

    }



    /**
     * Async task for getting a server response
     *
     */
    private class RetrieveServerResponse extends AsyncTask<RegistrationInfo, RegistrationInfo, ResponseStatus>{

        private Context context;

        private RetrieveServerResponse(Context context){
            this.context = context;
        }

        @Override
        protected ResponseStatus doInBackground(RegistrationInfo... params) {

            ResponseStatus responseStatus = null;
            try {
                responseStatus = communicationLayer.sendRegistrationInfo(params[0]);
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }
            return responseStatus;
        }



        @Override
        protected void onPostExecute(ResponseStatus responseStatus) {
            if(responseStatus == ResponseStatus.OK){
                Intent intent = new Intent(context, MainScreenActivity.class);
                startActivity(intent);
            }
            else
            {
                String empty = "";
                usernameView.setText(empty);
                emailView.setText(empty);
                passwordView.setText(empty);
                confirmPasswordView.setText(empty);
            }
            return;
        }


    }
}
