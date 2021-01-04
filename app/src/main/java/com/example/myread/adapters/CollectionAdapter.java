package com.example.myread.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.R;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private List<Book> mCards = new ArrayList<>();
    private OnCardListener mOnCardListener;

    public CollectionAdapter(List<Book> cards, OnCardListener onCardListener) {
        this.mCards = cards;
        this.mOnCardListener = onCardListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView bookTitle, bookAuthor;
        OnCardListener onCardListener;
        private Button buttonAdd, buttonDelete;

        public ViewHolder(View view, OnCardListener onCardListener) {
            super(view);

//            view.setOnClickListener(v -> {
//                Intent intent = new Intent(view.getContext(), BookCollectionActivity.class);
//                view.getContext().startActivity(intent);
//            });
            // book cover
            bookTitle = view.findViewById(R.id.bookTitle);
            bookAuthor = view.findViewById(R.id.bookAuthor);
            buttonAdd = view.findViewById(R.id.button_add);
            buttonDelete = view.findViewById(R.id.button_delete);
            this.onCardListener = onCardListener;

            view.setOnClickListener(this);

            buttonAdd.setOnClickListener(v -> onCardListener.OnPositiveButtonClick(getAdapterPosition()));
            buttonDelete.setOnClickListener(v -> onCardListener.OnNegativeButtonClick(getAdapterPosition()));
        }

        public TextView getBookTitle() {
            return bookTitle;
        }

        public TextView getBookAuthor() {
            return bookAuthor;
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mCards.get(position).title.length() > 20) {
            holder.getBookTitle().setText(mCards.get(position).title.substring(0,24).concat("..."));
        }
        else {
            holder.getBookTitle().setText(mCards.get(position).title);
        }
        // book cover
        holder.getBookAuthor().setText(mCards.get(position).author);
    }

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
