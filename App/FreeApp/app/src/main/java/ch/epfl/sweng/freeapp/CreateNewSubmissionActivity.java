package ch.epfl.sweng.freeapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class CreateNewSubmissionActivity extends AppCompatActivity {

    private Calendar currentCalendar = Calendar.getInstance();
    private int startYear  = currentCalendar.get(Calendar.YEAR);
    private int startMonth = currentCalendar.get(Calendar.MONTH);
    private int startDay   = currentCalendar.get(Calendar.DAY_OF_MONTH);
    private int DATE_DIALOG_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_submission2);

        TextView categoriesText = (TextView)findViewById(R.id.categoriesText);
        categoriesText.setTextSize(19);



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

                TextView dateTextView = (TextView)findViewById(R.id.date);
                dateTextView.setText(dayOfMonth +"-"+monthOfYear + "-"+ year );
            }
        };


        DatePickerDialog dialogPicker = new DatePickerDialog(this, datePickerListener,this.startYear,this.startMonth,this.startDay);
        dialogPicker.show();




    }




}
