package com.example.myread.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myread.R;
import com.example.myread.ServerConnect;
import com.example.myread.models.Book;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    TextView bookName;
    Button getBook;
    TextInputLayout bookUserInput;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        bookName = root.findViewById(R.id.bookName);
        getBook = root.findViewById(R.id.searchBook);
        bookUserInput = root.findViewById(R.id.bookSearchQuery);
        getBook.setOnClickListener(v -> showBookResults());
        return root;
    }

    //TODO: Only shows in console for now, let it show on screen :)
    public void showBookResults() {
        for (Book book : searchBooks()) {
            System.out.println("Title: " + book.title);
            if (!(book.subjects.size() == 0))
                System.out.println("First subject: " + book.subjects.get(0));
            System.out.println(".");
        }
    }

    private List<Book> searchBooks() {
        String bookName = bookUserInput.getEditText().getText().toString();
        return ServerConnect.getInstance().getBooks(bookName);
    }
}