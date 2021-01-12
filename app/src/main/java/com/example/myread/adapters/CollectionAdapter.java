package com.example.myread.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.R;
import com.example.myread.models.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private final List<Book> mCards;
    private final OnCardListener mOnCardListener;

    public CollectionAdapter(List<Book> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView bookTitle, bookAuthor;
        private ImageView medium_cover_image;
        final OnCardListener onCardListener;

        public ViewHolder(View view, OnCardListener onCardListener) {
            super(view);
            bookTitle = view.findViewById(R.id.bookTitle);
            bookAuthor = view.findViewById(R.id.bookAuthor);
            medium_cover_image = view.findViewById(R.id.book_cover);

            Button buttonAdd = view.findViewById(R.id.button_add);
            Button buttonDelete = view.findViewById(R.id.button_delete);
            this.onCardListener = onCardListener;

            view.setOnClickListener(this);

            buttonAdd.setOnClickListener(v -> onCardListener.OnPositiveButtonClick(getAdapterPosition()));
            buttonDelete.setOnClickListener(v -> onCardListener.OnNegativeButtonClick(getAdapterPosition()));
        }

        /**
         * A function to get the title of the book.
         * @return the book title.
         */
        public TextView getBookTitle() {
            return bookTitle;
        }

        /**
         * A function to get the author of the book.
         * @return the book author.
         */
        public TextView getBookAuthor() {
            return bookAuthor;
        }

        /**
         * A function to get the cover of the book with medium quality.
         * @return the medium-quality book cover.
         */
        public ImageView getMediumBookCover() {
            return medium_cover_image;
        }

        @Override
        public void onClick(View v) {
            onCardListener.OnCardClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    /** A function that will set the titles of the collection cards, load the cover and set the author's name.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCards.get(position).title.length() > 20) {
            holder.getBookTitle().setText(mCards.get(position).title.substring(0, 19).concat("..."));
        } else {
            holder.getBookTitle().setText(mCards.get(position).title);
        }
        if (mCards.get(position).mediumcover.contains("http"))
            Picasso.get().load(mCards.get(position).mediumcover).into(holder.getMediumBookCover());
        holder.getBookAuthor().setText(mCards.get(position).author);
    }

    /**
     * A function to get the amount of items in mCards.
     * @return the size of mCards.
     */
    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public interface OnCardListener {
        void OnCardClick(int position);

        void OnPositiveButtonClick(int position);

        void OnNegativeButtonClick(int position);
    }
}
