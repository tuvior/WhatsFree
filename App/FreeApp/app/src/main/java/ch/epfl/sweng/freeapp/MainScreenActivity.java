package ch.epfl.sweng.freeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import ch.epfl.sweng.freeapp.mainScreen.AroundYouTabActivity;
import ch.epfl.sweng.freeapp.mainScreen.CategoriesTabActivity;
import ch.epfl.sweng.freeapp.mainScreen.WhatsNewTabActivity;

/**
 * Created by Lois Talagrand on 11/5
 *
 * Displays the main screen of the application, where the user is directed
 * after the login.
 * 3 tabs are displayed: Categories, What's new, Around you (default: What's new).
 * 2 additional features: search button and map button
 *
 * Documentation (creating tabs): http://www.learn-android-easily.com/2013/07/android-tabwidget-example.html
 */
public class MainScreenActivity extends AppCompatActivity  {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_screen);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("What's new");
        tab1.setContent(new Intent(this, WhatsNewTabActivity.class));

        tab2.setIndicator("Categories");
        tab2.setContent(new Intent(this, CategoriesTabActivity.class));

        tab3.setIndicator("Around you");
        tab3.setContent(new Intent(this, AroundYouTabActivity.class));

        // Add the tabs  to the TabHost (allows to display)
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

    }

}
