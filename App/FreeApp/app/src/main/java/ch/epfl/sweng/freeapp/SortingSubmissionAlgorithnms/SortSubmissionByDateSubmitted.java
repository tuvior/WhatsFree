package ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.freeapp.Submission;

/**
 * Created by francisdamachi on 25/11/15.
 */
public class SortSubmissionByDateSubmitted  implements  SortSubmission{
    @Override
    public List<Submission> sort(List<Submission> submissions) {
        Collections.sort(submissions, new Comparator<Submission>() {
            @Override
            public int compare(Submission lhs, Submission rhs) {
                 if( lhs.getSubmitted() <  rhs.getSubmitted()){
                     return 1;
                 }else if( rhs.getSubmitted() > lhs.getSubmitted()){
                    return -1;
                }else{
                    return 0;
                }
            }
        });

        return submissions;
    }
}
