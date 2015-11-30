package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Created by francisdamachi on 28/11/15.
 */
public enum Vote {

    LIKE(1), DISLIKE(-1), NEUTRAL(0);

    private int value;

    Vote(int value){
        this.value = value;

    }

    public int getValue(){
        return value;
    }

}
