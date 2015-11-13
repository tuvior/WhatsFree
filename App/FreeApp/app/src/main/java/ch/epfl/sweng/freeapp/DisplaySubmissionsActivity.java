package ch.epfl.sweng.freeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ch.epfl.sweng.freeapp.mainScreen.TwoFragment;

public class DisplaySubmissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_submissions);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(TwoFragment.CATEGORY_MESSAGE);

        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("Display submissions related to " + message);

        // Set the text view as the activity layout
        setContentView(textView);

    }

}
