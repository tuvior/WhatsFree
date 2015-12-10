package ch.epfl.sweng.freeapp;


import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.freeapp.mainScreen.Vote;


/**
 * Class which uses a builder Pattern, it  represents a basic submission.
 * You can construct this class through the constructor or with the builder pattern
 */
public class Submission {

    private String name;
    private SubmissionCategory category;
    private String description;
    private String location;
    private String keywords;
    private String image; //see how to deal with it
    private Date submitted;
    private Date startOfEvent;
    private Date endOfEvent;

    private String id;

    private double latitude;
    private double longitude;
    private int rating;
    private Vote vote;

    public Submission(String name, String description, SubmissionCategory category, String location, String image, String id) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
        this.image = image;
        this.id = id;
    }

    private Submission(Builder builder) {
        this.name = builder.name;
        this.category = builder.category;
        this.description = builder.description;
        this.location = builder.location;
        this.keywords = builder.keywords;
        this.id = builder.id;
        this.image = builder.image;
        this.rating = builder.rating;


        if (builder.calendarSubmitted != null) {
            this.submitted = builder.calendarSubmitted.getTime();
        }
        if (builder.calendarStartOfEvent != null) {
            this.startOfEvent = builder.calendarStartOfEvent.getTime();
        }
        if (builder.calendarEndOfEvent != null) {
            this.endOfEvent = builder.calendarEndOfEvent.getTime();
        }

        if (builder.latitude != 0) {
            this.latitude = builder.latitude;
        }

        if (builder.longitude != 0) {
            this.longitude = builder.longitude;

        }
    }

    public void setRating(int rating){
        this.rating =  rating;
    }
    public int getRating() {
        return rating;
    }

    public String getCategory() {
        return category.toString();
    }

    public long getSubmitted() {
        return submitted.getTime();
    }

    public long getStartOfEvent() {
        return startOfEvent.getTime();
    }

    public long getEndOfEvent() {
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




    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public Vote getVote(){
        return this.vote;
    }
    public void setVote (Vote vote){
        this.vote  = vote;
    }

    public static class Builder {


        private String name;
        private SubmissionCategory category;
        private String description;
        private String location;
        private String keywords;
        private String id;

        private Calendar calendarSubmitted;
        private Calendar calendarStartOfEvent;
        private Calendar calendarEndOfEvent;
        private double latitude;
        private double longitude;

        private String image; //see how to deal with it
        private int rating;


        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder submitted(Calendar submitted) {
            this.calendarSubmitted = submitted;
            return this;
        }

        public Builder startOfEvent(Calendar startOfEvent) {
            this.calendarStartOfEvent = startOfEvent;
            return this;
        }

        public Builder endOfEvent(Calendar endOfEvent) {
            this.calendarEndOfEvent = endOfEvent;
            return this;
        }

        public Builder category(SubmissionCategory category) {
            this.category = category;
            return this;

        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder keywords(String keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }


        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }




        public Submission build() {
            return new Submission(this);
        }

        public Builder rating(int numberOfLikes) {

            this.rating = numberOfLikes;
            return this;
        }
    }

}
