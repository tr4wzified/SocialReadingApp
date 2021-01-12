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

import java.util.List;

public class BookListItemAdapter extends RecyclerView.Adapter<BookListItemAdapter.ViewHolder> {
    private final List<Book> mCards;
    private final BookListItemAdapter.OnCardListener mOnCardListener;

    public BookListItemAdapter(List<Book> mCards, OnCardListener onCardListener) {
        this.mCards = mCards;
        this.mOnCardListener = onCardListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView bookTitle, bookAuthor;
        private ImageView medium_cover_image;
        final BookListItemAdapter.OnCardListener onCardListener;

        public ViewHolder(View view, BookListItemAdapter.OnCardListener onCardListener) {
            super(view);
            bookTitle = view.findViewById(R.id.book_title);
            bookAuthor = view.findViewById(R.id.book_author);
            medium_cover_image = view.findViewById(R.id.book_cover);

            this.onCardListener = onCardListener;

            view.setOnClickListener(this);
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
    public BookListItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booklistitem, parent, false);
        return new BookListItemAdapter.ViewHolder(view, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListItemAdapter.ViewHolder holder, int position) {
        if (mCards.get(position).title.length() > 26) {
            holder.getBookTitle().setText(mCards.get(position).title.substring(0, 25).concat("..."));
        } else {
            holder.getBookTitle().setText(mCards.get(position).title);
        }
        if (mCards.get(position).mediumcover.contains("http"))
            Picasso.get().load(mCards.get(position).mediumcover).into(holder.getMediumBookCover());
        holder.getBookAuthor().setText(mCards.get(position).author);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public interface OnCardListener {
        void OnCardClick(int position);
    }
}
