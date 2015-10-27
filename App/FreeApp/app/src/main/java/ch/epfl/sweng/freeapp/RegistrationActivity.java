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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private CommunicationLayer communicationLayer;

    private EditText usernameView;
    private TextView hintUsernameView;

    private EditText emailView;
    private TextView hintEmailView;

    private EditText passwordView;
    private EditText confirmPasswordView;
    private TextView hintPasswordView;

    private Button signUpView;

    private final String usernamePattern = "( )";
    private final String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$"; // )(?=.*(_|[^\w]))
    private final String EmailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final int USERNAME_MINIMAL_LENGTH = 6;
    private static final int PASSWORD_MINIMAL_LENGTH = 8;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_main);

        usernameView = (EditText) findViewById(R.id.username);
        hintUsernameView = (TextView) findViewById(R.id.hintMessageUsername);

        emailView = (EditText) findViewById(R.id.email);
        hintEmailView = (TextView) findViewById(R.id.hintMessageEmail);

        passwordView = (EditText) findViewById(R.id.password);
        confirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        hintPasswordView = (TextView) findViewById(R.id.hintMessagePassword);

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

            RegistrationInfo registrationInfo = new RegistrationInfo(username, email, password);
            new RetrieveServerResponse(this).execute(registrationInfo);
        }

    }


    protected boolean isUsernameValid(String username){
        Pattern pattern = Pattern.compile(usernamePattern);
        Matcher matcher = pattern.matcher(username);
        return username.length() >= USERNAME_MINIMAL_LENGTH && !matcher.find();
    }

    protected boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EmailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    protected boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return password.length() > PASSWORD_MINIMAL_LENGTH && matcher.find();
    }

    /**
     * Preliminary check without internet access that user's
     * entered informations are valid.
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
            hintUsernameView.setText("The username must be at least 6 character long.");
            hintUsernameView.setVisibility(View.VISIBLE);
            valid = false;
        }
        else{
            hintUsernameView.setVisibility(View.GONE);
        }

        if(!isEmailValid(emailView.getText().toString())){
            emailView.setText(empty);
            hintEmailView.setText("invalid email address");
            hintEmailView.setVisibility(View.VISIBLE);
            valid = false;
        }
        else{
            hintEmailView.setVisibility(View.GONE);
        }

        if(!isPasswordValid(passwordView.getText().toString())){
            hintPasswordView.setText("The password must be at least 8 character long." +
                    " It must contains at least one digit, one uppercase letter and one" +
                    " special character");
            hintPasswordView.setVisibility(View.VISIBLE);
            valid = false;
        }
        else{
            hintPasswordView.setVisibility(View.GONE);
        }
        if(!passwordView.getText().toString().equals(confirmPasswordView.getText().toString())){
            // TODO : logic behind un-identical passwords (message to be displayed)
        }

        if(!valid)
        {
            passwordView.setText(empty);
            confirmPasswordView.setText(empty);
        }

        if(usernameView.getText().toString().trim().isEmpty() ||
                emailView.getText().toString().trim().isEmpty() ||
                    passwordView.getText().toString().trim().isEmpty() ||
                        confirmPasswordView.getText().toString().trim().isEmpty()){

            Toast.makeText(RegistrationActivity.this, "Please enter all the fields", Toast.LENGTH_LONG).show();

        }

        return valid;

    }



    /**
     * Async task for getting a server reponse
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
                Intent intent = new Intent(context, WelcomeActivity.class);
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
