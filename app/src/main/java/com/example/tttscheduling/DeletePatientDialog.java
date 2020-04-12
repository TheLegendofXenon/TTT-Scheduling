package com.example.tttscheduling;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeletePatientDialog extends AppCompatDialogFragment {
    private DeleteDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Patient?");
        builder.setMessage("Are you sure you want to delete this patient?");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onYesClicked();
            }
        });

        return builder.create();
    }

    public interface DeleteDialogListener {
        void onYesClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement DeleteDialogListener");
        }
    }
}
