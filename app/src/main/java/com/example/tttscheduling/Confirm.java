package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Confirm extends AppCompatActivity {

    Button backBtn;
    TextView result_info;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        result_info = findViewById(R.id.confirmText);
        backBtn = findViewById(R.id.confirmBack);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getEmail();

        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Intent intent = getIntent();
                String date = intent.getStringExtra("date");
                String time = intent.getStringExtra("time");

                String text = "Name: " + documentSnapshot.getString("Name") + '\n' +
                        "DOB: " + documentSnapshot.getString("DOB") + '\n' +
                        "Email: " + documentSnapshot.getString("Email") + '\n' +
                        "Phone number: " + documentSnapshot.getString("Phone") + '\n';
                result_info.setText(text + "Date: " + date + '\n' + "Time: " + time);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    // This method redirects to the AdminHome activity after pressing the back button.
    public void goBack() {
        startActivity(new Intent(getApplicationContext(), Appointment.class));
    }
}
