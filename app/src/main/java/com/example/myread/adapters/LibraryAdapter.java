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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklist_component, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    /** A function that will set the titles of the collection cards.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.ViewHolder holder, int position) {
        if (mCards.get(position).name.length() > 26) {
            holder.getListName().setText(mCards.get(position).name.substring(0, 25).concat("..."));
        } else {
            holder.getListName().setText(mCards.get(position).name);
        }
//        String as = holder.getListName().getText().toString();
//        String ad = Integer.toString(user.getBookCollection(as).getBookList().size());
        holder.getBookAmount().setText(GlobalApplication.getAppContext().getString(R.string.count_books, mCards.get(position).length()));
    }

    /**
     * A function to get the amount of items in mCards.
     * @return the size of mCards.
     */
    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView listName, bookAmount;
        final OnCardListener onCardListener;

        public ViewHolder(View view, OnCardListener onCardListener) {
            super(view);

//            view.setOnClickListener(v -> {
//                Intent intent = new Intent(view.getContext(), BookCollectionActivity.class);
//                view.getContext().startActivity(intent);
//            });

            listName = view.findViewById(R.id.list_title);
            bookAmount = view.findViewById(R.id.book_amount);
            Button deleteBtn = view.findViewById(R.id.delete_bookcollection);
            this.onCardListener = onCardListener;

            view.setOnClickListener(this);
            deleteBtn.setOnClickListener(v -> onCardListener.OnButtonClick(getAdapterPosition()));
        }

        /**
         * A function to get the book collection.
         * @return the book collection textview.
         */
        public TextView getListName() {
            return listName;
        }

        /**
         * A function to get the amount of books in the book collection.
         * @return the book amount textview.
         */
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
