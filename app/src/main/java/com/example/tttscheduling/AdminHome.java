package com.example.tttscheduling;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Calendar;

public class AdminHome extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button calendarBtn, patientListBtn;
    TextView logoutBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getSupportActionBar().setTitle("Admin Home");

        // Assign objects to layout ids
        calendarBtn = findViewById(R.id.adminCalendarBtn);
        patientListBtn = findViewById(R.id.patientListBtn);
        logoutBtn = findViewById(R.id.logoutAdmin);

        fAuth = FirebaseAuth.getInstance();

        // This function redirects to the activity after pressing the calendar button.
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // This function redirects to the PatientList activity after pressing the "Patient List" button.
        patientListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PatientList.class));
                finish();
            }
        });

        // This function redirects to the Login activity after pressing the logout button.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminHome.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String currDateString, tempDateString;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        currDateString = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(c.getTime());
        tempDateString = currDateString.substring(currDateString.length() - 2);
        currDateString = currDateString.substring(0, currDateString.length() - 2) + "20" + tempDateString;

        Intent adminCalendar = new Intent(getApplicationContext(), AdminCalendar.class);
        adminCalendar.putExtra("Date", currDateString);
        startActivity(adminCalendar);
    }
}
