package ch.epfl.sweng.freeapp.communication;


import org.json.JSONException;
import java.util.ArrayList;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;
import ch.epfl.sweng.freeapp.mainScreen.Vote;

/**
 * This communication layer is independent of the server and allows sending the app
 * some predefined responses.
 * <p/>
 * Created by lois on 11/13/15.
 */
public class FakeCommunicationLayer implements DefaultCommunicationLayer {

    private final String image = "Fake image";

    private final Submission freeCroissants = new Submission("Free Croissants", "There's a huge croissant giveaway at EPFL!", SubmissionCategory.Food, "EPFL", image, "id0");
    private final Submission freeDonuts = new Submission("Free Donuts", "Migros gives a free dozen to the first 5 customers", SubmissionCategory.Food, "This is not a valid location", image, "id1");
    private final Submission unicornDiscount = new Submission("Unicorn Discount", "Get one of our wonderful white unicorns!", SubmissionCategory.Miscellaneous, "Papeete Tahiti Temple", image, "id2");
    private final Submission freeClubEntrance = new Submission("Free Entrance Tonight", "Come get wasted for free tonight!", SubmissionCategory.Nightlife, "Port de Papeete", image, "id3");
    private final Submission myBike = new Submission("My Bike", "I don't want it no more", SubmissionCategory.Sport, "I don't care about writing a valid location", image, "id3");

    @Override
    public ArrayList<Submission> sendSubmissionsRequest() throws JSONException {

        ArrayList<Submission> submissions = new ArrayList<>();

        submissions.add(freeCroissants);
        submissions.add(unicornDiscount);
        submissions.add(freeClubEntrance);
        submissions.add(freeDonuts);
        submissions.add(myBike);

        return submissions;
    }

    @Override

    public Submission fetchSubmission(String id) {


        Submission submission;
        switch (id) {
            case "id0":
                submission = freeCroissants;
                break;
            case "id2":
                submission = unicornDiscount;
                break;
            case "id3":
                submission = freeClubEntrance;
                break;
            default:
                return null;
        }


        return submission;
    }


    @Override
    public ArrayList<Submission> sendCategoryRequest(SubmissionCategory category) {

        ArrayList<Submission> submissions = new ArrayList<>();

        switch (category) {
            case Food: {
                submissions.add(freeCroissants);
                submissions.add(freeDonuts);
            }
            break;
            case Miscellaneous: {
                submissions.add(unicornDiscount);
            }
            break;
            case Nightlife: {
                submissions.add(freeClubEntrance);
            }
            break;
            default:
        }

        return submissions;
    }

    @Override
    public ResponseStatus sendVote(Submission submission, Vote vote) throws CommunicationLayerException {

        if (submission == null) {
            throw new CommunicationLayerException();
        }

        //for now lets just allow all votes
        return ResponseStatus.OK;

    }

    @Override
    public ResponseStatus sendAddSubmissionRequest(Submission param) throws CommunicationLayerException {
        return ResponseStatus.OK;
    }

    /**
     * Used to access defined submissions in tests
     * @return All the predefined fake Submissions
     */
    public ArrayList<Submission> getFakeSubmissions(){
        ArrayList<Submission> predefinedSubmissions = new ArrayList<>();

        predefinedSubmissions.add(freeCroissants);
        predefinedSubmissions.add( freeClubEntrance);
        predefinedSubmissions.add(unicornDiscount);
        predefinedSubmissions.add(freeDonuts);
        predefinedSubmissions.add(myBike);

        return predefinedSubmissions;
    }

    /**
     * Used to access predefined submissions in tests
     * @return The freeCroissant predefined submission
     */
    public Submission getFreeCroissants(){
        return freeCroissants;
    }

    /**
     * Used to access predefined submissions in tests
     * @return The unicornDiscount predefined submission
     */
    public Submission getUnicornDiscount(){
        return unicornDiscount;
    }

    /**
     * Used to access predefined submissions in tests
     * @return The freeDonuts predefined submission
     */
    public Submission getFreeDonuts(){
        return freeDonuts;
    }

    /**
     * Used to access predefined submissions in tests
     * @return The freeClubEntrance predefined submission
     */
    public Submission getFreeClubEntrance(){
        return freeClubEntrance;
    }


}
