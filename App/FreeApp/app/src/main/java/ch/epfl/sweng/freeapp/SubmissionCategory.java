package ch.epfl.sweng.freeapp;

/**
 * Enum  which represents the various categories
 */
public enum SubmissionCategory {

    Food,
    Clothing,
    Nightlife,
    Sport,
    Miscellaneous,
    Culture,
    Events,
    Goods,
    Lifestyle;

    public static boolean contains(String test) {

        for (SubmissionCategory c : SubmissionCategory.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;

    }

}
