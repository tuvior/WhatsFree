package ch.epfl.sweng.freeapp;

import org.junit.Test;

import java.util.Calendar;

/**
 * Tests for the Submission class
 */
public class SubmissionTest {


    private String name = "Test name";
    private String description = "Test description";
    private SubmissionCategory category = SubmissionCategory.Miscellaneous;
    private String location = "Test location";
    private String image = "Test image";
    private String id = "Test id";
    private String keywords = "Test keywords";
    private Calendar calendarSubmitted = null;
    private Calendar calendarStartOfEvent = null;
    private Calendar calendarEndOfEvent = null;
    private int likes = 0;

    @Test
    public void testConstructor(){
        Submission submission = new Submission(name, description, category, location, image, id);
        checkSubmissionFields(submission);
    }

    @Test
    public void testBuilder(){
        Submission.Builder builder = new Submission.Builder();
        builder.name(name);
        builder.description(description);
        builder.category(category);
        builder.location(location);
        builder.id(id);
        builder.keywords(keywords);
        builder.submitted(calendarSubmitted);
        builder.startOfEvent(calendarStartOfEvent);
        builder.endOfEvent(calendarEndOfEvent);


        Submission submission = builder.build();
        checkSubmissionFields(submission);

        assert(submission.getKeywords().equals(keywords));
        assert(submission.getSubmitted() == calendarSubmitted.getTime().getTime());
        assert(submission.getStartOfEvent() == calendarStartOfEvent.getTime().getTime());
        assert(submission.getEndOfEvent() == calendarEndOfEvent.getTime().getTime());

    }

    private void checkSubmissionFields(Submission submission){
        assert(submission.getName().equals(name));
        assert(submission.getDescription().equals(description));
        assert(submission.getCategory().equals(category.toString()));
        assert(submission.getLocation().equals(location));
        assert(submission.getImage().equals(image));
        assert(submission.getId().equals(id));
    }

}
