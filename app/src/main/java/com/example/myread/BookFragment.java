package com.example.myread;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myread.models.Book;
import com.example.myread.models.User;
import com.squareup.picasso.Picasso;

import java.io.File;

public class BookFragment extends Fragment {
    private TextView book_title, book_author, book_rating, book_description, book_genre, book_isbn, book_year;
    private User user = User.getInstance();
    private ImageView book_cover;
    private Book currentBook;
    private Button wikiBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_book, container, false);

        book_cover = rootView.findViewById(R.id.book_cover_page);
        book_title = rootView.findViewById(R.id.book_title);
        book_author = rootView.findViewById(R.id.book_author);
        book_rating = rootView.findViewById(R.id.book_rating);
        book_description = rootView.findViewById(R.id.book_description);
        book_genre = rootView.findViewById(R.id.book_genre);
        book_isbn = rootView.findViewById(R.id.book_isbn);
        book_year = rootView.findViewById(R.id.book_year);

        currentBook = user.getTempBook();
        wikiBtn = rootView.findViewById(R.id.wiki_btn);

        wikiBtn.setOnClickListener(v -> openLink());

        initBook();
        return rootView;
    }

    public void updateField(TextView view, String text) {
        if (text == null || text.equals("")) {
            view.setText(R.string.unknown);
            return;
        }
        view.setText(text);
    }

    public void openLink() {
        if (currentBook.bookWiki.contains("http")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.bookWiki)));
            return;
        }
        Toast.makeText(getActivity(), currentBook.title + " does not have a wikipedia page.", Toast.LENGTH_SHORT).show();
    }

    public void initBook() {
        //Picasso.get().load(R.drawable.nocover_dark).into(book_cover);
        if (currentBook.cover.contains("http"))
            Picasso.get().load(currentBook.cover).into(book_cover);
        updateField(book_title, currentBook.title);
        updateField(book_author, currentBook.author);
        updateField(book_rating, currentBook.rating);
        updateField(book_description, currentBook.description);
        if (!(currentBook.subjects.size() == 0))
            updateField(book_genre, currentBook.subjects.get(0));
        else {
            book_genre.setText(R.string.unknown);
        }
        updateField(book_isbn, currentBook.isbn);
        updateField(book_year, currentBook.publishDate);
    }
}