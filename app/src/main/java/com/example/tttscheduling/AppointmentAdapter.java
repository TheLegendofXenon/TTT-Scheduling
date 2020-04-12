package com.example.tttscheduling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private ArrayList<AppointmentModel> appointmentList;
    private OnItemClickListener aListener;

    public interface OnItemClickListener {
        void itemClick(int pos);
        void deleteClick(int pos);
        void editClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listen) {
        aListener = listen;
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        public ImageView clipView, editView, deleteView;
        public TextView nameText, emailText, phoneText, DOBText, timeText, addressText;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            clipView = itemView.findViewById(R.id.aListClipboardIcon);
            editView = itemView.findViewById(R.id.aListEditIcon);
            deleteView = itemView.findViewById(R.id.aListDeleteIcon);
            nameText = itemView.findViewById(R.id.alist_name);
            emailText = itemView.findViewById(R.id.alist_email);
            phoneText = itemView.findViewById(R.id.alist_phone);
            DOBText = itemView.findViewById(R.id.alist_DOB);
            timeText = itemView.findViewById(R.id.alist_time);
            addressText = itemView.findViewById(R.id.alist_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION)
                            aListener.itemClick(pos);
                    }
                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION)
                            aListener.deleteClick(pos);
                    }
                }
            });

            editView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (aListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION)
                            aListener.editClick(pos);
                    }
                }
            });
        }
    }

    public AppointmentAdapter(ArrayList<AppointmentModel> getList) {
        appointmentList = getList;
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment_item, parent, false);
        AppointmentViewHolder avh = new AppointmentViewHolder(v);
        return avh;
    }

    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {
        AppointmentModel currAppointment = appointmentList.get(position);

        holder.clipView.setImageResource(R.drawable.ic_clipboard);
        holder.editView.setImageResource(R.drawable.ic_edit);
        holder.deleteView.setImageResource(R.drawable.ic_delete);

        holder.nameText.setText(currAppointment.getName());
        holder.emailText.setText("Email: " + currAppointment.getEmail());
        holder.phoneText.setText("Phone #: " + currAppointment.getPhone());
        holder.DOBText.setText("DOB: " + currAppointment.getDOB());
        holder.timeText.setText(currAppointment.getDate() + " - " + currAppointment.getTime());
        holder.addressText.setText("Address: " + currAppointment.getAddress());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
