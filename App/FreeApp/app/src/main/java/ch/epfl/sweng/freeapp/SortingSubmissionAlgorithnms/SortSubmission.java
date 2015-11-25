package ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms;

import java.util.List;

import ch.epfl.sweng.freeapp.Submission;

/**
 * Design Pattern  Strategy encapsulating a piece of code, So that sorting can be interchangeable
 */
public interface SortSubmission {


    List<Submission> sort( List <Submission> submissions);


}
