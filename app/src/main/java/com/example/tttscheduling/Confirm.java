package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Confirm extends AppCompatActivity {

    Button backBtn, confirmationBtn;
    TextView result_info;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, name, email, DOB, phone, date, time, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        getSupportActionBar().setTitle("Confirm Appointment");

        result_info = findViewById(R.id.confirmText);
        backBtn = findViewById(R.id.confirmBack);
        confirmationBtn = findViewById(R.id.confirmBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getEmail();

        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Intent confirmRetrieve = getIntent();
                name = documentSnapshot.getString("Name");
                email = documentSnapshot.getString("Email");
                phone = documentSnapshot.getString("Phone");
                DOB = documentSnapshot.getString("DOB");
                date = confirmRetrieve.getStringExtra("date");
                time = confirmRetrieve.getStringExtra("time");
                address = confirmRetrieve.getStringExtra("address");

                String tempAMorPM = time.substring(0, 2);
                String tempTime = time.substring(3);
                String confirmInfo = "Name: " + name + '\n' +
                        "DOB: " + DOB + '\n' +
                        "Email: " + email + '\n' +
                        "Phone number: " + phone + '\n' +
                        "Date: " + date + '\n' +
                        "Time: " + tempTime + ' ' + tempAMorPM + '\n' +
                        "Address: " + address;
                result_info.setText(confirmInfo);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        confirmationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAppt();
            }
        });
    }

    // This method redirects to the AdminHome activity after pressing the back button.
    public void goBack() {
        Intent backToPHome = new Intent(this, PatientHome.class);
        backToPHome.putExtra("address", address);
        startActivity(backToPHome);
    }

    // This method creates an Appointment document in Firebase Firestore.
    public void createAppt() {
        // Initialize necessities
        DocumentReference dRef = fStore.collection("Appointments").document();
        Map<String, Object> appointment = new HashMap<>();

        appointment.put("Name", name);
        appointment.put("Email", email);
        appointment.put("Phone", phone);
        appointment.put("DOB", DOB);
        appointment.put("Date", date);
        appointment.put("Time", time);
        appointment.put("Address", address);

        dRef.set(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Confirm.this, "Appointment Successfully Created!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Confirm.this, "Failed to create appointment. Please try again later...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Send a confirmation email
        String tempAMorPM = time.substring(0, 2);
        String tempTime = time.substring(3);
        String emailSubject = "Appointment set for " + date + " at " + address;
        String emailMessage = "Hello " + name + ",\n\n" +
                "You have an appointment scheduled for " + date + " at " + tempTime + ' ' + tempAMorPM + " at " + address + '.'
                + "\n\nThank you,\n" + "TT&T Scheduling";
        JavaMailAPI sendEmail = new JavaMailAPI(Confirm.this, email, emailSubject, emailMessage);
        sendEmail.execute();

        startActivity(new Intent(getApplicationContext(), PatientHome.class));
    }
}
