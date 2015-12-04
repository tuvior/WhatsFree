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

    private int likes;
    private int dislikes;
    private String id;

    private String latitude;
    private String longitude;
    private int rating;

    public Submission(String name , String description, SubmissionCategory category, String location, String image, String id){
        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
        this.image = image;
        this.id = id;
    }

    public int getRating() {
        return rating;
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
        private String latitude;
        private String longitude;

        private String image; //see how to deal with it
        private int likes;

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

        public Builder latitude(String latitude){
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(String longitude){
            this.longitude = longitude;
            return this;
        }



        public Builder image(String image){
            this.image = image;
            return this;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder likes(int likes){
            this.likes = likes; return this;
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
        this.id = builder.id;

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


        if(builder.latitude != null){
            this.latitude = builder.latitude;
        }

        if(builder.longitude != null ){
            this.longitude = builder.longitude;

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



    public void setLikes(int likes ){
        this.likes = likes;
    }
    public  void setDislikes(int dislikes){
        this.dislikes =  dislikes;
    }


    public String getId() {
        return id;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

}
