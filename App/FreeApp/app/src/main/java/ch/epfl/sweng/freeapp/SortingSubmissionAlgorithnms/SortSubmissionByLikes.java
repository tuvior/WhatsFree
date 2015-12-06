package ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.freeapp.Submission;

/**
 * Created by francisdamachi on 25/11/15.
 */
public class SortSubmissionByLikes  implements  SortSubmission{
    @Override
    public List<Submission> sort(List<Submission> submissions) {

        if(submissions == null){
            return null;
        }

        Collections.sort(submissions, new Comparator<Submission>() {
            @Override
            public int compare(Submission lhs, Submission rhs) {

               return Integer.compare(lhs.getLikes(),rhs.getLikes());

            }
        });

        return submissions;
    }
    }

