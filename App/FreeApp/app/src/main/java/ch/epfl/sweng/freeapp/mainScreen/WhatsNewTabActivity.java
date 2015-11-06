package ch.epfl.sweng.freeapp.mainScreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;

import android.widget.TextView;

import ch.epfl.sweng.freeapp.R;

public class WhatsNewTabActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText("This Is Tab1 Activity");

        setContentView(tv);
    }
}
