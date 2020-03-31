package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignupAdmin extends AppCompatActivity {
    EditText aName, aEmail, aPassword, aPhoneNumber;
    Button aSignUpBtn;
    TextView loginBtn, result;
    FirebaseAuth fAuth; // Firebase Authentication object
    FirebaseFirestore fStore; // Firebase Firestore object
    String adminID;
    private static final String TAG = "SignupAdmin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_admin);

        // Assign objects to layout ids
        result = findViewById(R.id.HashPassAdmin);
        aName = findViewById(R.id.signUpAdminName);
        aEmail = findViewById(R.id.signUpAdminEmail);
        aPassword = findViewById(R.id.signUpAdminPassword);
        aPhoneNumber = findViewById(R.id.signUpAdminPhone);
        aSignUpBtn = findViewById(R.id.signUpAdminBtn);
        loginBtn = findViewById(R.id.signUpAdminLogin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        aSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = aName.getText().toString().trim();
                final String email = aEmail.getText().toString().trim();
                final String password = aPassword.getText().toString().trim();
                final String phoneNumber = aPhoneNumber.getText().toString().trim();
                final String aHashPass = result.getText().toString().trim();

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
                            admin.put("Name", name);
                            admin.put("Email", email);
                            admin.put("Password", aHashPass);
                            admin.put("Phone", phoneNumber);
                            dReference.set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
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
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

    public void computeMD5Hash(String password) {
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
