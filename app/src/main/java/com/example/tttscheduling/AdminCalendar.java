package com.example.tttscheduling;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminCalendar extends AppCompatActivity implements AppointmentAdapter.OnItemClickListener, DeleteApptDialog.DeleteDialogListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private RecyclerView aList;
    private Button back;
    private String date, time, name, email, phone, DOB, address, adminEmail = "", adminAddress = "", holdDateCheck = "", holdTimeCheck = "";
    private int deletePos, editPos, day, month, year, hr, min, dayFinal, monthFinal, yearFinal, hrFinal, minFinal;

    private ArrayList<AppointmentModel> appointmentList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private CollectionReference adminListRef = fStore.collection("Admin");
    private Query apptListRef = fStore.collection("Appointments").orderBy("Time", Query.Direction.ASCENDING);
    private DocumentReference editA, checkA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_calendar);

        back = findViewById(R.id.calendarListBack);
        aList = findViewById(R.id.adminCalendarList);

        Intent retrieveDate = getIntent();
        date = retrieveDate.getStringExtra("Date");
        getSupportActionBar().setTitle("Appointments on " + date);

        // Get the admin's address.
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

        // Create an appointment list
        apptListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                appointmentList = new ArrayList<>();
                for (QueryDocumentSnapshot apptSnapshot : queryDocumentSnapshots) {
                    AppointmentModel appointment = apptSnapshot.toObject(AppointmentModel.class);

                    // Get the right appointments for the current date and location.
                    if (date.equals(appointment.getDate()) && adminAddress.equals(appointment.getAddress())) {
                        name = appointment.getName();
                        email = appointment.getEmail();
                        phone = appointment.getPhone();
                        time = appointment.getTime();
                        DOB = appointment.getDOB();
                        address = appointment.getAddress();
                        appointmentList.add(new AppointmentModel(name, email, phone, date, time, DOB, address));
                    }
                }

                aList.setHasFixedSize(true);
                adapter = new AppointmentAdapter(appointmentList);
                ((AppointmentAdapter) adapter).setOnItemClickListener(AdminCalendar.this);

                aList.setAdapter(adapter);
                aList.setLayoutManager(layoutManager);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(view);
            }
        });
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, AdminHome.class);
        startActivity(intent);
    }

    // This method does nothing.
    @Override
    public void itemClick(int pos) {
    }

    // This method deletes an appointment
    @Override
    public void deleteClick(int pos) {
        deletePos = pos;
        DeleteApptDialog dialog = new DeleteApptDialog();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    // This helper method displays a dialog box for deleting an appointment.
    @Override
    public void onYesClicked() {
        // Find the appointment in Firestore and delete.
        AppointmentModel deleteAppt = appointmentList.get(deletePos);
        final String deleteApptDate = deleteAppt.getDate();
        final String deleteApptTime = deleteAppt.getTime();

        // Delete the patient's appointment
        apptListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryApptSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot deleteApptSnapshot : queryApptSnapshots) {
                    AppointmentModel apptDelete = deleteApptSnapshot.toObject(AppointmentModel.class);
                    String daDateCheck = apptDelete.getDate(); String daTimeCheck = apptDelete.getTime();
                    DocumentReference deleteA = deleteApptSnapshot.getReference();
                    if (deleteApptDate.equals(daDateCheck) && deleteApptTime.equals(daTimeCheck)) {
                        deleteA.delete();
                        Toast.makeText(AdminCalendar.this, "Appointment Successfully Canceled!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });

        appointmentList.remove(deletePos);
        adapter.notifyItemRemoved(deletePos);
    }

    @Override
    public void editClick(int pos) {
        editPos = pos;

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AdminCalendar.this, AdminCalendar.this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AdminCalendar.this, AdminCalendar.this, hr, min, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String amOrPM = "";
        final boolean[] apptCheck = {false};

        // Set the correct times to AM or PM instead of 24-hour.
        hrFinal = hourOfDay;
        if (hrFinal > 12) {
            hrFinal -= 12;
            amOrPM = "PM";
        }
        else if (hrFinal == 12) {
            amOrPM = "PM";
        }
        else if (hrFinal > 0) {
            amOrPM = "AM";
        }
        else {
            hrFinal += 12;
            amOrPM = "AM";
        }

        minFinal = minute;

        final String tempDate = monthFinal + "/" + dayFinal + "/" + yearFinal;
        final String tempTime = amOrPM + " " + hrFinal + ":" + String.format("%02d",minFinal);

        // Find the appointment in Firestore and delete.
        AppointmentModel editAppt = appointmentList.get(editPos);
        final String editApptDate = editAppt.getDate();
        final String editApptTime = editAppt.getTime();

        if (!(minFinal % 10 == 0)) {// Checks to make sure that a 10-min mark is chosen.
            Toast.makeText(AdminCalendar.this, "Please choose a 10-minute mark...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check to make sure the edit appointment doesn't conflict
        apptListRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryApptSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                boolean apptCheck = false;
                for (QueryDocumentSnapshot checkApptSnapshot : queryApptSnapshots) {
                    AppointmentModel apptEdit = checkApptSnapshot.toObject(AppointmentModel.class);
                    String eaDateCheck = apptEdit.getDate(); String eaTimeCheck = apptEdit.getTime();

                    editA = checkApptSnapshot.getReference();

                    // Makes sure rescheduled appointment doesn't conflict
                    if (eaDateCheck.equals(tempDate) && eaTimeCheck.equals(tempTime) && !holdDateCheck.isEmpty() && !holdTimeCheck.isEmpty()) {
                        Toast.makeText(AdminCalendar.this, "The appointment already exists... Try another 10-minute mark.", Toast.LENGTH_SHORT).show();
                        checkA.update("Date", holdDateCheck);
                        checkA.update("Time", holdTimeCheck);
                        break;
                    }
                    else if (eaDateCheck.equals(tempDate) && eaTimeCheck.equals(tempTime)) {
                        Toast.makeText(AdminCalendar.this, "The appointment already exists... Try another 10-minute mark.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    // Reschedules the appointment
                    if (editApptDate.equals(eaDateCheck) && editApptTime.equals(eaTimeCheck)) {
                        checkA = editA;
                        holdDateCheck = eaDateCheck;
                        holdTimeCheck = eaTimeCheck;
                        editA.update("Date", tempDate);
                        editA.update("Time", tempTime);
                        Toast.makeText(AdminCalendar.this, "Appointment Successfully Rescheduled!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        adapter.notifyItemChanged(editPos);
    }

    private class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView list_name, list_email, list_phone, list_DOB, list_time, list_address;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = (TextView) findViewById(R.id.alist_name);
            list_email = (TextView) findViewById(R.id.alist_email);
            list_phone = (TextView) findViewById(R.id.alist_phone);
            list_DOB = (TextView) findViewById(R.id.alist_DOB);
            list_time = (TextView) findViewById(R.id.alist_time);
            list_address = (TextView) findViewById(R.id.alist_address);
        }
    }

}
