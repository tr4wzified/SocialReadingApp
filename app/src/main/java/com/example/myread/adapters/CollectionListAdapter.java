package com.example.myread.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.R;
import com.example.myread.models.BookCollection;

import java.util.List;

public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder> {
    private final List<BookCollection> mCards;
    private final OnCardListener mOnCardListener;

    public CollectionListAdapter(List<BookCollection> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }

    @NonNull
    @Override
    public CollectionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_component_addingto, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionListAdapter.ViewHolder holder, int position) {
        if (holder.getListName().length() > 20) {
            holder.getListName().setText(mCards.get(position).name.substring(0, 19).concat("..."));
        } else {
            holder.getListName().setText(mCards.get(position).name);
        }
//        String as = holder.getListName().getText().toString();
//        String ad = Integer.toString(user.getBookCollection(as).getBookList().size());
        String amount = "Books: ".concat(Integer.toString(mCards.get(position).length()));
        holder.getBookAmount().setText(amount);
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
            Button deleteBtn = view.findViewById(R.id.delete_bookcollection);
            this.onCardListener = onCardListener;

            view.setOnClickListener(this);
        }

        public TextView getListName() {
            return listName;
        }

        public TextView getBookAmount() {
            return bookAmount;
        }

        @Override
        public void onClick(View v) {
            onCardListener.OnListItemClick(getAdapterPosition());
        }

    }

    public interface OnCardListener {
        void OnListItemClick(int position);
    }
}
