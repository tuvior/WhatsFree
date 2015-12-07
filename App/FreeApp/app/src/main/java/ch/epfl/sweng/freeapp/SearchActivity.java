package ch.epfl.sweng.freeapp;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.mainScreen.DisplaySubmissionActivity;
import ch.epfl.sweng.freeapp.mainScreen.MainScreenActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            Toast.makeText(this, "Search Activity : The query is " + query, Toast.LENGTH_LONG).show();

            /*
            CommunicationLayer comm = new CommunicationLayer(new DefaultNetworkProvider());
            Submission submission = null;
            try {
                submission = comm.fetchSubmission(query);

                intent = new Intent(this, DisplaySubmissionActivity.class);
                intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, query);
                startActivity(intent);

            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }*/

        }
        else{
            Toast.makeText(this, "SearchActivity : Not the correct intent", Toast.LENGTH_LONG).show();
        }
    }
}
