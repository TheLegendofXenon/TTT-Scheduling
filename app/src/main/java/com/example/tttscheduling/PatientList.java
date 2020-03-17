package com.example.tttscheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PatientList extends AppCompatActivity {

    Button backBtn;
    ListView pList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        pList = (ListView)findViewById(R.id.patientList);
        backBtn = findViewById(R.id.patientListBack);

        ArrayList<String> patients = new ArrayList<>();

        patients.add("Patient #1: Cyrus Albright");
        patients.add("Patient #2: Futaba Sakura");
        patients.add("Patient #3: Andrew Ryan");
        patients.add("Patient #4: Selvaria Bles");
        patients.add("Patient #5: Tanjiro Kamado");
        patients.add("Patient #6: Makise Kurisu");
        patients.add("Patient #7: Nathan Drake");
        patients.add("Patient #8: Toph Beifong");
        patients.add("Patient #9: Dan Salvato");
        patients.add("Patient #10: Weiss Schnee");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, patients);

        pList.setAdapter(arrayAdapter);

        // This function redirects to the AdminHome activity after pressing the back button.
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PatientList.this, "Admin Home!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AdminHome.class));
            }
        });
    }

}
