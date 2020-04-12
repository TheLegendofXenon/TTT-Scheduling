package com.example.tttscheduling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class PatientHome extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Button locateButton, setUpAppointBtn, futureApptsBtn;
    private TextView logoutBtn;
    private String email, address = "";
    private int day, month, year, hr, min, dayFinal, monthFinal, yearFinal, hrFinal, minFinal;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference patientListRef = fStore.collection("Patients"),
            apptListRef = fStore.collection("Appointments");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
        getSupportActionBar().setTitle("Patient Home");

        Intent locationIntent = getIntent();
        address = locationIntent.getStringExtra("address");

        // Assign objects to layout ids
        locateButton = findViewById(R.id.locateBtn);
        setUpAppointBtn = findViewById(R.id.setUpAppointmentBtn);
        futureApptsBtn = findViewById(R.id.apptsBtn);
        logoutBtn = findViewById(R.id.logoutPatient);

        email = fAuth.getCurrentUser().getEmail();

        // This function redirects to the activity after pressing the logout button.
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locateIntent = new Intent(getApplicationContext(), Locate.class);
                locateIntent.putExtra("ParentPage", 0);
                startActivity(locateIntent);
            }
        });

        // This function redirects to the PatientList activity after pressing the "Patient List" button.
        setUpAppointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address == null || address.isEmpty())
                    Toast.makeText(PatientHome.this, "Please select a location first...", Toast.LENGTH_SHORT).show();
                else {
                    Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(PatientHome.this, PatientHome.this, year, month, day);
                    datePickerDialog.show();
                }
            }
        });

        // This function redirects to the PatientDialog activity after pressing the "Future Appointments" button.
        futureApptsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAppointments();
            }
        });

        // This function redirects to the Login activity after pressing the logout button.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Toast.makeText(PatientHome.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
            }
        });
    }

    // This function goes the the future appointments under this patient.
    private void goToAppointments() {

        patientListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    PatientModel patient = documentSnapshot.toObject(PatientModel.class);

                    // Get the right appointments for the current patient.
                    if (email.equals(patient.getEmail())) {
                        Intent apptIntent = new Intent(getApplicationContext(), PatientDialog.class);

                        apptIntent.putExtra("Name", patient.getName());
                        apptIntent.putExtra("Email", patient.getEmail());
                        apptIntent.putExtra("Phone", patient.getPhone());
                        apptIntent.putExtra("SSN", patient.getSSN());
                        apptIntent.putExtra("DOB", patient.getDOB());
                        apptIntent.putExtra("ParentPage", 1);

                        startActivity(apptIntent);
                    }
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(PatientHome.this, PatientHome.this, hr, min, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String amOrPM = "";
        final Intent newAppt = new Intent(this, Confirm.class);

        // Set the correct times to AM or PM instead of 24-hour.
        hrFinal = hourOfDay;
        if (hrFinal > 12) {
            hrFinal -= 12;
            amOrPM = "PM";
        }
        else if (hrFinal == 12) {
            amOrPM = "PM";
        }
        else if (hrFinal > 0) {
            amOrPM = "AM";
        }
        else {
            hrFinal += 12;
            amOrPM = "AM";
        }

        minFinal = minute;

        final String date = monthFinal + "/" + dayFinal + "/" + yearFinal;
        final String time =  amOrPM + " " + hrFinal + ":" + String.format("%02d",minFinal);

        if (!(minFinal % 10 == 0)) {// Checks to make sure that a 10-min mark is chosen.
            Toast.makeText(PatientHome.this, "Please choose a 10-minute mark...", Toast.LENGTH_SHORT).show();
            return;
        }
        apptListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null)
                    return;

                boolean apptExists = false;
                for (QueryDocumentSnapshot apptSnapshot : queryDocumentSnapshots) {
                    AppointmentModel appointment = apptSnapshot.toObject(AppointmentModel.class);

                    // See if there is already an appointment.
                    if (time.equals(appointment.getTime()) && date.equals(appointment.getDate()) && address.equals(appointment.getAddress())) {
                        Toast.makeText(PatientHome.this, "The appointment already exists... Try another 10-minute mark.", Toast.LENGTH_SHORT).show();
                        apptExists = true;
                    }
                }

                if (apptExists)
                    return;

                newAppt.putExtra("date", date);
                newAppt.putExtra("time", time);
                newAppt.putExtra("address", address);
                startActivity(newAppt);
            }
        });

    }

}
