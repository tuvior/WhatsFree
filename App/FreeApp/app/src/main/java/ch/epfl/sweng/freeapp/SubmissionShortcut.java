package ch.epfl.sweng.freeapp;

/**
 * Represents a submission's lighter version.
 * SubmissionShortcuts are used when retrieving lots of submissions.
 *
 * Created by lois on 11/14/15.
 */
public class SubmissionShortcut {

    //TODO: define image
    private String name;
    private String location;

    public SubmissionShortcut(String name, String location){
        this.name = name;
        this.location = location;
    }

    public String getName(){
        return name;
    }

    public String getLocation() {
        return location;
    }

}
