package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PatientList extends AppCompatActivity implements PatientAdapter.OnItemClickListener {

    // All Declarations
    public static final String EXTRA_NAME = "Name", EXTRA_EMAIL = "Email", EXTRA_PHONE = "Phone",
        EXTRA_SSN = "SSN", EXTRA_DOB = "DOB";
    String name, email, password, phone, SSN, DOB;

    private Button backBtn;
    private RecyclerView pList;
    private ArrayList<PatientModel> patientList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();;
    private CollectionReference patientListRef = fStore.collection("Patients");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        // Initializations
        backBtn = findViewById(R.id.patientListBack);
        pList = findViewById(R.id.patientList);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    // This method redirects to the AdminHome activity after pressing the back button.
    public void goBack() {
        startActivity(new Intent(getApplicationContext(), AdminHome.class));
    }

    private class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView list_name, list_email;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = (TextView) findViewById(R.id.list_name);
            list_email = (TextView) findViewById(R.id.list_email);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        patientListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
            if (e != null) {
                return;
            }

            patientList = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PatientModel patient = documentSnapshot.toObject(PatientModel.class);

                name = patient.getName();
                email = patient.getEmail();
                password = patient.getPassword();
                DOB = patient.getDOB();
                SSN = patient.getSSN();
                phone = patient.getPhone();

                patientList.add(new PatientModel(R.drawable.ic_person, name, email, password, DOB, SSN, phone));
            }

            pList = findViewById(R.id.patientList);
            pList.setHasFixedSize(true);
            adapter = new PatientAdapter(patientList);
            ((PatientAdapter) adapter).setOnItemClickListener(PatientList.this);

            pList.setLayoutManager(layoutManager);
            pList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void itemClick(int pos) {
        Intent dialogIntent = new Intent(this, PatientListDialog.class);
        PatientModel clickedPatient = patientList.get(pos);

        dialogIntent.putExtra(EXTRA_NAME, clickedPatient.getName());
        dialogIntent.putExtra(EXTRA_EMAIL, clickedPatient.getEmail());
        dialogIntent.putExtra(EXTRA_PHONE, clickedPatient.getPhone());
        dialogIntent.putExtra(EXTRA_SSN, clickedPatient.getSSN());
        dialogIntent.putExtra(EXTRA_DOB, clickedPatient.getDOB());

        startActivity(dialogIntent);
    }
}


