package com.example.tttscheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHome extends AppCompatActivity {
    Button calendarBtn, patientListBtn;
    TextView logoutBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Assign objects to layout ids
        calendarBtn = findViewById(R.id.adminCalendarBtn);
        patientListBtn = findViewById(R.id.patientListBtn);
        logoutBtn = findViewById(R.id.logoutAdmin);

        fAuth = FirebaseAuth.getInstance();

        // This function redirects to the activity after pressing the logout button.
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminHome.this, "Admin Calendar!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AdminCalendar.class));
            }
        });

        // This function redirects to the PatientList activity after pressing the "Patient List" button.
        patientListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminHome.this, "Patient List!", Toast.LENGTH_SHORT).show();
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
}
