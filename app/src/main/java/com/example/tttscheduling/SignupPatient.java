package com.example.tttscheduling;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupPatient extends AppCompatActivity {

    EditText pName, pEmail, pPassword, pPhoneNumber, pSSN;
    TextView pDOB;
    Button pSignUpBtn;
    TextView loginBtn, result;
    DatabaseReference fbRef; // Database Reference object
    FirebaseAuth fAuth; // Firebase Authentication object
    FirebaseFirestore fStore; // Firebase Firestore object
    private static final String TAG = "SignupPatient";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_patient);
        getSupportActionBar().setTitle("Patient Sign Up");

        // Assign objects to layout ids
        result = findViewById(R.id.HashPass);
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

        pDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SignupPatient.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                pDOB.setText(date);
            }
        };

        pSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeMD5Hash(pPassword.toString());
                final String name = pName.getText().toString().trim();
                final String email = pEmail.getText().toString().trim();
                final String password = pPassword.getText().toString().trim();
                final String phoneNumber = pPhoneNumber.getText().toString().trim();
                final String DOB = pDOB.getText().toString().trim();
                final String SSN = pSSN.getText().toString().trim();

                final String HashPass = result.getText().toString().trim();

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

                if(phoneNumber.length() != 10) {
                    pPhoneNumber.setError("Phone number must be 10...");
                    return;
                }

                if(DOB.equals("Date of Birth")) {
                    Toast.makeText(SignupPatient.this, "Please select a date of birth...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SSN.length() != 4) {
                    pSSN.setError("This isn't 4 digits...");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            DocumentReference dRef = fStore.collection("Patients").document(email);
                            Map<String, Object> patient = new HashMap<>();
                            patient.put("Name", name);
                            patient.put("Email", email);
                            patient.put("Password", HashPass);
                            patient.put("Phone", phoneNumber);
                            patient.put("DOB", DOB);
                            patient.put("SSN", SSN);
                            dRef.set(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignupPatient.this, "Patient Successfully Created!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupPatient.this, "Failed to create patient. Please try again later...",
                                            Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    public void computeMD5Hash(String password)  {
        try {
            //Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }
            result.setText(MD5Hash);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
