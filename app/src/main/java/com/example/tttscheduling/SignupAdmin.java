package com.example.tttscheduling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupAdmin extends AppCompatActivity {
    EditText aName, aEmail, aPassword, aPhoneNumber;
    Button aSignUpBtn;
    TextView loginBtn;
    DatabaseReference fbRef; // Database Reference object
    FirebaseAuth fAuth; // Firebase Authentication object
    FirebaseFirestore fStore; // Firebase Firestore object
    //AdminFirebase admin; // AdminFirebase object
    String adminID;
    private static final String TAG = "SignupAdmin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_admin);

        // Assign objects to layout ids
        aName = findViewById(R.id.signUpAdminName);
        aEmail = findViewById(R.id.signUpAdminEmail);
        aPassword = findViewById(R.id.signUpAdminPassword);
        aPhoneNumber = findViewById(R.id.signUpAdminPhone);
        aSignUpBtn = findViewById(R.id.signUpAdminBtn);
        loginBtn = findViewById(R.id.signUpAdminLogin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        /*if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), AdminHome.class));
            finish();
        }*/

        /*fbRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        admin = new AdminFirebase();
        fbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    adminID = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */

        aSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = aName.getText().toString().trim();
                final String email = aEmail.getText().toString().trim();
                final String password = aPassword.getText().toString().trim();
                final String phoneNumber = aPhoneNumber.getText().toString().trim();

                /*
                admin.setName(name);
                admin.setEmail(email);
                admin.setPassword(password);
                admin.setPhoneNumber(phoneNumber);

                fbRef.child("Admin " + String.valueOf(adminID + 1) + ": " + admin.getName()).setValue(admin);
                Toast.makeText(SignupAdmin.this, "New Admin Created!", Toast.LENGTH_LONG).show(); */

                if(TextUtils.isEmpty(email)) {
                    aEmail.setError("Email is Required...");
                    return;
                }

                if (email.indexOf("@admin.com") < 0) {
                    aEmail.setError("Admin email is Required... (@admin.com)");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    aPassword.setError("Password is Required...");
                    return;
                }

                if(password.length() < 6) {
                    aPassword.setError("Password must be >= 6 characters...");
                    return;
                }

                // Creates an Admin Document and sends it to the Admin Collection.
                // Go to the Admin Home Menu as well.
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SignupAdmin.this, "Admin Created!", Toast.LENGTH_SHORT).show();
                            adminID = fAuth.getCurrentUser().getUid();
                            DocumentReference dReference = fStore.collection("Admin").document(email);
                            Map<String, Object> admin = new HashMap<>();
                            admin.put("Full Name", name);
                            admin.put("Email", email);
                            admin.put("Password", password);
                            admin.put("Phone", phoneNumber);
                            dReference.set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Log.d(TAG, "Admin Created!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                            startActivity(new Intent(getApplicationContext(), AdminHome.class));
                        }
                        else {
                            Toast.makeText(SignupAdmin.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // This function redirects to the Login activity after pressing the login button.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupAdmin.this, "Login Instead.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}
