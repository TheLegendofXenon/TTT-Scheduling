package com.example.tttscheduling;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Appointment extends AppCompatActivity {

    private CalendarView calendar;
    private String date, clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        calendar = (CalendarView) findViewById(R.id.appointmentCalendar);

        MyTimePicker po = new MyTimePicker(this);

        TimePicker time = findViewById(R.id.appointTimePicker);

        po.setTimePickerInterval(time);

        Button send = (Button) this.findViewById(R.id.appointNext);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int i, int i1, int i2) {
                date = (i1 + 1) + "/" + i2 + "/" + i;
                Log.d(TAG, "onSelectedDayChange: mm/dd/yyyy " + date);

            }
        });

        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                clock = hourOfDay + ":" + String.format("%02d", minute * 30);
                Log.d(TAG, "onTimeChanged: hh:mm " + clock);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }

        });

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, Confirm.class);
        intent.putExtra("date", date);
        intent.putExtra("time", clock);
        startActivity(intent);
    }

    // This method redirects to the AdminHome activity after pressing the back button.
    public void goBack() {
        startActivity(new Intent(getApplicationContext(), AdminHome.class));
    }
}

class MyTimePicker extends TimePicker {

    private static final int TIME_PICKER_INTERVAL = 30;

    public MyTimePicker(Context context) {
        super(context);
    }

    void setTimePickerInterval(TimePicker timePicker) {
        try {

            NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
    }
}
