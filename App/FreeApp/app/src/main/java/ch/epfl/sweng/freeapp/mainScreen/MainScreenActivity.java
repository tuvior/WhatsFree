package ch.epfl.sweng.freeapp.mainScreen;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmission;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

/**
 * Created by Lois Talagrand on 11/5
 * <p/>
 * Displays the app's main screen, where the user is directed
 * after the login.
 * 3 tabs are displayed: Categories, What's new, Around you (default: What's new).
 * 2 additional features: search button and map button
 * <p/>
 * Tutorial (creating tabs): http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
 */

public class MainScreenActivity extends AppCompatActivity {

    public final static String SUBMISSION_MESSAGE = "ch.epfl.sweng.freeapp.SUBMISSION";

    protected static SortSubmission sortSubmissionAlgorithm;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.tab_whats_new,
            R.drawable.tab_categories,
            R.drawable.tab_around_you
    };

    private boolean visibility = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Set toolbar as the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Sets up a viewPager that allows the user to flip left and right
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        handleIntent(getIntent());
    }


    private void displayToast(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

             new DownloadSubmissionTask(this).execute(query);

        }
    }

    /**
     * Action bar contains search, map as well as the new submission button
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_screen_activity, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(
                 searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_submission:
                Intent intent = new Intent(this, CreateNewSubmissionActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Used by tests to get the tabs
     *
     * @return the viewPAger
     */
    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * Adds an icon to each tab
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    /**
     * Defines the number of tabs by setting appropriate fragment and tab name.
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WhatsNewFragment(), "What's new");
        adapter.addFragment(new CategoriesFragment(), "Categories");
        adapter.addFragment(new AroundYouFragment(), "Around You");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        visibility = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        visibility = false;
    }

    public boolean getVisibility(){
        return visibility;
    }


    /**
     * A viewPagerAdapter is used for populating a viewPager's tabs
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    private class DownloadSubmissionTask extends AsyncTask<String, Void, Submission> {

        Context context;

        private DownloadSubmissionTask(Context context) {
            this.context = context;
        }

        @Override
        protected Submission doInBackground(String... params) {
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());
            Submission submission = null;

            try {
                submission = communicationLayer.fetchSubmissionByName(params[0]);
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }
            return submission;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Submission submission) {

            if(submission == null){
                displayToast("No submission exists with this name");
            }
            // display submission
            else {

                Intent intent = new Intent(context, DisplaySubmissionActivity.class);
                intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submission.getId());

                startActivity(intent);
            }

        }

    }


}