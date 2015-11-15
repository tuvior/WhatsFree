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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import ch.epfl.sweng.freeapp.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.R;


public class AroundYouFragment extends ListFragment {

    public final static String SUBMISSION_MESSAGE = "ch.epfl.sweng.freeapp.SUBMISSION";

    public AroundYouFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.around_you_fragment, container, false);

        //Get the JSONArray corresponding to the submissions
        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        try {
            JSONArray jsonNamesAndPictures = fakeCommunicationLayer.sendWhatIsNewRequest();
            ArrayList<SubmissionShortcut> submissions = jsonArrayToArrayList(jsonNamesAndPictures);
            //Adapter provides a view for each item in the data set
            SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, submissions);
            this.setListAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        SubmissionShortcut submissionShortcut = (SubmissionShortcut)getListAdapter().getItem(position);
        String submissionName = submissionShortcut.getName();
        Intent intent = new Intent(v.getContext(), DisplaySubmissionActivity.class);
        intent.putExtra(SUBMISSION_MESSAGE, submissionName);
        startActivity(intent);
    }

    private ArrayList<SubmissionShortcut> jsonArrayToArrayList(JSONArray jsonSubmissions) throws JSONException {

        ArrayList<SubmissionShortcut> submissionsList = new ArrayList<>();

        for(int i = 0; i < jsonSubmissions.length(); i++){
            //TODO: also include image
            JSONObject jsonSubmission = jsonSubmissions.getJSONObject(i);
            String name = jsonSubmission.getString("name");

            SubmissionShortcut submission = new SubmissionShortcut(name);
            submissionsList.add(submission);
        }

        return submissionsList;

    }


}
