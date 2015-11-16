package ch.epfl.sweng.freeapp;

/**
 * Created by lois on 11/10/15.
 */
public class Submission {

    private String name;
    private String description;
    private SubmissionCategory category;

    public Submission(String name, String description, SubmissionCategory category){
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public SubmissionCategory getCategory(){
        return category;
    }

}
