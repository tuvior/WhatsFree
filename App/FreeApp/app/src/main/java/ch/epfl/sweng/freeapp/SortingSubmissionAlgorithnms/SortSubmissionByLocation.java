package ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.freeapp.Submission;

/**
 * Created by lois on 12/4/15.
 */
public class SortSubmissionByLocation implements SortSubmission {

    private LatLng userLocation;
    private Context context;

    public SortSubmissionByLocation(Context context, LatLng userLocation){
        this.context = context;
        this.userLocation = userLocation;
    }

    @Override
    public List<Submission> sort(List<Submission> submissions) {
        Collections.sort(submissions, new Comparator<Submission>() {
            @Override
            public int compare(Submission lhs, Submission rhs) {
                LatLng lhsLatLng = getSubmissionLatLng(lhs);
                LatLng rhsLatLng = getSubmissionLatLng(rhs);

                //Submission's whose LatLng is null will be placed together at the
                //end of the list
                if( (lhsLatLng == null) && (rhsLatLng != null) ){
                    return 1;
                } else if ((lhsLatLng != null) && (rhsLatLng == null)) {
                    return -1;
                } else if ((lhsLatLng == null) && (rhsLatLng == null)){
                    return 0;
                }

                double leftDistance = distance(userLocation, lhsLatLng);
                double rightDistance = distance(userLocation, rhsLatLng);

                if(leftDistance < rightDistance){
                    return -1;
                } else if (leftDistance > rightDistance){
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return submissions;
    }

    private static double distance(LatLng origin, LatLng submissionCoordinates) {
        double lat1 = origin.latitude;
        double lat2 = submissionCoordinates.longitude;

        double lon1 = origin.longitude;
        double lon2 = submissionCoordinates.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist;
    }

    /**
     * Converts decimal degrees to radians
     * @param deg the angle in degrees
     * @return the angle in radians
     */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Converts radian degrees to decimal degrees
     * @param rad the angle in radians
     * @return the angle in decimal
     */
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    /**
     * Returns the submission's LatLng, computed from its
     * address
     * @param submission The submission from which we want the LatLng
     * @return the LatLng of the given submission
     */
    public LatLng getSubmissionLatLng(Submission submission){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        if(submission.getLocation() != null) {
            //Get maximum 1 address
            try {
                addresses = geocoder.getFromLocationName(submission.getLocation(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //If an address has been found
            LatLng latLng = null;
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                latLng = new LatLng(latitude, longitude);
            }

            return latLng;
        } else {
            return null;
        }

    }

}
