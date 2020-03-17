package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PatientHome extends AppCompatActivity {
    Button locateButton, setUpAppointBtn;
    TextView logoutBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        // Assign objects to layout ids
        locateButton = findViewById(R.id.locateBtn);
        setUpAppointBtn = findViewById(R.id.setUpAppointmentBtn);
        logoutBtn = findViewById(R.id.logoutPatient);

        fAuth = FirebaseAuth.getInstance();

        // This function redirects to the activity after pressing the logout button.
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PatientHome.this, "Locate Hospital!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Locate.class));
            }
        });

        // This function redirects to the PatientList activity after pressing the "Patient List" button.
        setUpAppointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PatientHome.this, "Set up an Appointment!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Appointment.class));
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


}
