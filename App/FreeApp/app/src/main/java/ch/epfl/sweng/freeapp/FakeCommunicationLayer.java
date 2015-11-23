package ch.epfl.sweng.freeapp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import java.util.ArrayList;

/**
 * This communication layer is independent of the server and allows sending the app
 * some predefined responses.
 *
 * Created by lois on 11/13/15.
 */
public class FakeCommunicationLayer implements DefaultCommunicationLayer {

    private int startTime = 17;
    private int endTime = 18;

    private Bitmap bitmapImage;
    private static String image = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEB\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCACgAHgDASIA\nAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\nAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\nODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\np6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\nAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\nBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\nU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9m7PR\ntqD5PTdwCDtXacHA5wGO0eyqMHdVwaWF4C4BYDgc4GcNkKoIzkjOSOCBgA12kFuqgAfgpVcAgYJG\nOOARxg7QvACqpD2iXI+UdQMBc8+hx3Dc9Ac85AIFd05Sbutvv/l1srX0skvxSdz4elQjyRve6bXw\nx6uK7W73e+tkm3d8M+nKATs2jGDxhcYz2UZAwAvTjgdC1Zc2mhWJILEZycD+HsxHynGc/LwOQOVI\nr0OSJGwdoAGcdzztx03Bgeo/754rMmhXOSvU8DAIBPOc9yBnkE+nfNQ57fJeV/w210Xyvs9JYaMu\nzvbprstWrK/TV3a30vZcI2ngZJB77lxkA9xnghQCOAeepzgVnXOncEBCcEY6KWB54PIOBtxk+voV\n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\nhj0h7OFJ/nr4EfCv4Zfsd+DbfX7q/wBD8MWEmkJptvqOtavHb2ZtLRjLPa6bNqlybiV/tjPcXY+0\n/bbueWS4uklbyCv67eL/AI6eGPgj+z/4ibxArXFno81xqVzYR27X898kkgVdJFtZyNM8t/JBLao0\nayfZnuYpDEVPkD+d743DVNTi0v4r/tRa7p0l6fDmmR/Dr9nvw3pMk89i1/Gmq6VZ+K7VBYafJJpM\n15cXWoeF7CK700ahLajxPrWo3D6pbV8g8FLGXbxEop8lTExbVnWguWDcOZupJU+ZU4+9y8ztyxbT\n/p7h3G4atOtGlQw+FpYan7LD1pNRSw1RxqV5ubXs6dKVT2dSs9PeUUlLRUvafE/7TOk/F03GoeFr\nfWLnQoLi4ittZmsntdN1aOCOSI3OlLdRpJeWQlCRRXgieGa5+0R200kkT+V4re3ciC+1SaIwW8nm\nGFWJ8sqJN28JkKZAwDQ7l3J5bKGyzM2x8LPh9488caTL4n8WQDwfoFxGEtDqUtrDcJDEN5t7C1Me\n2b7NG1v5kxgNnAgihiWXASPzj9qDxTpfhSDTPC3hq5S4upoPs4gikj5uHjVcuEYDc0jeZKVMm2NZ\nW2M65bxfY01iZUcMm1Od5SdnFKK96TnG0Gna75VyLZOzie1mGIjDDKc60pRjdUXUg6UqmtoOFJty\nipXvGEk2tFJRk0o+x/BD4iarpySavos5Sa0uLiB/MmuUgu7fdADZ3SW7wSyWso3u5juoZoJIopoT\n5qrPB9w+Kf2kbW2+F8+t/D34R6X4i8fWF7JcX9j4+8Xak/he08O6ZDNdatqunXemaNeaxr2uyiAW\nWmeHbiLw5YrJfLfXHikLYjT9Q/MT4J2U+m+GLe2mmDzTl7i7Y7w7Hb8+4jhd7KQjHadrvGcyKgr6\nE0/xUmh6frem3VsPM1KzvpVWVyI9nkuJyc7tqPGqRlItqZkQyGNmZzrhM1x+V4uUsG6dWhKuqksN\nWg50arTgndc0JLnVuZwcU0op3itfAzfhjJM9wdCpmlGpRxawzpRxmGqewxVKLTlFOSVSnU5ZOfJG\ntTm6d248rbTzbD/gtTDpnirT2s/2XfB8vhFY9txpLfEDUB4inkjs5F8yDxEfCQ0y1ia/aGf7NJ4Z\nvCtistst007LdRy+Of8Agtv4z1mTxBcfD74CeAvh9Jq9nax2M2u+ItS+IOo6Tqa2lja3GqRzw6J4\n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\n";
    private String keywords = "Beautiful Fun nice ";
    private String location = "Ecublens Epfl";
    private Calendar startOfEvent = Calendar.getInstance();
    private Calendar endOfEvent = Calendar.getInstance();

