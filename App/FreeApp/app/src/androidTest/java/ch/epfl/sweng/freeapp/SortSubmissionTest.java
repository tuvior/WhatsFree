package ch.epfl.sweng.freeapp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmission;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByEndOFEvent;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByLikes;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByName;

import static junit.framework.Assert.assertTrue;

/**
 * Created by francisdamachi on 04/12/15.
 */
public class SortSubmissionTest {

    private List<Submission> submissionList;
    //generateSubmission();


    @Test
    public void testSortSubmissionByName() {

        submissionList = generateSubmission();
        SortSubmission sortSubmission = new SortSubmissionByName();
        boolean sorted = isSortedByName(sortSubmission.sort(submissionList));

        assertTrue(sorted);

        List<Submission> submissions = null;
        boolean sort = isSortedByName(sortSubmission.sort(submissions));

        assertTrue(sort);
    }

    @Test
    public void testSortSubmissionByLikes() {
        submissionList = generateSubmission();

        SortSubmission sortSubmission = new SortSubmissionByLikes();
        boolean sorted = isSortedByLikes(sortSubmission.sort(submissionList));

        assertTrue(sorted);

        List<Submission> submissions = null;
        boolean sort = isSortedByLikes(sortSubmission.sort(submissions));

        assertTrue(sort);


    }

    @Test
    public void testSortSubmissionByDate() {
        submissionList = generateSubmission();
        List<Long> list = new ArrayList<>();

        for (int i = 0; i < submissionList.size(); i++) {

            list.add(submissionList.get(i).getEndOfEvent());
            //System.out.println();
        }


        System.out.println(list);

        SortSubmission sortSubmission = new SortSubmissionByEndOFEvent();

        submissionList = sortSubmission.sort(submissionList);

        List<Long> list1 = new ArrayList<>();

        for (int i = 0; i < submissionList.size(); i++) {


            list1.add(submissionList.get(i).getEndOfEvent());
            //System.out.println();
        }


        System.out.println(list1);

        boolean sorted = isSortedByTime(sortSubmission.sort(submissionList));

        assertTrue(sorted);

        List<Submission> submissions = null;
        boolean sort = isSortedByTime(sortSubmission.sort(submissions));

        assertTrue(sort);


    }


    private boolean isSortedByName(List<Submission> submissionList) {
        if (submissionList == null) {
            return true;
        }
        for (int i = 0; i < submissionList.size() - 1; i++) {
            if ((submissionList.get(i).getName().compareTo(submissionList.get(i + 1).getName())) == -1) {
                return false;
            }
        }
        return true;
    }

    private boolean isSortedByLikes(List<Submission> submissionList) {

        if (submissionList == null) {
            return true;
        }


        for (int i = 0; i < submissionList.size() - 1; i++) {
            if ((submissionList.get(i).getName().toLowerCase().compareTo(submissionList.get(i + 1).getName().toLowerCase())) == -1) {
                return false;
            }
        }
        return true;
    }

    private boolean isSortedByTime(List<Submission> submissionList) {

        if (submissionList == null) {
            return true;
        }
        for (int i = 0; i < submissionList.size() - 1; i++) {
            if ((submissionList.get(i).getEndOfEvent() > (submissionList.get(i + 1).getEndOfEvent()))) {
                return false;
            }
        }
        return true;

    }


    private List<Submission> generateSubmission() {

        Random random = new Random();

        int n = 7;

        int numberOfSubmission = random.nextInt(n);
        int numberOfChars;
        int numberOfLikes;
        List<Submission> submissions = new ArrayList<>();

        for (int i = 0; i < numberOfSubmission; i++) {
            numberOfChars = random.nextInt(n);
            numberOfLikes = random.nextInt(n);


            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(n));

            Submission submission = new Submission.Builder().name(generateString(numberOfChars)).rating(numberOfLikes).endOfEvent(calendar).build();
            submissions.add(submission);

        }

        return submissions;


    }


    private String generateString(final int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) (r.nextInt((int) (Character.MAX_VALUE)));
            sb.append(c);
        }
        return sb.toString();
    }


}
