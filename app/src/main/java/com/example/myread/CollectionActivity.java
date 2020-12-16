package com.example.myread;

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
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnCardListener {
    protected RecyclerView mRecyclerView;
    protected CollectionAdapter mAdapter;
    private List<Book> mCards = new ArrayList<>();
    protected User user;
    private String collectionTitle;
    private String bookTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mRecyclerView = findViewById(R.id.collectionRecyclerView);
        user = User.getInstance();
        collectionTitle = getIntent().getStringExtra("collectiontitle");


        initRecyclerView();
        initBooks();

    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mAdapter = new CollectionAdapter(mCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initBooks() {
        mCards.addAll(user.getBookCollection(collectionTitle));
    }

    private void deleteCard(Book b) {
        mCards.remove(b);
        mAdapter.notifyDataSetChanged();

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteCard(mCards.get(viewHolder.getAdapterPosition()));
        }
    };

    @Override
    public void OnCardClick(int position) {
        bookTitle = mCards.get(position).title;
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("Book", bookTitle);
        startActivity(intent);
    }
}
