package com.example.myread.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.GlobalApplication;
import com.example.myread.R;
import com.example.myread.models.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CollectionSearchAdapter extends RecyclerView.Adapter<CollectionSearchAdapter.ViewHolder> {
    private final List<Book> mCards;
    private final OnCardListener mOnCardListener;

    public CollectionSearchAdapter(List<Book> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView bookTitle, bookAuthor;
        private ImageView medium_cover_image;
        final OnCardListener onCardListener;

        public ViewHolder(View view, OnCardListener onCardListener) {
            super(view);

            // book cover
            bookTitle = view.findViewById(R.id.bookTitle);
            bookAuthor = view.findViewById(R.id.bookAuthor);
            final Button buttonAdd = view.findViewById(R.id.button_add);
            medium_cover_image = view.findViewById(R.id.book_cover);
            this.onCardListener = onCardListener;

            view.setOnClickListener(this);

            buttonAdd.setOnClickListener(v -> onCardListener.OnPositiveButtonClick(getAdapterPosition()));
        }

        public TextView getBookTitle() {
            return bookTitle;
        }

        public TextView getBookAuthor() {
            return bookAuthor;
        }

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
    public CollectionSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlistitem, parent, false);
        return new ViewHolder(view, mOnCardListener);
    }

    /** A function that will set the titles of the collection cards, load the cover and set the author's name.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCards.get(position).title.length() > 20)
            holder.getBookTitle().setText(mCards.get(position).title.substring(0, 19).concat("..."));
        else
            holder.getBookTitle().setText(mCards.get(position).title);

        // Set book cover
        boolean dataSaver = GlobalApplication.getEncryptedSharedPreferences().getBoolean("dataSaver", false);

        if (mCards.get(position).mediumcover.contains("http")) {
            if (dataSaver)
                Picasso.get().load(mCards.get(position).smallcover).into(holder.getMediumBookCover());
            else
                Picasso.get().load(mCards.get(position).mediumcover).into(holder.getMediumBookCover());
        }
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

    }
}
