package ch.epfl.sweng.freeapp.mainScreen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ch.epfl.sweng.freeapp.ProvideImage;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultCommunicationLayer;
import ch.epfl.sweng.freeapp.communication.ProvideCommunicationLayer;
import ch.epfl.sweng.freeapp.communication.ResponseStatus;

public class CreateNewSubmissionActivity extends AppCompatActivity {

    private static final int MAX_CHARACTERS = 60;
    private static final int MIN_CHARACTERS = 4;
    private int SCALE_FACTOR = 300;

    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar startEventCalendar = Calendar.getInstance();
    private Calendar endEventCalendar = Calendar.getInstance();

    private TimeZone timeZone = TimeZone.getTimeZone("Europe/Zurich");
    //private Calendar submissionCalendar = Calendar.getInstance();




    private int startYear  = currentCalendar.get(Calendar.YEAR);
    private int startMonth = currentCalendar.get(Calendar.MONTH);
    private int startDay   = currentCalendar.get(Calendar.DAY_OF_MONTH);
    private DefaultCommunicationLayer communicationLayer = ProvideCommunicationLayer.getCommunicationLayer();
    /// private int DATE_DIALOG_ID = 0;

    private final static int PICTURE_REQUEST = 200;
    private final static int IMAGE_GALLERY_REQUEST = 201;
    //private Uri imageUri;
    private Bitmap bitmap;
    // = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher);

    private EditText nameOfEvent ;
    private EditText eventDescription ;
    private EditText location;
    private ImageView imageView;

    private
    TextView  date ;
    private TextView startTime ;
    private TextView endTime ;
    private EditText keywords ;


    private Date dateOfEvent = new Date();
    private Date endDate = new Date();
    private Spinner spinnerCategory;
    private int textSize = 19;

