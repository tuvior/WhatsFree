package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Created by francisdamachi on 28/11/15.
 */
public enum Vote {

    LIKE(1), DISLIKE(-1), NEUTRAL(0);

    private int value;

    Vote(int value) {
        this.value = value;

    }

    public  int getValue() {
        return value;
    }

    public static  Vote value(String vote){
        if(vote.equals("0")){
            return NEUTRAL;
        }else if( vote.equals("1") ){
            return LIKE;
        }else{
            assert(vote.equals("-1"));
            return DISLIKE;
        }
    }

}
