package com.example.myread;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class RenameCollectionDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_renamecollection, null))
                .setPositiveButton(R.string.renamebutton,
                        (dialog, which) -> Objects.requireNonNull(getTargetFragment())
                        .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, requireActivity().getIntent()))
                .setNegativeButton(R.string.cancel_collection_btn, (dialog, which) ->
                        Objects.requireNonNull(RenameCollectionDialog.this.getDialog()).cancel());
        return builder.create();
    }




}