    private Submission.Builder submission = new Submission.Builder();
    private ProvideImage provideImage = new ProvideImage();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_submission2);

        this.spinnerCategory = (Spinner)findViewById(R.id.categories);
        this.imageView = (ImageView)findViewById(R.id.picture);
        this.nameOfEvent = (EditText)findViewById(R.id.NameOfEvent);
        this.eventDescription= (EditText)findViewById(R.id.Description);
        this.location = (EditText)findViewById(R.id.Location);
        this.keywords = (EditText)findViewById(R.id.keywords);
        this.date = (TextView)findViewById(R.id.date);
        this.startTime =   (TextView)findViewById(R.id.startTime);
        this.endTime =  (TextView)findViewById(R.id.endTime);



        TextView categoriesText = (TextView)findViewById(R.id.categoriesText);
        categoriesText.setTextSize(textSize);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories,android.R.layout.simple_spinner_dropdown_item);
        //layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply adapter to the spinner
        spinnerCategory.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_new_submission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSetDateClicked(View view){


        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {




                // dateOfEvent.setYear(year); dateOfEvent.setMonth(monthOfYear);dateOfEvent.setDate(dayOfMonth);
                startEventCalendar.set(Calendar.YEAR,year);
                startEventCalendar.set(Calendar.MONTH,monthOfYear);
                startEventCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);



                //endDate.setYear(year);endDate.setYear(monthOfYear);endDate.setYear(dayOfMonth);

                endEventCalendar.set(Calendar.YEAR, year);
                endEventCalendar.set(Calendar.MONTH, monthOfYear);
                endEventCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                submission.startOfEvent(startEventCalendar);
                submission.endOfEvent(endEventCalendar);

                startEventCalendar.setTimeZone(timeZone);
                endEventCalendar.setTimeZone(timeZone);

                date.setText(dayOfMonth +"-"+(monthOfYear+1) + "-"+ year );

            }
        };


        DatePickerDialog dialogPicker = new DatePickerDialog(this, datePickerListener,this.startYear,this.startMonth,this.startDay);
        dialogPicker.show();




    }

    public void onClickSetTime(View view){

        final Button startTimeButton = (Button)findViewById(R.id.startButton);
        final Button startOrEnd = (Button)view;

        //Process in order to get current time
        final Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                String hours = correctFormat(hourOfDay);
                String minuteOfDay = correctFormat(minute);

                if(startOrEnd.getText().toString().equals(startTimeButton.getText().toString())){


                    // dateOfEvent.setHours(hourOfDay);dateOfEvent.setMinutes(minute);
                    startEventCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    startEventCalendar.set(Calendar.MINUTE,minute);



                    startTime.setText(hours+":"+ minuteOfDay);

                    submission.startOfEvent(startEventCalendar);

                }else{

                    //endDate.setHours(hourOfDay);endDate.setMinutes(minute);
                    endEventCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    endEventCalendar.set(Calendar.MINUTE,minute);

                    endTime.setText(hours+":"+ minuteOfDay);

                    submission.endOfEvent(endEventCalendar);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,onTimeSetListener,hours,minutes,true);
        timePickerDialog.show();
    }



    private String correctFormat(int number){
        String numberString = Integer.toString(number);
        if(number > 0 && number < 10){
            numberString = "0"+numberString;
        }

        return numberString;

    }




    public  void onClickTakeImage(View view){



        // condition necessary in order to inject a default image
        if(provideImage.getTypeOfImage() == ProvideImage.ImageType.FROM_TEST ){
            bitmap = ProvideImage.getImage();
            imageView.setImageBitmap(ProvideImage.getImage());

        }else {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, PICTURE_REQUEST);
        }

    }

    public void onClickExistingPicture(View view){

        // condition necessary in order to inject a default image
        if(provideImage.getTypeOfImage() == ProvideImage.ImageType.FROM_TEST ){
            imageView.setImageBitmap(ProvideImage.getImage());

        }else {
            //invoking image gallery using implicit intent
            Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK);

            //just basically says where we want to find the data
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String pictureDirectoryPath = pictureDirectory.getPath();

            //Get URI representation
            Uri data = Uri.parse(pictureDirectoryPath);

            //set the data image and type. Get all images

            photoGalleryIntent.setDataAndType(data, "image/*");

            startActivityForResult(photoGalleryIntent, IMAGE_GALLERY_REQUEST);
        }

    }




    //What the camera will output
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST) {
                bitmap = (Bitmap) intent.getExtras().get("data");
                if(bitmap != null ) {
                    imageView.setImageBitmap(bitmap);
                }
            }else if(requestCode == IMAGE_GALLERY_REQUEST ){
                Uri imageUri = intent.getData();

                //Declare an inputStream in order to read the image from the SD card
                InputStream inputStream;

                try {
                    //input stream based on Uri of the  image.
                    inputStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);// Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream), SCALE_FACTOR, SCALE_FACTOR,false);
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, SCALE_FACTOR, SCALE_FACTOR, false));
                }catch(FileNotFoundException e ){
                    e.printStackTrace();
                }
            }
        }


    }






    public void onClickCreateButton(View view ){

        //we want to check if all field


        EditText nameOfEvent = (EditText)findViewById(R.id.NameOfEvent);
        EditText eventDescription = (EditText)findViewById(R.id.Description);
        EditText location = (EditText)findViewById(R.id.Location);

        TextView  date = (TextView)findViewById(R.id.date);
        TextView startTime = (TextView)findViewById(R.id.startTime);
        TextView endTime  = (TextView)findViewById(R.id.endTime);

        ImageView imageView = (ImageView)findViewById(R.id.picture);
        EditText keywords = (EditText)findViewById(R.id.keywords);



        if(isWhiteSpaces(nameOfEvent.getText().toString())){
            nameOfEvent.setError("Please fill in name");
            nameOfEvent.setText("");
            Toast.makeText(this, "No name", Toast.LENGTH_SHORT).show();

        }else if(isWhiteSpaces(eventDescription.getText().toString())){
            eventDescription.setError("Please fill in description");
            eventDescription.setText("");

            Toast.makeText(this, "No description", Toast.LENGTH_SHORT).show();


        }else if(isWhiteSpaces(location.getText().toString())){

            location.setError("Please input  location");
            location.setText("");

            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(date.getText().toString())){

            Toast.makeText(this, "No date", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(startTime.getText().toString()) ){

            Toast.makeText(this,"No start time", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(endTime.getText().toString())){

            Toast.makeText(this,"No end time",Toast.LENGTH_SHORT).show();

        }else if(imageView.getDrawable() == null ){

            Toast.makeText(this,"No picture", Toast.LENGTH_SHORT).show();

        }else if(isWhiteSpaces(keywords.getText().toString())){
            keywords.setError("Put some keywords");
            keywords.setText("");
            Toast.makeText(this, "No keywords", Toast.LENGTH_SHORT).show();
        }else{


            currentCalendar.setTimeZone(timeZone);

            this.dateOfEvent = startEventCalendar.getTime();
            this.endDate = endEventCalendar.getTime();


            if(endDate.before(dateOfEvent)|| endDate.getTime() == dateOfEvent.getTime() || dateOfEvent.before(currentCalendar.getTime())){
                date.setText("");
                startTime.setText("");
                endTime.setText("");
                Toast.makeText(this,"Event has already passed",Toast.LENGTH_SHORT).show();
            }else {


                if(validLength(nameOfEvent) && validLength(eventDescription) && validLength(location)&& validLength(keywords)){

                    submission.name(nameOfEvent.getText().toString());
                    submission.description(eventDescription.getText().toString());
                    submission.location(location.getText().toString());
                    submission.keywords(keywords.getText().toString());
                    submission.image(encodeImage(bitmap));

                    SubmissionCategory submissionCategory = SubmissionCategory.valueOf(spinnerCategory.getSelectedItem().toString());
                    submission.category(submissionCategory);
                    submission.startOfEvent(startEventCalendar);
                    submission.endOfEvent(endEventCalendar);
                    submission.submitted(currentCalendar);

                    Submission submit = submission.build();
                    new UploadSubmissionTask(this).execute(submit);



                }


            }

        }
    }

    //Setter used to Inject Dependencies so that we can test our app offline.
    public void setCommunicationLayer(DefaultCommunicationLayer layer){
        this.communicationLayer = layer;
    }




    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(bitmap != null) {
            outState.putParcelable("bitmap", bitmap);

        }
        outState.putString("nameOfEvent", nameOfEvent.getText().toString());
        outState.putString("eventDescription", eventDescription.getText().toString());
        outState.putString("location", location.getText().toString());
        outState.putString("date", date.getText().toString());
        outState.putString("startTime", startTime.getText().toString());
        outState.putString("endTime", endTime.getText().toString());
        outState.putString("keywords",keywords.getText().toString());
        // outState.putSerializable("dateOfEvent", dateOfEvent);

        outState.putSerializable("startEventCalendar", startEventCalendar);
        outState.putSerializable("endEventCalendar",endEventCalendar);
        // outState.putString("dateTextView", dateTextView.getText().toString());



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstance){

        super.onRestoreInstanceState(savedInstance);

        bitmap = savedInstance.getParcelable("bitmap");

        nameOfEvent.setText((String) savedInstance.get("nameOfEvent"));
        eventDescription.setText((String) savedInstance.get("eventDescription"));
        location.setText((String) savedInstance.get("location"));
        date.setText((String) savedInstance.get("date"));
        startTime.setText((String) savedInstance.get("startTime"));
        endTime.setText((String) savedInstance.get("endTime"));
        keywords.setText((String) savedInstance.get("keywords"));

        startEventCalendar = (Calendar)savedInstance.getSerializable("startEventCalendar");
        endEventCalendar = (Calendar)savedInstance.getSerializable("endEventCalendar");




        if(bitmap != null ){
            bitmap = Bitmap.createScaledBitmap(bitmap, SCALE_FACTOR, SCALE_FACTOR,false);
            // imageView.setImageBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
        }


    }

    private String encodeImage( Bitmap bitmapImage){
        assert(bitmapImage != null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        return  Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);


    }



    private class UploadSubmissionTask extends AsyncTask<Submission,Void ,ResponseStatus>{

        private Context context;


        public UploadSubmissionTask(Context context){
            this.context = context;
        }

        @Override
        protected ResponseStatus doInBackground(Submission... params) {
            try {
                return communicationLayer.sendAddSubmissionRequest(params[0]);
            } catch (CommunicationLayerException e) {
                return null;

            }
        }

        @Override
        protected void  onPostExecute(ResponseStatus status){

            if(status == ResponseStatus.OK) {
                Intent intent = new Intent(context, MainScreenActivity.class);
                startActivity(intent);
            }
            else if (status == ResponseStatus.COOKIE  ){
                Toast.makeText(context,"Cookie session", Toast.LENGTH_SHORT).show();

            }else {

                Toast.makeText(context,"Problem with server", Toast.LENGTH_SHORT).show();

            }




        }
    }


    private boolean isWhiteSpaces(String s) {
        return ((s.length() == 0) || ( s.matches("\\s+") ));
    }


    private boolean validLength(EditText field){

        assert (field != null);
        String string  = field.getText().toString();

        boolean valid = true;

        if(string.length() >= MIN_CHARACTERS){

            if(string.length() > MAX_CHARACTERS){
                field.setError("Max number of characters" + MAX_CHARACTERS);
                valid = false;

            }

        }else{
            field.setError("Input at least"+ MIN_CHARACTERS);
            valid = false;
        }

        if(!valid){
            field.setText("");
        }
        return valid;
    }




}
