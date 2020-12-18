package com.example.myread.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.R;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private List<BookCollection> mCards = new ArrayList<>();
    private OnCardListener mOnCardListener;

    public LibraryAdapter(List<BookCollection> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }

    @NonNull
    @Override
    public LibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_component, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.ViewHolder holder, int position) {
        holder.getListName().setText(mCards.get(position).name);
//        String as = holder.getListName().getText().toString();
//        String ad = Integer.toString(user.getBookCollection(as).getBookList().size());
        holder.getBookAmount().setText("Books: " + Integer.toString(mCards.get(position).length()));
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView listName, bookAmount;
        private final Button deleteBtn;
        OnCardListener onCardListener;

        public ViewHolder(View view, OnCardListener onCardListener) {
            super(view);

//            view.setOnClickListener(v -> {
//                Intent intent = new Intent(view.getContext(), BookCollectionActivity.class);
//                view.getContext().startActivity(intent);
//            });

            listName = view.findViewById(R.id.list_title);
            bookAmount = view.findViewById(R.id.book_amount);
            deleteBtn = view.findViewById(R.id.delete_bookcollection);
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
