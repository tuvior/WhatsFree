package ch.epfl.sweng.freeapp.mainScreen;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            CommunicationLayer comm = new CommunicationLayer(new DefaultNetworkProvider());

            try {
                Submission submission = comm.fetchSubmission(query);

                intent = new Intent(this, DisplaySubmissionActivity.class);
                intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, query);
                startActivity(intent);

            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }

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


    /*

    public void onClickSortSubmission(View view){

        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Sort Submission");
        dialog.setContentView(R.layout.sort_submission_dialog);
        dialog.show();

        final SortSubmission[] sortSubmission = new SortSubmission[1];

        RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radioGroupFilter);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)dialog.findViewById(group.getCheckedRadioButtonId());

                switch(radioButton.getId()){
                    case R.id.byTime : sortSubmission[0] = new SortSubmissionByEndOFEvent();
                        break;
                    case R.id.byLikes: sortSubmission[0] = new SortSubmissionByLikes();
                        break;
                    case R.id.byName: sortSubmission[0] = new SortSubmissionByName();
                        break;

                }

            }
        });



        final Button okButton = (Button)dialog.findViewById(R.id.dialogOkButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sortSubmissionAlgorithm = sortSubmission[0];
                dialog.dismiss();
            }
        });




    }
 */
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
}