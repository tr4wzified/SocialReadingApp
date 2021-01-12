package com.example.myread.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.GlobalApplication;
import com.example.myread.R;
import com.example.myread.models.BookCollection;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private final List<BookCollection> mCards;
    private final OnCardListener mOnCardListener;

    public LibraryAdapter(List<BookCollection> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }

    @NonNull
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_component, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.ViewHolder holder, int position) {
        if (mCards.get(position).name.length() > 26)
            holder.getListName().setText(mCards.get(position).name.substring(0, 25).concat("..."));
        else
            holder.getListName().setText(mCards.get(position).name);

//        String as = holder.getListName().getText().toString();
//        String ad = Integer.toString(user.getBookCollection(as).getBookList().size());
        holder.getBookAmount().setText(GlobalApplication.getAppContext().getString(R.string.count_books, mCards.get(position).length()));
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView listName, bookAmount;
        final OnCardListener onCardListener;

        public ViewHolder(View view, OnCardListener onCardListener) {
            super(view);

            listName = view.findViewById(R.id.list_title);
            bookAmount = view.findViewById(R.id.book_amount);
            final Button deleteBtn = view.findViewById(R.id.delete_bookcollection);
            this.onCardListener = onCardListener;

            view.setOnClickListener(this);
            deleteBtn.setOnClickListener(v -> onCardListener.OnButtonClick(getAdapterPosition()));
        }

        public TextView getListName() {
            return listName;
        }

        public TextView getBookAmount() {
            return bookAmount;
        }

        @Override
        public void onClick(View v) {
            onCardListener.OnCardClick(getAdapterPosition());
        }

    }

    public interface OnCardListener {
        void OnCardClick(int position);
        void OnButtonClick(int position);
    }
}
