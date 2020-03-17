package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Confirm extends AppCompatActivity {

    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        backBtn = findViewById(R.id.confirmBack);

        // This function redirects to the Appointment activity after pressing the back button.
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Confirm.this, "Admin Home!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Appointment.class));
            }
        });
    }

}
