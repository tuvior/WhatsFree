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
    private String image;

    public SubmissionShortcut(String name, String location){
        //TODO: complete constructor with image
        this.name = name;
        this.location = location;
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getImage(){
        return image;
    }

}
