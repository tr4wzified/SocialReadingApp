package com.example.myread.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.AddCollectionDialog;
import com.example.myread.GlobalApplication;
import com.example.myread.R;
import com.example.myread.ServerConnect;
import com.example.myread.adapters.CollectionListAdapter;
import com.example.myread.adapters.CollectionSearchAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment implements CollectionSearchAdapter.OnCardListener, CollectionListAdapter.OnCardListener {
    private final List<Book> mCards = new ArrayList<>();
    private CollectionSearchAdapter mAdapter;
    protected User user = User.getInstance();
    private TextInputLayout bookUserInput;
    private List<BookCollection> mListItem;
    private Book clickedBook;
    private AddCollectionDialog addCollectionDialog;
    private ProgressBar spinner;
    private final Context context = GlobalApplication.getAppContext();
    private boolean destroyed;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        final FragmentActivity c = getActivity();
        final RecyclerView mRecyclerView = rootView.findViewById(R.id.searchRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(c));

        final Button searchBookBtn = rootView.findViewById(R.id.searchBook);
        bookUserInput = rootView.findViewById(R.id.bookSearchQuery);
        spinner = rootView.findViewById(R.id.loadingIconSearch);
        destroyed = false;

        new Thread(() -> {
            mAdapter = new CollectionSearchAdapter(mCards, SearchFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        }).start();

        searchBookBtn.setOnClickListener(v -> new Thread(() -> {

            requireActivity().runOnUiThread(() -> spinner.setVisibility(View.VISIBLE));
            Looper.prepare();
            initCards();
            if (!destroyed)
                requireActivity().runOnUiThread(() -> spinner.setVisibility(View.INVISIBLE));
        }).start());

        return rootView;
    }

    /**
     * A function that adds searched books to the mCards list and refreshes the view.
     */
    public void initCards() {
        mCards.clear();
        mCards.addAll(searchBooks());
        if (!destroyed)
            requireActivity().runOnUiThread(() -> mAdapter.notifyDataSetChanged());
    }

    /**
     * A function that searches for books based on user input.
     * @return a list of books.
     */
    private List<Book> searchBooks() {
        final String bookName = Objects.requireNonNull(bookUserInput.getEditText()).getText().toString();
        final List<Book> bookList = ServerConnect.getInstance().getBooks(bookName);
        if (!destroyed && bookList.isEmpty()) {
            Toast.makeText(getActivity(), context.getString(R.string.no_results), Toast.LENGTH_SHORT).show();
        }
        return bookList;
    }

    @Override
    public void OnCardClick(int position) {
        user.setTempBook(mCards.get(position));
        Navigation.findNavController(getView()).navigate(R.id.action_nav_search_to_bookFragment);
    }

    @Override
    public void OnPositiveButtonClick(int position) {
        if (user.getCollectionList().isEmpty()) {
            Toast.makeText(getActivity(), context.getString(R.string.no_collections), Toast.LENGTH_SHORT).show();
            return;
        }
        clickedBook = mCards.get(position);
        mListItem = user.getCollectionList();
        final CollectionListAdapter collectionListAdapter = new CollectionListAdapter(mListItem, this);
        addCollectionDialog = new AddCollectionDialog(getActivity(), collectionListAdapter);
        addCollectionDialog.show();
        addCollectionDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void OnListItemClick(int position) {
        final BookCollection bc = mListItem.get(position);
        bc.add(clickedBook);
        Toast.makeText(getActivity(), clickedBook.title + " has been added to " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        addCollectionDialog.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ServerConnect.getInstance().cancelSearchRequests();
        destroyed = true;
    }
}