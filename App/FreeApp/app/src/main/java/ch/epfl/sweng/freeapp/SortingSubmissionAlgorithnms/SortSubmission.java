package ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms;

import java.util.List;

import ch.epfl.sweng.freeapp.Submission;

/**
 * Design Pattern  Strategy used here encapsulating a piece of code, So that sorting can be interchangeable later on
 * Making it also easier to test
 */
public interface SortSubmission {

    /**
     * Sorts submissions based on different criteria
     *
     * @param submissions
     * @return sorted Submissions
     */
    List<Submission> sort(List<Submission> submissions);


}
