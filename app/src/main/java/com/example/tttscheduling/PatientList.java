package com.example.tttscheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PatientList extends AppCompatActivity implements PatientAdapter.OnItemClickListener, DeletePatientDialog.DeleteDialogListener {

    // All Declarations
    private String name, email, password, phone, SSN, DOB, adminEmail = "", adminAddress = "";
    private int deletePos;
    private Button backBtn;
    private RecyclerView pList;
    private ArrayList<PatientModel> patientList;
    private ArrayList<String> apptList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private CollectionReference patientListRef = fStore.collection("Patients"),
            apptListRef = fStore.collection("Appointments"),
            adminListRef = fStore.collection("Admin");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        getSupportActionBar().setTitle("Patient List");

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

    @Override
    protected void onStart() {
        super.onStart();

        adminListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                adminEmail = fAuth.getCurrentUser().getEmail();
                for (QueryDocumentSnapshot adminSnapshot : queryDocumentSnapshots) {
                    AdminModel admin = adminSnapshot.toObject(AdminModel.class);

                    // Get the right admin's address
                    if ((admin.getEmail()).equals(adminEmail)) {
                        adminAddress = admin.getAddress();
                        break;
                    }
                }
            }
        });

        // Find this admin's appointment emails
        apptListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                apptList = new ArrayList<>();
                for (QueryDocumentSnapshot apptSnapshot : queryDocumentSnapshots) {
                    AppointmentModel appointment = apptSnapshot.toObject(AppointmentModel.class);

                    // Get the right appointments for the current date and location.
                    if (adminAddress.equals(appointment.getAddress()) && !apptList.contains(appointment.getEmail()))
                        apptList.add(appointment.getEmail());
                }
            }
        });

        // Find this admin's patients
        patientListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryPatientSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                patientList = new ArrayList<>();
                for (QueryDocumentSnapshot patientSnapshot : queryPatientSnapshots) {
                    PatientModel patient = patientSnapshot.toObject(PatientModel.class);

                    if (apptList.contains(patient.getEmail())) {
                        name = patient.getName();
                        email = patient.getEmail();
                        password = patient.getPassword();
                        DOB = patient.getDOB();
                        SSN = patient.getSSN();
                        phone = patient.getPhone();
                        patientList.add(new PatientModel(name, email, password, DOB, SSN, phone));
                    }
                }

                pList.setHasFixedSize(true);
                adapter = new PatientAdapter(patientList);
                ((PatientAdapter) adapter).setOnItemClickListener(PatientList.this);

                pList.setAdapter(adapter);
                pList.setLayoutManager(layoutManager);
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

            list_name = (TextView) findViewById(R.id.plist_name);
            list_email = (TextView) findViewById(R.id.plist_email);
        }
    }

    // This method goes to the patient dialog class with the current patient.
    @Override
    public void itemClick(int pos) {
        Intent pDialogIntent = new Intent(this, PatientDialog.class);
        PatientModel clickedPatient = patientList.get(pos);

        pDialogIntent.putExtra("Name", clickedPatient.getName());
        pDialogIntent.putExtra("Email", clickedPatient.getEmail());
        pDialogIntent.putExtra("Phone", clickedPatient.getPhone());
        pDialogIntent.putExtra("SSN", clickedPatient.getSSN());
        pDialogIntent.putExtra("DOB", clickedPatient.getDOB());
        pDialogIntent.putExtra("ParentPage", 0);

        startActivity(pDialogIntent);
    }

    // This method deletes the clicked patient.
    @Override
    public void deleteClick(int pos) {
        deletePos = pos;
        DeletePatientDialog dialog = new DeletePatientDialog();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    // This method opens the delete dialog for deleting the patient.
    @Override
    public void onYesClicked() {
        // Find the patient in Firestore and delete.
        PatientModel deletePatient = patientList.get(deletePos);
        final String deleteEmail = deletePatient.getEmail();

        // Delete the patient's appointments.
        apptListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryApptSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot apptSnapshot : queryApptSnapshots) {
                    AppointmentModel apptDelete = apptSnapshot.toObject(AppointmentModel.class);
                    DocumentReference deleteA = apptSnapshot.getReference();
                    if (apptDelete.getEmail().equals(deleteEmail)) {
                        deleteA.delete();
                    }
                }
            }
        });

        // Send a deletion email
        String emailSubject = "Sorry from TT&T Scheduling...";
        String emailMessage = "Hello " + deletePatient.getName() + ",\n\n" +
                "You have been deleted from TT&T Scheduling. All of your future appointments have been cancelled."
                + "\n\nThank you,\n" + "TT&T Scheduling";
        JavaMailAPI sendEmail = new JavaMailAPI(PatientList.this, deletePatient.getEmail(), emailSubject, emailMessage);
        sendEmail.execute();

        DocumentReference deleteP = patientListRef.document(deleteEmail);
        deleteP.delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PatientList.this, "Patient Successfully Deleted!", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PatientList.this, "Failed to delete patient...", Toast.LENGTH_SHORT).show();
                }
            });

        patientList.remove(deletePos);
        adapter.notifyItemRemoved(deletePos);
    }
}


