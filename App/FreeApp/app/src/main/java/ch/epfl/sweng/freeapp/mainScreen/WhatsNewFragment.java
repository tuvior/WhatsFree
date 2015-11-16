package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.R;
/**
 * Created by lois on 11/6/15.
 */
public class WhatsNewFragment extends ListFragment {

    public WhatsNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.whats_new_fragment, container, false);

        //Get the JSONArray corresponding to the submissions
        try {
            JSONArray jsonNamesAndPictures = FakeCommunicationLayer.sendWhatIsNewRequest();
            ArrayList<SubmissionShortcut> submissions = FakeCommunicationLayer.jsonArrayToArrayList(jsonNamesAndPictures);
            //Adapter provides a view for each item in the data set
            SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, submissions);
            this.setListAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    /**
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SubmissionShortcut submissionShortcut = (SubmissionShortcut)getListAdapter().getItem(position);
        String submissionName = submissionShortcut.getName();
        Intent intent = new Intent(v.getContext(), DisplaySubmissionActivity.class);
        intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submissionName);
        startActivity(intent);
    }

}
