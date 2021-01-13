package com.example.myread;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.adapters.CollectionAdapter;
import com.example.myread.adapters.CollectionListAdapter;
import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionFragment extends Fragment implements CollectionAdapter.OnCardListener, CollectionListAdapter.OnCardListener {
    private RecyclerView mRecyclerView;
    private CollectionAdapter mAdapter;

    private final List<Book> mCards = new ArrayList<>();
    private User user;
    private Book clickedBook;
    private AddCollectionDialog addCollectionDialog;
    private List<BookCollection> mListItem;
    private TextView bookAmount;
    private TextView editText;
    private String collectionTitle;
    private Button renamebutton;
    private RenameCollectionDialog renameCollectionDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_collection, container, false);

        renamebutton = rootView.findViewById(R.id.renamebutton);
        renamebutton.setOnClickListener(v -> {
            renameCollectionDialog = new RenameCollectionDialog();
            renameCollectionDialog.setTargetFragment(this, 1);
            renameCollectionDialog.show(getParentFragmentManager(), "dialog");
        });

        final FragmentActivity c = getActivity();
        mRecyclerView = rootView.findViewById(R.id.collectionRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CollectionAdapter(mCards, CollectionFragment.this);
        user = User.getInstance();
//        collectionTitle = getArguments().getString("collectiontitle");
        collectionTitle = User.getInstance().getTempTitle();
        bookAmount = rootView.findViewById(R.id.book_collections);
        editText = rootView.findViewById(R.id.book_collection);

        new Thread(() -> {
            mRecyclerView.setAdapter(mAdapter);
            updateData();
            initCards();
        }).start();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            final Dialog dialogView = renameCollectionDialog.getDialog();
            assert dialogView != null;
            final EditText inputCollectionName = dialogView.findViewById(R.id.renameCollectionName);
            final String name = inputCollectionName.getText().toString();
            //Checks for non ascii characters.
            if (!GlobalFunctions.asciip(name)) {
                Toast.makeText(getActivity(), "Please only use ASCII characters in the library name.", Toast.LENGTH_SHORT).show();
                return;
            }
            //Checks if the adding of the book collection was successful.
            if (user.renameBookCollection(user.getInstance().getBookCollectionByName(collectionTitle), name)) {
                Toast.makeText(getActivity(), "Collection has been renamed to " + name + ".", Toast.LENGTH_SHORT).show();
                if (name.length() > 20)
                    editText.setText(name.substring(0, 19).concat("..."));
                else
                    editText.setText(name);

                mAdapter.notifyDataSetChanged();

                return;
            }
            Toast.makeText(getActivity(), "Failed to rename collection: " + name + ".", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A function that updates the view.
     */
    private void updateData() {
        if (collectionTitle.length() > 20)
            editText.setText(collectionTitle.substring(0,19).concat("..."));
        else
            editText.setText(collectionTitle);

        bookAmount.setText("Books: " + user.getBookCollection(collectionTitle).size());
    }

    /**
     * A function that adds searched books to the mcards list and refreshes the view.
     */
    private void initCards() {
        mCards.clear();
        mCards.addAll(user.getBookCollection(collectionTitle));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnCardClick(int position) {
        final Book tempBook = mCards.get(position);
        user.setTempBook(tempBook);
        Navigation.findNavController(requireView()).navigate(R.id.action_collectionFragment_to_bookFragment);
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
        user.getBookCollection(bc).add(clickedBook);
        if (bc.name.equals(collectionTitle)) mCards.add(clickedBook);
        Toast.makeText(getActivity(), clickedBook.title + " has been added to " + bc.name, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        updateData();
        addCollectionDialog.cancel();
    }
}
