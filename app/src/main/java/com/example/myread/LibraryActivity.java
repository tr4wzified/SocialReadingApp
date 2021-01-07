package com.example.myread;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.LibraryAdapter;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements NewCollectionDialog.NoticeDialogListener, LibraryAdapter.OnCardListener {
    ////    private EditText inputCollectionName;

    protected RecyclerView mRecyclerView;
    protected LibraryAdapter mAdapter;
    private final List<BookCollection> mCards = new ArrayList<>();
    protected User user;
    public BookCollection clickedCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        mRecyclerView = findViewById(R.id.libraryRecyclerView);
        user = User.getInstance();

        initRecyclerView();
        initCards();

        //    private LinearLayout linearLayout;
        //    private LinearLayout booklistcomp;
        //    private LayoutInflater layoutInflater;
        //    private User user;
        //    private SharedPreferences pref;
        //
        Button bookcollection_btn;
        EditText inputCollectionName = (EditText) findViewById(R.id.newCollectionName);
        bookcollection_btn = (Button) findViewById(R.id.bookCollectionButton);
        bookcollection_btn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            NewCollectionDialog newCollectionDialog = new NewCollectionDialog();
            newCollectionDialog.show(fragmentManager, "dialog");
        });
    }

    private void initCards() {
        mCards.clear();
        mCards.addAll(user.getCollectionList());
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mAdapter = new LibraryAdapter(mCards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
//        View dialogComp = layoutInflater.inflate(R.layout.dialog_newbookcollection, null);
        Dialog dialogView = dialog.getDialog();
        assert dialogView != null;
        EditText inputCollectionName = dialogView.findViewById(R.id.newCollectionName);
        String name = inputCollectionName.getText().toString();
        if (!GlobalFunctions.asciip(name)) {
            Toast.makeText(LibraryActivity.this, "Please only use ASCII characters in the library name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user.addBookCollection(new BookCollection(name))) {
            mCards.add(new BookCollection(name));
            Toast.makeText(LibraryActivity.this, "Collection: " + name + " has been created.", Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
            return;
        }
        Toast.makeText(LibraryActivity.this, "Failed to add collection: " + name + ".", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnCardClick(int position) {
        clickedCard = mCards.get(position);
        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra("collectiontitle", clickedCard.name);
        startActivity(intent);
    }

    @Override
    public void OnButtonClick(int position) {
        deleteCard(position);
    }

    private void deleteCard(int position) {
        BookCollection bookcollection = mCards.get(position);
        mCards.remove(bookcollection);
        user.deleteBookCollection(bookcollection);
        Toast.makeText(LibraryActivity.this, "Collection: " + bookcollection.name + " has been deleted.", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    /*
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction){}


    };

     */
}
