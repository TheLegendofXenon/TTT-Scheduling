package com.example.tttscheduling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private ArrayList<PatientModel> patientList;
    private OnItemClickListener pListener;

    public interface OnItemClickListener {
        void itemClick(int pos);
        void deleteClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listen) {
        pListener = listen;
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {
        public ImageView personView, deleteView;
        public TextView nameText, emailText;

        public PatientViewHolder(View itemView) {
            super(itemView);
            personView = itemView.findViewById(R.id.pListPersonIcon);
            deleteView = itemView.findViewById(R.id.pListDeleteIcon);
            nameText = itemView.findViewById(R.id.plist_name);
            emailText = itemView.findViewById(R.id.plist_email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION)
                            pListener.itemClick(pos);
                    }
                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION)
                            pListener.deleteClick(pos);
                    }
                }
            });
        }
    }

    public PatientAdapter(ArrayList<PatientModel> getList) {
        patientList = getList;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patient_item, parent, false);
        PatientViewHolder evh = new PatientViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        PatientModel currPatient = patientList.get(position);

        holder.personView.setImageResource(R.drawable.ic_person);
        holder.deleteView.setImageResource(R.drawable.ic_delete);
        holder.nameText.setText(currPatient.getName());
        holder.emailText.setText("Email: " + currPatient.getEmail());
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }
}
