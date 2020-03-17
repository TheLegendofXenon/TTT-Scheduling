package com.example.tttscheduling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupPatient extends AppCompatActivity {

    EditText pName, pEmail, pPassword, pPhoneNumber, pDOB, pSSN;
    Button pSignUpBtn;
    TextView loginBtn;
    DatabaseReference fbRef; // Database Reference object
    FirebaseAuth fAuth; // Firebase Authentication object
    //PatientFirebase patient; // PatientFirebase object
    FirebaseFirestore fStore; // Firebase Firestore object
    String patientID;
    private static final String TAG = "SignupPatient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_patient);

        // Assign objects to layout ids
        pName = findViewById(R.id.signUpPatientName);
        pEmail = findViewById(R.id.signUpPatientEmail);
        pPassword = findViewById(R.id.signUpPatientPassword);
        pPhoneNumber = findViewById(R.id.signUpPatientPhone);
        pDOB = findViewById(R.id.signUpPatientDOB);
        pSSN = findViewById(R.id.signUpPatientSSN);
        pSignUpBtn = findViewById(R.id.signUpPatientBtn);
        loginBtn = findViewById(R.id.signUpPatientLogin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        /*if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), PatientHome.class));
            finish();
        }*/

        /*
        fbRef = FirebaseDatabase.getInstance().getReference().child("Patient");
        patient = new PatientFirebase();
        fbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    patientID = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */

        pSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = pName.getText().toString().trim();
                final String email = pEmail.getText().toString().trim();
                final String password = pPassword.getText().toString().trim();
                final String phoneNumber = pPhoneNumber.getText().toString().trim();
                final String DOB = pDOB.getText().toString().trim();
                final String SSN = pSSN.getText().toString().trim();

                /*
                patient.setName(name);
                patient.setEmail(email);
                patient.setPassword(password);
                patient.setPhoneNumber(phoneNumber);
                patient.setDOB(DOB);
                patient.setSSN(SSN);

                fbRef.child("Patient " + String.valueOf(patientID + 1) + ": " + patient.getName()).setValue(patient);
                Toast.makeText(SignupPatient.this, "New Patient Created!", Toast.LENGTH_LONG).show(); */

                if(TextUtils.isEmpty(email)) {
                    pEmail.setError("Email is Required...");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    pPassword.setError("Password is Required...");
                    return;
                }

                if(password.length() < 6) {
                    pPassword.setError("Password must be >= 6 characters...");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignupPatient.this, "Patient Created!", Toast.LENGTH_SHORT).show();
                            patientID = fAuth.getCurrentUser().getUid();
                            DocumentReference dRef = fStore.collection("Patients").document(email);
                            Map<String, Object> patient = new HashMap<>();
                            patient.put("Full Name", name);
                            patient.put("Email", email);
                            patient.put("Password", password);
                            patient.put("Phone", phoneNumber);
                            patient.put("DOB", DOB);
                            patient.put("SSN", SSN);
                            dRef.set(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Log.d(TAG, "Admin Created!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            startActivity(new Intent(getApplicationContext(), PatientHome.class));
                        }
                        else {
                            Toast.makeText(SignupPatient.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // This function redirects to the Login activity after pressing the login button.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupPatient.this, "Login Instead.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}
