package ch.epfl.sweng.freeapp;

import org.junit.Test;

import java.util.Calendar;

import static junit.framework.Assert.assertTrue;

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

        assertTrue(submission.getKeywords().equals(keywords));
        assertTrue(submission.getSubmitted() == calendarSubmitted.getTime().getTime());
        assertTrue(submission.getStartOfEvent() == calendarStartOfEvent.getTime().getTime());
        assertTrue(submission.getEndOfEvent() == calendarEndOfEvent.getTime().getTime());

    }

    private void checkSubmissionFields(Submission submission){
        assertTrue(submission.getName().equals(name));
        assertTrue(submission.getDescription().equals(description));
        assertTrue(submission.getCategory().equals(category.toString()));
        assertTrue(submission.getLocation().equals(location));
        assertTrue(submission.getId().equals(id));
    }

}
