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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

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
        getBook.setOnClickListener(v -> bookName.setText(getBookName()));
        return root;
    }

    private String getBookName() {
        String bookID = bookUserInput.getEditText().getText().toString();
        ServerConnect.Response getResponse = ServerConnect.getInstance().getBook(bookID);
        String jsonString = getResponse.responseString;
        String bookName = "Not found";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            bookName = jsonObject.getString("title");
        } catch (JSONException e) {
            System.out.println("Something is wrong with the JSON.");
            System.out.println("Json parse error: " + e.getMessage());
        }
        return bookName;
    }
}