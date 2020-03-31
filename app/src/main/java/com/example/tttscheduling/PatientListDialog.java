package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.tttscheduling.PatientList.EXTRA_DOB;
import static com.example.tttscheduling.PatientList.EXTRA_EMAIL;
import static com.example.tttscheduling.PatientList.EXTRA_NAME;
import static com.example.tttscheduling.PatientList.EXTRA_PHONE;
import static com.example.tttscheduling.PatientList.EXTRA_SSN;

public class PatientListDialog extends AppCompatActivity {
    // Declarations
    Button backBtn;
    String name, email, phone, SSN, DOB;
    TextView nameText, emailText, phoneText, SSNText, DOBText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list_dialog);

        // Initializations
        backBtn = findViewById(R.id.patientListDialogBack);
        nameText = findViewById(R.id.pListDialogName);
        emailText = findViewById(R.id.pListDialogEmail);
        phoneText = findViewById(R.id.pListDialogPhone);
        SSNText = findViewById(R.id.pListDialogSSN);
        DOBText = findViewById(R.id.pListDialogDOB);

        Intent retrieve = getIntent();
        name = retrieve.getStringExtra(EXTRA_NAME);
        email = retrieve.getStringExtra(EXTRA_EMAIL);
        phone = retrieve.getStringExtra(EXTRA_PHONE);
        SSN = retrieve.getStringExtra(EXTRA_SSN);
        DOB = retrieve.getStringExtra(EXTRA_DOB);

        nameText.setText(name);
        emailText.setText("Email: " + email);
        phoneText.setText("Phone Number: " + phone);
        SSNText.setText("Last 4 Digits of SSN: " + SSN);
        DOBText.setText("Date of Birth: " + DOB);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    // This method redirects to the PatientList activity after pressing the back button.
    public void goBack() {
        startActivity(new Intent(getApplicationContext(), PatientList.class));
    }
}
