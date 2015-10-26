package ch.epfl.sweng.freeapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private LogInInfo logInInfo;
    private CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public LogInInfo getLogin(){

        return new LogInInfo(logInInfo.getUsername(),logInInfo.getPassword());

    }


    public void onLoginClick(View View){

        EditText usernameField = (EditText)findViewById(R.id.username);
        EditText passwordField = (EditText)findViewById(R.id.password);

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if(username != null && password != null) {

            this.logInInfo = new LogInInfo(username,password);

            new GetServerResponseTask(this).execute(logInInfo);

        }

    }

    public void onRegisterClick(View view){
        Intent intent = new Intent(this,RegistrationActivity.class);
        startActivity(intent);

    }


    private class GetServerResponseTask extends AsyncTask<LogInInfo,Void,ResponseStatus> {

            private Context context;

        public GetServerResponseTask(Context context){
            this.context = context;
        }


        @Override
        protected ResponseStatus doInBackground(LogInInfo... params) {

            try {
                ResponseStatus status = communicationLayer.sendLogInInfo(params[0]);
                return status;
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
                alertUser("Server unable to answer request please try again");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseStatus responseStatus){


            if(responseStatus == ResponseStatus.OK){

                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);

            }else {
                //Failure

                EditText passwordField = (EditText)findViewById(R.id.password);

                if( responseStatus == ResponseStatus.PASSWORD){

                     alertUser("wrong password");
                     passwordField.setText("");

                }else if(responseStatus == ResponseStatus.USERNAME){

                    alertUser("wrong userName");
                    EditText userField = (EditText)findViewById(R.id.username);

                    userField.setText("");
                    passwordField.setText("");

                }else{
                    assert(responseStatus == ResponseStatus.EMAIL.EMPTY);

                    alertUser("Empty Field(s)");

                }

            }

        }

        private void alertUser(String message){
            Context context = getApplicationContext();
            Toast.makeText(context, message,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
