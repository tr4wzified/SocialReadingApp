package com.example.myread;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.CollectionAdapter;
import com.example.myread.adapters.CollectionListAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnCardListener, CollectionListAdapter.OnCardListener {
    protected RecyclerView mRecyclerView;
    protected CollectionAdapter mAdapter;
    private final List<Book> mCards = new ArrayList<>();
    protected User user;
    private Book clickedBook;
    private AddCollectionDialog addCollectionDialog;
    private List<BookCollection> mListItem;
    private TextView bookAmount;
    private TextView editText;
    String collectionTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mRecyclerView = findViewById(R.id.collectionRecyclerView);
        user = User.getInstance();
        collectionTitle = getIntent().getStringExtra("collectiontitle");
        bookAmount = findViewById(R.id.book_collections);
        editText = findViewById(R.id.book_collection);

        updateData();
        initRecyclerView();
        initBooks();
    }

    private void updateData() {
        if (collectionTitle.length() > 20) {
            editText.setText(collectionTitle.substring(0, 19).concat("..."));
        } else {
            editText.setText(collectionTitle);
        }
        String books = "Books: " + user.getBookCollection(collectionTitle).size();
        bookAmount.setText(books);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new CollectionAdapter(mCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initBooks() {
        mCards.clear();
        mCards.addAll(user.getBookCollection(collectionTitle));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnCardClick(int position) {
        Book tempBook = mCards.get(position);
        user.setTempBook(tempBook);
        Intent intent = new Intent(this, BookActivity.class);
//        intent.putExtra("Book", bookTitle);
//        intent.putExtra("Collection", collectionTitle);
        startActivity(intent);
    }

    @Override
    public void OnPositiveButtonClick(int position) {
        clickedBook = mCards.get(position);
        mListItem = user.getCollectionList();
        CollectionListAdapter collectionListAdapter = new CollectionListAdapter(mListItem, this);
        addCollectionDialog = new AddCollectionDialog(this, collectionListAdapter);
        addCollectionDialog.show();
        addCollectionDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void OnNegativeButtonClick(int position) {
        BookCollection bc = user.getBookCollectionByName(collectionTitle);
        Book deletedBook = mCards.get(position);
        mCards.remove(deletedBook);
        bc.delete(deletedBook);
        Toast.makeText(CollectionActivity.this, deletedBook.title + " has been removed from " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        updateData();
    }

    @Override
    public void OnListItemClick(int position) {
        BookCollection bc = mListItem.get(position);
//        bc.addBook(clickedBook);
        user.getBookCollection(bc).addBookToServer(clickedBook);
        if (bc.name.equals(collectionTitle)) {
            mCards.add(clickedBook);
        }
        Toast.makeText(CollectionActivity.this, clickedBook.title + " has been added to " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        updateData();
        addCollectionDialog.cancel();
    }
}
