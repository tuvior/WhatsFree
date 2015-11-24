package ch.epfl.sweng.freeapp;

/**
 * Created by lois on 11/10/15.
 */
public enum SubmissionCategory {

    FOOD,
    CLOTHING,
    NIGHTLIFE,
    SPORT,
    MISCELLANEOUS,
    CULTURE,
    LIFESTYLE;

    public static boolean contains(String test) {

        for (SubmissionCategory c : SubmissionCategory.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;

    }

}
