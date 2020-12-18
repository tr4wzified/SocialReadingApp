package com.example.myread;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.CollectionAdapter;
import com.example.myread.adapters.CollectionListAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnCardListener, CollectionListAdapter.OnCardListener {
    protected RecyclerView mRecyclerView;
    protected CollectionAdapter mAdapter;
    private List<Book> mCards = new ArrayList<>();
    protected User user;
    private Book tempBook;
    private Book clickedBook;
    private AddCollectionDialog addCollectionDialog;
    private List<BookCollection> mListItem;
    String collectionTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mRecyclerView = findViewById(R.id.collectionRecyclerView);
        user = User.getInstance();
        collectionTitle = getIntent().getStringExtra("collectiontitle");
        TextView bookAmount = findViewById(R.id.book_collections);
        TextView editText = findViewById(R.id.book_collection);
        editText.setText(collectionTitle);
        String books = "Books: " + user.getBookCollection(collectionTitle).size();
        bookAmount.setText(books);



        initRecyclerView();
        initBooks();

    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mAdapter = new CollectionAdapter(mCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initBooks() {
        mCards.clear();
        mCards.addAll(user.getBookCollection(collectionTitle));
        mAdapter.notifyDataSetChanged();
    }

    private void deleteCard(int position) {
        BookCollection bc = user.getBookCollectionByName(collectionTitle);
        Book deletedBook = mCards.get(position);
        mCards.remove(deletedBook);
        bc.delete(deletedBook);
        mAdapter.notifyDataSetChanged();
    }

//    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            deleteCard(mCards.get(viewHolder.getAdapterPosition()));
//        }
//    };

    @Override
    public void OnCardClick(int position) {
        tempBook = mCards.get(position);
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
        deleteCard(position);
    }

    @Override
    public void OnListItemClick(int position) {
        BookCollection bc = mListItem.get(position);
//        bc.addBook(clickedBook);
        user.getBookCollection(bc).addBookToServer(clickedBook);
        addCollectionDialog.cancel();
        mAdapter.notifyDataSetChanged();
    }
}
