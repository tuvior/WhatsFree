package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Created by lois on 11/6/15.
 */

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.epfl.sweng.freeapp.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.MapActivity;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.SubmissionShortcut;

public class AroundYouFragment extends ListFragment {

    private GoogleMap googleMap;
    private Location location;

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

        try{

            if(googleMap == null ){
                googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }

           this.location =  googleMap.getMyLocation();



        }catch(Exception  e){
            e.printStackTrace();
        }


        //Get the JSONArray corresponding to the submissions
        try {

            FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
            ArrayList<SubmissionShortcut> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

            //Adapter provides a view for each item in the data set
            SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, submissions);
            this.setListAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Set listener for mapButton
        ImageButton mapButton = (ImageButton) rootView.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                //intent.putExtra(MAP_MESSAGE, mapMessage);
                startActivity(intent);
            }
        });

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

    /**
     * Sort submissions according to how close they are to you
     */
    public ArrayList<SubmissionShortcut> sortSubmissions(ArrayList<SubmissionShortcut> submissionShortcuts){
        Collections.sort(submissionShortcuts, new Comparator<SubmissionShortcut>() {
            @Override
            public int compare(SubmissionShortcut lhs, SubmissionShortcut rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return submissionShortcuts;
    }
}
