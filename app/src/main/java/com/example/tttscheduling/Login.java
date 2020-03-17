package com.example.tttscheduling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText tEmail, tPassword;
    Button loginButton;
    TextView adminBtn, patientBtn;
    FirebaseAuth fAuth; // Firebase Authentication object
    FirebaseFirestore fStore; // Firebase Firestore object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assign objects to layout ids
        tEmail = findViewById(R.id.loginEmail);
        tPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginBtn);
        adminBtn = findViewById(R.id.signUpAsAdmin);
        patientBtn = findViewById(R.id.signUpAsPatient);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fAuth.signOut(); // Sign out to make sure no account is currently logged in.

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = tEmail.getText().toString().trim();
                final String password = tPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    tEmail.setError("Email is Required...");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    tPassword.setError("Password is Required...");
                    return;
                }

                if (password.length() < 6) {
                    tPassword.setError("Password must be >= 6 characters...");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Checks to make sure the login is an Admin or a Patient
                            if (email.indexOf("@admin.com") > 0) {
                                Toast.makeText(Login.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), AdminHome.class));
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), PatientHome.class));
                                finish();
                            }
                        }
                    }
                });
            }
        });

        // This function redirects to the SignupAdmin activity after pressing the "Sign up as Admin" button.
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Sign up as Admin!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SignupAdmin.class));
            }
        });

        // This function redirects to the SignupPatient activity after pressing the "Sign up as Patient" button.
        patientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Sign up as Patient!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SignupPatient.class));
            }
        });
    }
}
