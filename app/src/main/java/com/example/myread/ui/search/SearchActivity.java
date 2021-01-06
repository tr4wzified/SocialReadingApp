package com.example.myread.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.AddCollectionDialog;
import com.example.myread.BookActivity;
import com.example.myread.CollectionActivity;
import com.example.myread.LibraryActivity;
import com.example.myread.LoginActivity;
import com.example.myread.R;
import com.example.myread.ServerConnect;
import com.example.myread.adapters.CollectionAdapter;
import com.example.myread.adapters.CollectionListAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements CollectionAdapter.OnCardListener, CollectionListAdapter.OnCardListener {
    protected RecyclerView mRecyclerView;
    protected CollectionAdapter mAdapter;
    private List<Book> mCards = new ArrayList<>();
    protected User user;
    private String collectionTitle;
    private Book clickedBook;
    private AddCollectionDialog addCollectionDialog;
    private List<BookCollection> mListItem;
    private String bookTitle;
    private ProgressBar spinner;

    //    TextView bookName;
    Button getBook;
    TextInputLayout bookUserInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);
        spinner = (ProgressBar) findViewById(R.id.loadingIconSearch);
        mRecyclerView = findViewById(R.id.searchRecyclerView);
        user = User.getInstance();
        collectionTitle = getIntent().getStringExtra("collectiontitle");

//        bookName = findViewById(R.id.bookName);
        getBook = findViewById(R.id.searchBook);
        bookUserInput = findViewById(R.id.bookSearchQuery);


        initRecyclerView();
//        initBooks();

        getBook.setOnClickListener(v -> new Thread(() -> {
            runOnUiThread(() -> spinner.setVisibility(View.VISIBLE));
            Looper.prepare();
            showBookResults();
            runOnUiThread(() -> spinner.setVisibility(View.INVISIBLE));
        }).start());
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new CollectionAdapter(mCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showBookResults() {
        mCards.clear();
        mCards.addAll(searchBooks());
        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
    }

    private List<Book> searchBooks() {
        String bookName = bookUserInput.getEditText().getText().toString();
        List<Book> bookList = ServerConnect.getInstance().getBooks(bookName);
        if (bookList.isEmpty()) {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
        }
        return bookList;
    }

    @Override
    public void OnCardClick(int position) {
        user.setTempBook(mCards.get(position));
//        bookTitle = mCards.get(position).title;
        Intent intent = new Intent(this, BookActivity.class);
//        intent.putExtra("Book", bookTitle);
        startActivity(intent);
    }

    @Override
    public void OnPositiveButtonClick(int position) {
        if (user.getCollectionList().isEmpty()) {
            Toast.makeText(this, "No existing collections, try adding a collection first.", Toast.LENGTH_SHORT).show();
            return;
        }
        clickedBook = mCards.get(position);
        mListItem = user.getCollectionList();
        CollectionListAdapter collectionListAdapter = new CollectionListAdapter(mListItem, this);
        addCollectionDialog = new AddCollectionDialog(this, collectionListAdapter);
        addCollectionDialog.show();
        addCollectionDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void OnNegativeButtonClick(int position) { }

    @Override
    public void OnListItemClick(int position) {
        BookCollection bc = mListItem.get(position);
        bc.addBookToServer(clickedBook);
        Toast.makeText(SearchActivity.this, clickedBook.title + " has been added to " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        addCollectionDialog.cancel();
    }
}
