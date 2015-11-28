package ch.epfl.sweng.freeapp;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by francisdamachi on 14/11/15.
 */
public  class Submission  {

    private String name;
    private SubmissionCategory category;
    private String description;
    private String location;
    private String keywords;
    private String image; //see how to deal with it
    private Date submitted;
    private Date startOfEvent;
    private Date endOfEvent;
    private int id;
    private int likes;
    private int dislikes;


    public Submission(String name , String description, SubmissionCategory category, String location, String image){
        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
        this.image = image;
    }

    public static class Builder {


        private String name;
        private SubmissionCategory category;
        private String description;
        private String location;
        private String keywords;

        private Calendar calendarSubmitted;
        private Calendar calendarStartOfEvent;
        private Calendar calendarEndOfEvent;

        private String image; //see how to deal with it

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder submitted(Calendar submitted){
            this.calendarSubmitted = submitted;
            return this;
        }
        public Builder startOfEvent(Calendar startOfEvent){
            this.calendarStartOfEvent = startOfEvent;
            return this;
        }

        public Builder endOfEvent(Calendar endOfEvent){
            this.calendarEndOfEvent = endOfEvent;
            return this;
        }
        public Builder category(SubmissionCategory category){
            this.category = category;
            return this;

        }
        public Builder description  (String description){
            this.description = description;
            return this;
        }
        public Builder location(String location){
            this.location = location;
            return this;
        }
        public Builder keywords(String keywords){
            this.keywords = keywords;
            return this;
        }


        public Builder image(String image){
            this.image = image;
            return this;
        }


        public Submission build(){
            return new Submission(this);
        }

    }

    private  Submission(Builder builder) {
        this.name = builder.name;
        this.category = builder.category;
        this.description = builder.description;
        this.location = builder.location;
        this.keywords = builder.keywords;

        this.image = builder.image;

        if(builder.calendarSubmitted != null) {
            this.submitted = builder.calendarSubmitted.getTime();
        }
        if(builder.calendarStartOfEvent != null) {
            this.startOfEvent = builder.calendarStartOfEvent.getTime();
        }
        if(builder.calendarEndOfEvent != null) {
            this.endOfEvent = builder.calendarEndOfEvent.getTime();
        }

    }



    public String getCategory() {
        return category.toString();
    }

    public long getSubmitted() {
        return  submitted.getTime();
    }

    public long getStartOfEvent() {
        return startOfEvent.getTime();
    }

    public long getEndOfEvent(){
        return endOfEvent.getTime();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getImage() {
        return image;
    }

    public int getLikes (){
        return likes;
    }

    public int getDislikes(){
        return dislikes;
    }

    public int getId(){
        return id;
    }

}
