package com.example.myread;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.CollectionAdapter;
import com.example.myread.adapters.CollectionListAdapter;
import com.example.myread.adapters.LibraryAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;
import com.example.myread.ui.library.LibraryFragment;

import java.util.ArrayList;
import java.util.List;

public class CollectionFragment extends Fragment implements CollectionAdapter.OnCardListener, CollectionListAdapter.OnCardListener {
    private CollectionAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Book tempBook;
    private Book clickedBook;
    private AddCollectionDialog addCollectionDialog;
    private User user = User.getInstance();
    private List<Book> mCards = new ArrayList<>();
    private List<BookCollection> mListItem;
    private TextView bookAmount;
    private TextView editText;
    String collectionTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_collection, container, false);
        final FragmentActivity c = getActivity();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.collectionRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CollectionAdapter(mCards, CollectionFragment.this);

        collectionTitle = getArguments().getString("collectiontitle");
        bookAmount = rootView.findViewById(R.id.book_collections);
        editText = rootView.findViewById(R.id.book_collection);

        new Thread(() -> {
            mRecyclerView.setAdapter(mAdapter);
            updateData();
            initBooks();
        }).start();

        return rootView;
    }

    private void updateData() {
        if (collectionTitle.length() > 20) {
            editText.setText(collectionTitle.substring(0,19).concat("..."));
        }
        else {
            editText.setText(collectionTitle);
        }
        String books = "Books: " + user.getBookCollection(collectionTitle).size();
        bookAmount.setText(books);
    }

    private void initBooks() {
        mCards.clear();
        mCards.addAll(user.getBookCollection(collectionTitle));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnCardClick(int position) {
        tempBook = mCards.get(position);
        user.setTempBook(tempBook);
        Fragment fragment = new BookFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();

//        Intent intent = new Intent(getActivity(), BookActivity.class);
////        intent.putExtra("Book", bookTitle);
////        intent.putExtra("Collection", collectionTitle);
//        startActivity(intent);
    }

    @Override
    public void OnPositiveButtonClick(int position) {
        clickedBook = mCards.get(position);
        mListItem = user.getCollectionList();
        CollectionListAdapter collectionListAdapter = new CollectionListAdapter(mListItem, this);
        addCollectionDialog = new AddCollectionDialog(getActivity(), collectionListAdapter);
        addCollectionDialog.show();
        addCollectionDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void OnNegativeButtonClick(int position) {
        BookCollection bc = user.getBookCollectionByName(collectionTitle);
        Book deletedBook = mCards.get(position);
        mCards.remove(deletedBook);
        bc.delete(deletedBook);
        Toast.makeText(getActivity(), deletedBook.title + " has been removed from " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        updateData();
    }

    @Override
    public void OnListItemClick(int position) {
        BookCollection bc = mListItem.get(position);
        user.getBookCollection(bc).addBookToServer(clickedBook);
        if (bc.name.equals(collectionTitle)) {
            mCards.add(clickedBook);
        }
        Toast.makeText(getActivity(), clickedBook.title + " has been added to " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        updateData();
        addCollectionDialog.cancel();
    }
}
