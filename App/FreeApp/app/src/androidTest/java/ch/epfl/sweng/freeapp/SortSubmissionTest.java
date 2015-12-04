package ch.epfl.sweng.freeapp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by francisdamachi on 04/12/15.
 */
public class SortSubmissionTest {







    @Test
    public void  testSortSubmissionByName(){

    }

    @Test
    public void  testSortSubmissionByLikes(){

    }

    @Test
    public void  testSortSubmissionByDate(){


    }


    private  boolean isSortedByName(List<Submission> submissionList) {
        for (int i = 0; i < submissionList.size()-1; i++) {
            if ((submissionList.get(i).getName().compareTo(submissionList.get(i+1).getName())) == -1 ) {
                return false;
            }
        }
        return true;
    }

    private  boolean isSortedByLikes(List<Submission> submissionList){


        for (int i = 0; i < submissionList.size()-1; i++) {
            if ((submissionList.get(i).getName().compareTo(submissionList.get(i+1).getName())) == -1 ) {
                return false;
            }
        }
        return true;
    }

    private boolean isSortedByTime(List<Submission> submissionList){

        for (int i = 0; i < submissionList.size()-1; i++) {
            if ((submissionList.get(i+1).getEndOfEvent() > (submissionList.get(i).getEndOfEvent()))) {
                return false;
            }
        }
        return true;

    }


    private  List<Submission> generateSubmission( ){

        Random random = new Random();

        int n = 7;

        int numberOfSubmission = random.nextInt(n);
        int numberOfchars;
        int numberOfLikes;
        List<Submission> submissions = new ArrayList<>();

        Random r =new Random();
        long unixtime;

        for(int i = 0; i <numberOfSubmission; i++){
            numberOfchars = random.nextInt(n);
            numberOfLikes = random.nextInt(n);
            unixtime = (long) (1293861599+r.nextDouble()*60*60*24*365);


            new Submission.Builder().name(generateString(numberOfchars)).

        }



    }


    private String generateString(final int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = (char)(r.nextInt((int)(Character.MAX_VALUE)));
            sb.append(c);
        }
        return sb.toString();
    }




}
