package com.example.myread;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.LibraryAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements BookCollectionDialog.NoticeDialogListener, LibraryAdapter.OnCardListener {
//    private LinearLayout linearLayout;
//    private LinearLayout booklistcomp;
//    private LayoutInflater layoutInflater;
//    private User user;
//    private SharedPreferences pref;
//
    private Button bookcollection_btn;
    private EditText inputCollectionName;

    protected RecyclerView mRecyclerView;
    protected LibraryAdapter mAdapter;
    private List<BookCollection> mCards = new ArrayList<>();
    protected User user;
    public String clickedCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mRecyclerView = findViewById(R.id.libraryRecyclerView);
        user = User.getInstance();


        initRecyclerView();
        initCards();
//        linearLayout = (LinearLayout)findViewById(R.id.bookScroll);
//        booklistcomp = (LinearLayout)findViewById(R.id.booklistScroll);
        bookcollection_btn = (Button)findViewById(R.id.bookCollectionButton);
        inputCollectionName = (EditText)findViewById(R.id.newCollectionName);
//
//
//        layoutInflater = (LayoutInflater)
//                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        pref = getSharedPreferences("user_details", MODE_PRIVATE);
//        user = User.getInstance();
//        user.name = pref.getString("username", "");
//
//        initBookList(user);
//
        bookcollection_btn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            BookCollectionDialog bookCollectionDialog = new BookCollectionDialog();
            bookCollectionDialog.show(fragmentManager, "dialog");
        });

    }

    private void initCards() {
        mCards.addAll(user.getCollectionList());
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mAdapter = new LibraryAdapter(mCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
//        View dialogComp = layoutInflater.inflate(R.layout.dialog_bookcollection, null);
        Dialog dialogView = dialog.getDialog();
        assert dialogView != null;
        EditText inputCollectionName = dialogView.findViewById(R.id.newCollectionName);
        String name = inputCollectionName.getText().toString();
        user.addBookCollection(new BookCollection(name));
    }

    @Override
    public void OnCardClick(int position) {
        clickedCard = mCards.get(position).name;
        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra("collectiontitle", clickedCard);
        startActivity(intent);
    }

    private void deleteCard(BookCollection bc) {
        mCards.remove(bc);
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
}
