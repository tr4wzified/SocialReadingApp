package com.example.myread.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.R;
import com.example.myread.models.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {
    private final List<Book> mCards;
    private final RecommendedAdapter.OnCardListener mOnCardListener;

    public RecommendedAdapter(List<Book> mCards, OnCardListener onCardListener) {
        this.mCards = mCards;
        this.mOnCardListener = onCardListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView medium_cover_image;
        final RecommendedAdapter.OnCardListener onCardListener;

        public ViewHolder(View view, RecommendedAdapter.OnCardListener onCardListener) {
            super(view);
            medium_cover_image = view.findViewById(R.id.recommend_image);

            this.onCardListener = onCardListener;

            view.setOnClickListener(this);
        }

        public ImageView getMediumBookCover() {
            return medium_cover_image;
        }

        @Override
        public void onClick(View v) {
            onCardListener.OnRecCardClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecommendedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommenditem, parent, false);
        return new RecommendedAdapter.ViewHolder(view, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedAdapter.ViewHolder holder, int position) {
        if (mCards.get(position).mediumcover.contains("http"))
            Picasso.get().load(mCards.get(position).mediumcover).into(holder.getMediumBookCover());
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public interface OnCardListener {
        void OnRecCardClick(int position);
    }
}
