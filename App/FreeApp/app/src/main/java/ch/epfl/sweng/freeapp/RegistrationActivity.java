package ch.epfl.sweng.freeapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;


public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private Button signUpView;

    private RegistrationInfo registrationInfo;

    private static final String serverUrl = "http://sweng-wiinotfit.appspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_registration);

        usernameView = (EditText) findViewById(R.id.username);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        confirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        signUpView = (Button) findViewById(R.id.button);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    public void onClickSignUp(View view){
        int zero = 0;
        if(usernameView.getText().length() == zero || emailView.getText().length() == zero
                || passwordView.getText().length() == zero || confirmPasswordView.getText().length() == zero) {
            Toast.makeText(getApplicationContext(), "One of the fields is empty", Toast.LENGTH_LONG).show();
        }
        else if(!passwordView.getText().toString().equals(confirmPasswordView.getText().toString()))
        {
            Toast.makeText(getApplicationContext(), "passwords aren't identical", Toast.LENGTH_LONG).show();

        }
        else
        {
            RegistrationInfo registrationInfo = new RegistrationInfo(usernameView.getText().toString(),
                                                                     emailView.getText().toString(),
                                                                     passwordView.getText().toString());

            //CommunicationLayer communicationLayer = new CommunicationLayer(serverUrl);

            /*
            if(communicationLayer.sendRegistrationInfo(registrationInfo) == null ||
                    !communicationLayer.sendRegistrationInfo(registrationInfo).equals("OK"))
            {
                Toast.makeText(getApplicationContext(), "Either the username already exists or the email address is invalid",
                        Toast.LENGTH_LONG).show();
            }*/

        }

        /*
        else if(emailView.getText().length() == zero)
        {

        }
        else if(passwordView.getText().length() == zero){

        }
        */
    }

}
