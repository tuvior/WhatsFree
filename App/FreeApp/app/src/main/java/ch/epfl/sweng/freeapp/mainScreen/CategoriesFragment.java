package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Created by lois on 11/6/15.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;

public class CategoriesFragment extends ListFragment {

    public final static String CATEGORY_MESSAGE = "ch.epfl.sweng.freeapp.MESSAGE";

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * The categories tab presents all the categories as a list
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.categories_fragment, container,
                false);

        String[] values = new String[] { "Food", "Clothing", "Events", "Nightlife", "Sport", "Culture", "Goods", "Lifestyle", "Miscellaneous" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        return rootView;
    }

    /**
     * When the user clicks on a specific category, launch
     * the activity (fragment?) responsible for displaying the related submissions
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
        String category = (String) getListAdapter().getItem(position);
        Intent intent = new Intent(v.getContext(), CategoryDisplaySubmissionsActivity.class);
        intent.putExtra(CATEGORY_MESSAGE, category);
        startActivity(intent);
    }

    /**
     * Clicking on a specific category will filter submissions, but
     * there still needs to be an ordering among them.
     * Here the criterion is distance from your location.
     */
    public ArrayList<Submission> sortSubmissions(ArrayList<Submission> submissionShortcuts){
        //TODO
        return null;
    }

}