    private Submission.Builder submissionBuilderCroissant = new Submission.Builder();
    private Submission.Builder submissionBuilderUnicornDiscount = new Submission.Builder();
    private Submission.Builder submissionBuilderFreeClubEntrance = new Submission.Builder();
    private Submission.Builder submissionBuilderFreeDonuts =  new Submission.Builder();

    private static Submission freeCroissants = new Submission("Free Croissants", "There's a huge croissant giveaway at Flon!", SubmissionCategory.FOOD, "Presidence de la polynesie francaise", image);
    private static Submission freeDonuts =  new Submission("Free Donuts", "Migros gives a free dozen to the first 5 customers",SubmissionCategory.FOOD, "Motu Uta", image);
    private static Submission unicornDiscount = new Submission("Unicorn Discount", "Get one of our wonderful white unicorns!", SubmissionCategory.MISCELLANEOUS, "Papeete Tahiti Temple", image);
    private static Submission freeClubEntrance  = new Submission("Free Entrance Tonight", "Come get wasted for free tonight!", SubmissionCategory.NIGHTLIFE, "Port de Papeete", image);


    public FakeCommunicationLayer() {

    }

    public ResponseStatus sendAddSubmissionRequest(Submission submission) {
        //TODO
        return ResponseStatus.OK;
    }

    @Override
    public ArrayList<SubmissionShortcut> sendSubmissionsRequest() throws JSONException {

        ArrayList<SubmissionShortcut> submissionShortcuts = new ArrayList<>();

        submissionShortcuts.add(toShortcut(freeCroissants));
        submissionShortcuts.add(toShortcut(unicornDiscount));
        submissionShortcuts.add(toShortcut(freeClubEntrance));

        return submissionShortcuts;
    }

    @Override
    public Submission fetchSubmission(String name) {

        Submission submission;
        switch (name) {
            case "Free Croissants":
                submission = freeCroissants;
                break;
            case "Unicorn Discount":
                submission = unicornDiscount;
                break;
            case "Free Entrance Tonight":
                submission = freeClubEntrance;
                break;
            default:
                return null;
        }

        return submission;
    }

    @Override
    public ArrayList<SubmissionShortcut> sendCategoryRequest(SubmissionCategory category){

        ArrayList<SubmissionShortcut> submissionShortcuts = new ArrayList<>();

        switch (category){
            case FOOD: {
                submissionShortcuts.add(toShortcut(freeCroissants));
                submissionShortcuts.add(toShortcut(freeDonuts));
            }
            break;
            case MISCELLANEOUS: {
                submissionShortcuts.add(toShortcut(unicornDiscount));
            }
            break;
            case NIGHTLIFE: {
                submissionShortcuts.add(toShortcut(freeClubEntrance));
            }
            break;
            default:
        }

        return submissionShortcuts;
    }

    /**
     * The server sends the submissions as a JSONArray, and the communication layer
     * conveys those to the tabs as an ArrayList
     * @param jsonSubmissions
     * @return
     * @throws JSONException
     */
    public ArrayList<SubmissionShortcut> jsonArrayToArrayList(JSONArray jsonSubmissions) throws JSONException {

        ArrayList<SubmissionShortcut> submissionsList = new ArrayList<>();

        for(int i = 0; i < jsonSubmissions.length(); i++){
            //TODO: also include image
            JSONObject jsonSubmission = jsonSubmissions.getJSONObject(i);
            String name = jsonSubmission.getString("name");
            String location = jsonSubmission.getString("location");

            SubmissionShortcut submission = new SubmissionShortcut(name, location);
            submissionsList.add(submission);
        }

        return submissionsList;

    }

    private String encodeImage(AssetManager assetManager) {
        if (assetManager != null) {

            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("a.png");
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmapImage = BitmapFactory.decodeStream(inputStream);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


        } else {
            return null;
        }

    }

    private Submission createSubmission(Submission.Builder builder){

        builder.location(location);
        builder.category(SubmissionCategory.FOOD);

        startOfEvent.set(Calendar.HOUR_OF_DAY, startTime);
        endOfEvent.set(Calendar.HOUR_OF_DAY, endTime);

        builder.startOfEvent(startOfEvent);
        builder.endOfEvent(endOfEvent);
        builder.image(image);
        builder.keywords(keywords);
        builder.submitted(startOfEvent);

        return builder.build();

    }

    /**
     * Transforms the submission into its shortcut equivalent
     *
     * @param submission
     * @return the shortcut version of the submission
     */
    private SubmissionShortcut toShortcut(Submission submission){
        return new SubmissionShortcut(submission.getName(), submission.getLocation());
    }


}
