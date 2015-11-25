package ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.freeapp.Submission;


public class SortSubmissionByName  implements  SortSubmission{
    @Override
    public List<Submission> sort(List<Submission> submissions) {
        Collections.sort(submissions, new Comparator<Submission>() {
            @Override
            public int compare(Submission lhs, Submission rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        return submissions;
    }
}
