package ch.epfl.sweng.freeapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import java.util.Calendar;

public class CreateNewSubmissionActivity extends AppCompatActivity {


    private Calendar currentCalendar = Calendar.getInstance();
    private int startYear  = currentCalendar.get(Calendar.YEAR);
    private int startMonth = currentCalendar.get(Calendar.MONTH);
    private int startDay   = currentCalendar.get(Calendar.DAY_OF_MONTH);
   /// private int DATE_DIALOG_ID = 0;

    private final static int PICTURE_REQUEST = 200;
    //private Uri imageUri;
    private Bitmap bitmap;

    private EditText nameOfEvent ;
    private EditText eventDescription ;
    private EditText location;
    private ImageView imageView;

    private TextView  date ;
    private TextView startTime ;
    private TextView endTime ;
    private EditText keywords ;

    private int textSize = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_submission2);

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


        Spinner spinner = (Spinner)findViewById(R.id.categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories,android.R.layout.simple_spinner_dropdown_item);
        //layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply adapter to the spinner
        spinner.setAdapter(adapter);

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

                date.setText(dayOfMonth +"-"+monthOfYear + "-"+ year );
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

                if(startOrEnd.getText().toString().equals(startTimeButton.getText().toString())){


                    startTime.setText(hourOfDay +":"+minute);

                }else{


                    endTime.setText(hourOfDay+":"+minute);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,onTimeSetListener,hours,minutes,true);
        timePickerDialog.show();
    }



    public  void onClickTakeImage(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICTURE_REQUEST);

    }


    //What the camera will output
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);



        new BitmapTask(resultCode,intent).execute(requestCode);

    }


    public void onClickExistingPicture(View view){


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


        if(TextUtils.isEmpty(nameOfEvent.getText().toString())){
            nameOfEvent.setError("Please fill in name");

        }else if(TextUtils.isEmpty(eventDescription.getText().toString())){
            eventDescription.setError("Please fill in description");
        }else if(TextUtils.isEmpty(location.getText().toString())){

            location.setError("Please input  location");

        }else if(TextUtils.isEmpty(date.getText().toString())){

            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(startTime.getText().toString()) ){

            Toast.makeText(this,"Select Start Time", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(endTime.getText().toString())){

            Toast.makeText(this,"Select End Time ",Toast.LENGTH_SHORT).show();

        }else if(imageView.getDrawable() == null ){

            Toast.makeText(this,"Please insert a picture", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(keywords.getText().toString())){
            keywords.setError("Put some keywords");
        }else{

            //access submit to server,

        }







    }




    private class BitmapTask extends AsyncTask<Integer,Void,Bitmap>{


         private int requestCode;
         private int resultCode;
         private Intent intent;


         public BitmapTask(int resultCode,Intent intent){


             this.resultCode = resultCode;
             this.intent = intent;
         }
         @Override
         protected Bitmap doInBackground(Integer... params) {
             this.requestCode = params[0];
             if(resultCode == RESULT_OK){
                 if(requestCode == PICTURE_REQUEST){
                      bitmap =  (Bitmap)intent.getExtras().get("data");


                     return  bitmap;
                 }
             }
             return null;
         }

        @Override
        protected  void onPostExecute(Bitmap bitmap){
            if(bitmap != null ){
                imageView.setImageBitmap(bitmap);
            }

        }
     }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable("bitmap", bitmap);
        outState.putString("nameOfEvent", nameOfEvent.getText().toString());
        outState.putString("eventDescription", eventDescription.getText().toString());
        outState.putString("location", location.getText().toString());
        outState.putString("date", date.getText().toString());
        outState.putString("startTime", startTime.getText().toString());
        outState.putString("endTime", endTime.getText().toString());
        outState.putString("keywords",keywords.getText().toString());
       // outState.putString("dateTextView", dateTextView.getText().toString());



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstance){

        super.onRestoreInstanceState(savedInstance);

            bitmap = savedInstance.getParcelable("bitmap");
            nameOfEvent.setText((String)savedInstance.get("nameOfEvent"));
            eventDescription.setText((String) savedInstance.get("eventDescription"));
            location.setText((String)savedInstance.get("location"));
            date.setText((String) savedInstance.get("date"));
            startTime.setText((String) savedInstance.get("startTime"));
            endTime.setText((String) savedInstance.get("endTime"));
            keywords.setText((String) savedInstance.get("keywords"));


        if(bitmap != null ){
            imageView.setImageBitmap(bitmap);
        }









    }











}
