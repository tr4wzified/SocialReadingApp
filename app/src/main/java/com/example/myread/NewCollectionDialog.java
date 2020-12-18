package com.example.myread;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NewCollectionDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_newcollection, null))
                .setPositiveButton(R.string.add_collection_btn, (dialog, which) -> {
                    listener.onDialogPositiveClick(NewCollectionDialog.this);
                })
                .setNegativeButton(R.string.cancel_collection_btn, (dialog, which) -> NewCollectionDialog.this.getDialog().cancel());
        return builder.create();
    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
//        public void onDialogNegativeClick(DialogFragment dialog);
    }
    NewCollectionDialog.NoticeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (NewCollectionDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + "must implement NoticeDialogListener");
        }
    }
}
