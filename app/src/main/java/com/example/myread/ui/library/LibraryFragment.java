package com.example.myread.ui.library;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myread.CollectionFragment;
import com.example.myread.GlobalFunctions;
import com.example.myread.NewCollectionDialog;
import com.example.myread.R;
import com.example.myread.adapters.LibraryAdapter;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment implements LibraryAdapter.OnCardListener  {
    private LibraryAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private BookCollection clickedCard;
    protected User user = User.getInstance();
    private List<BookCollection> mCards = new ArrayList<>();
    NewCollectionDialog newCollectionDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_library, container, false);
        final FragmentActivity c = getActivity();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.libraryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(layoutManager);

        Button bookCollectionBtn = (Button)rootView.findViewById(R.id.bookCollectionButton);
        mAdapter = new LibraryAdapter(mCards, LibraryFragment.this);

        new Thread(() -> {
            mRecyclerView.setAdapter(mAdapter);
            initCards();
        }).start();


        bookCollectionBtn.setOnClickListener(v -> {
            newCollectionDialog = new NewCollectionDialog();
            newCollectionDialog.setTargetFragment(this, 1);
            newCollectionDialog.show(getFragmentManager(), "dialog");
        });

        return rootView;
    }

    private void initCards() {
        mCards.clear();
        mCards.addAll(user.getCollectionList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnCardClick(int position) {
        clickedCard = mCards.get(position);
//        Intent intent = new Intent(getActivity(), CollectionFragment.class);
//        intent.putExtra("collectiontitle", clickedCard.name);
//        startActivity(intent);
        Bundle bundle = new Bundle();
        bundle.putString("collectiontitle", clickedCard.name);

        Fragment fragment = new CollectionFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();

//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit();
    }

    @Override
    public void OnButtonClick(int position) {
        BookCollection bookcollection = mCards.get(position);
        mCards.remove(bookcollection);
        user.deleteBookCollection(bookcollection);
        Toast.makeText(getActivity(), "Collection: " + bookcollection.name + " has been deleted.", Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Dialog dialogView = newCollectionDialog.getDialog();
            assert dialogView != null;
            EditText inputCollectionName = dialogView.findViewById(R.id.newCollectionName);
            String name = inputCollectionName.getText().toString();
            if (!GlobalFunctions.asciip(name)) {
                Toast.makeText(getActivity(), "Please only use ASCII characters in the library name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (user.addBookCollection(new BookCollection(name))) {
                mCards.add(new BookCollection(name));
                Toast.makeText(getActivity(), "Collection: " + name + " has been created.", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return;
            }
            Toast.makeText(getActivity(), "Failed to add collection: " + name + ".", Toast.LENGTH_SHORT).show();
        }
    }
}
