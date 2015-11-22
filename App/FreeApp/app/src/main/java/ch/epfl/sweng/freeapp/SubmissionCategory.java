package ch.epfl.sweng.freeapp;

/**
 * Created by lois on 11/10/15.
 */
public enum SubmissionCategory {

    FOOD{
        @Override
        public String toString() {
            return "FOOD";
        }
    },
    CLOTHING{
        @Override
        public String toString() {
            return "CLOTHING";
        }
    },
    NIGHTLIFE{
        @Override
        public String toString() {
            return "NIGHTLIFE";
        }
    },

    SPORT{
        @Override
        public String toString() {
            return "SPORT";
        }
    },
    MISCELLANEOUS{
        @Override
        public String toString() {
            return "MISCELLANEOUS";
        }
    },
    CULTURE{
        @Override
        public String toString() {
            return "CULTURE";
        }

    },

    LIFESTYLE{
        @Override
        public String toString() {
            return "CULTURE";
        }

    }


}
