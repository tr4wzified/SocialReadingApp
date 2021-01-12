package com.example.myread.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.CollectionFragment;
import com.example.myread.R;
import com.example.myread.adapters.BookListItemAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements BookListItemAdapter.OnCardListener {
    private BookListItemAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Book clickedCard;
    protected User user = User.getInstance();
    private List<Book> mCards = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final FragmentActivity c = getActivity();
        mRecyclerView = rootView.findViewById(R.id.bookListItemRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BookListItemAdapter(mCards, HomeFragment.this);

        mRecyclerView.setAdapter(mAdapter);
        initCards();

        return rootView;
    }

    private void initCards() {
        mCards.clear();
        mCards.addAll(user.getAllBooksList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnCardClick(int position) {
        clickedCard = mCards.get(position);
        User.getInstance().setTempBook(clickedCard);
        Navigation.findNavController(getView()).navigate(R.id.action_collectionFragment_to_bookFragment);
    }
}