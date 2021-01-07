package com.example.myread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myread.models.Book;
import com.example.myread.models.User;

public class BookFragment extends Fragment {
    private TextView book_title, book_author, book_rating, book_description, book_genre, book_isbn, book_year;
    private User user = User.getInstance();
    private Book currentBook;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_book, container, false);

        book_title = rootView.findViewById(R.id.book_title);
        book_author = rootView.findViewById(R.id.book_author);
        book_rating = rootView.findViewById(R.id.book_rating);
        book_description = rootView.findViewById(R.id.book_description);
        book_genre = rootView.findViewById(R.id.book_genre);
        book_isbn = rootView.findViewById(R.id.book_isbn);
        book_year = rootView.findViewById(R.id.book_year);

        currentBook = user.getTempBook();

        initBook();
        return rootView;
    }

    public void initBook() {
        book_title.setText(currentBook.title);
        book_author.setText(currentBook.author);
        book_rating.setText(currentBook.rating);
        book_description.setText(currentBook.description);
//        book_genre.setText(currentBook.genre);
        book_isbn.setText(currentBook.isbn);
        book_year.setText(currentBook.publishDate);
    }
}
