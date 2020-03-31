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
        void itemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listen) {
        pListener = listen;
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconView;
        public TextView nameText, emailText;

        public PatientViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.pListPersonIcon);
            nameText = itemView.findViewById(R.id.list_name);
            emailText = itemView.findViewById(R.id.list_email);

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
        }
    }

    public PatientAdapter(ArrayList<PatientModel> getList) {
        patientList = getList;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        PatientViewHolder evh = new PatientViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        PatientModel currPatient = patientList.get(position);

        holder.iconView.setImageResource(currPatient.getIcon());
        holder.nameText.setText(currPatient.getName());
        holder.emailText.setText(currPatient.getEmail());
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }
}
