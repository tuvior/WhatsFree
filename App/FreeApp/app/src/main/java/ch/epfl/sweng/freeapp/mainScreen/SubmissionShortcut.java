package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Represents a submission's name and picture
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
