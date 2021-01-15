package com.example.myread.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.R;
import com.example.myread.ServerConnect;
import com.example.myread.adapters.BookListItemAdapter;
import com.example.myread.adapters.RecommendedAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements BookListItemAdapter.OnCardListener, RecommendedAdapter.OnCardListener {
    private BookListItemAdapter mAdapter;
    private RecommendedAdapter rAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView rRecyclerView;

    private Book clickedCard;
    protected User user = User.getInstance();
    private final List<Book> mCards = new ArrayList<>();
    private final List<Book> rCards = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final FragmentActivity c = getActivity();
        mRecyclerView = rootView.findViewById(R.id.bookListItemRecyclerView);
        rRecyclerView = rootView.findViewById(R.id.recommendRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        LinearLayoutManager rLayoutManager = new LinearLayoutManager(c, LinearLayoutManager.HORIZONTAL, false);

        rRecyclerView.setLayoutManager(rLayoutManager);
        mRecyclerView.setLayoutManager(layoutManager);
        rAdapter = new RecommendedAdapter(rCards, HomeFragment.this);
        mAdapter = new BookListItemAdapter(mCards, HomeFragment.this);
        mRecyclerView.setAdapter(mAdapter);
        rRecyclerView.setAdapter(rAdapter);

        TextView recommendText = rootView.findViewById(R.id.recommended_title);
        recommendText.setOnClickListener(v -> {
            ServerConnect.getInstance().getRecommendations();
            rAdapter.notifyDataSetChanged();
        });

        initCards();

        return rootView;
    }

    public void initCards() {
        mCards.clear();
        mCards.addAll(user.getAllBooksList());
        mAdapter.notifyDataSetChanged();
        initRecommended();
    }

    public void initRecommended() {
        rCards.clear();
        rCards.addAll(user.getRecommendations());
        rAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnCardClick(int position) {
        clickedCard = mCards.get(position);
        User.getInstance().setTempBook(clickedCard);
        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_bookFragment);
    }


    @Override
    public void OnRecCardClick(int position) {
        clickedCard = rCards.get(position);
        User.getInstance().setTempBook(clickedCard);
        Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_bookFragment);
    }
}