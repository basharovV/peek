package com.peekapps.peek.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.peekapps.peek.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CreateEventActivity extends ActionBarActivity {

    private TextView todayDate;
    private Button timeButton;
    private TextView timeTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        todayDate = (TextView) findViewById(R.id.today_date);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append(cal.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.LONG, Locale.getDefault()) + ", ");
        dateBuilder.append(cal.get(Calendar.DAY_OF_MONTH) + " of ");
        dateBuilder.append(cal.getDisplayName(Calendar.MONTH,
                Calendar.LONG, Locale.getDefault()));
        todayDate.append(dateBuilder.toString());
        /**
         * Time picker -
         * Implements the Material Design time picker library
         */
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog();
                timePicker.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                        timeTextView.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
                    }
                });
                timePicker.show(getFragmentManager(), "timepicker");
                }
        });

        //TOOLBAR BACK BUTTON
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
