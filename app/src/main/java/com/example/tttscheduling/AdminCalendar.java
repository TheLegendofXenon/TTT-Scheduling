package com.example.tttscheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_calendar);

        Button send = (Button) this.findViewById(R.id.calendarBack);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }

        });
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }
}
