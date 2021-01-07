package com.example.myread;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.CollectionListAdapter;

public class AddCollectionDialog extends Dialog {

    public AddCollectionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public AddCollectionDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public Activity activity;
    public RecyclerView mRecyclerView;
    protected CollectionListAdapter mAdapter;

    public AddCollectionDialog(Activity activity, CollectionListAdapter adapter) {
        super(activity);
        this.activity = activity;
        this.mAdapter = adapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_addcollection);
        mRecyclerView = findViewById(R.id.addDialogRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

//    public interface NoticeDialogListener {
//        public void onClick(DialogFragment dialog);
////        public void onDialogNegativeClick(DialogFragment dialog);
//    }
//    AddCollectionDialog.NoticeDialogListener listener;
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//
//        try {
//            listener = (AddCollectionDialog.NoticeDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString() + "must implement NoticeDialogListener");
//        }
//    }
}
