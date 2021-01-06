package com.example.myread;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myread.models.Book;
import com.example.myread.models.User;
import com.squareup.picasso.Picasso;

public class BookActivity extends AppCompatActivity {
    private TextView book_title, book_author, book_rating, book_description, book_genre, book_isbn, book_year;
    private Book currentBook;
    private ImageView book_cover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
//        String bookTitle = getIntent().getStringExtra("Book");
//        String collectionTitle = getIntent().getStringExtra("Collection");
        User user = User.getInstance();

        book_cover = findViewById(R.id.book_cover_page);
        book_title = findViewById(R.id.book_title);
        book_author = findViewById(R.id.book_author);
        book_rating = findViewById(R.id.book_rating);
        book_description = findViewById(R.id.book_description);
        book_genre = findViewById(R.id.book_genre);
        book_isbn = findViewById(R.id.book_isbn);
        book_year = findViewById(R.id.book_year);


//        currentBook = user.getBook(collectionTitle, bookTitle);
        currentBook = user.getTempBook();

        initBook();
    }

    public void updateField(TextView view, String text) {
        if (text == null) {
            view.setText("unknown");
            return;
        }
        if (text.equals("")) {
            view.setText("unknown");
            return;
        }
        view.setText(text);
    }

    public void initBook() {
        if (currentBook.cover.contains("http"))
            Picasso.get().load(currentBook.cover).into(book_cover);
        updateField(book_title, currentBook.title);
        updateField(book_author, currentBook.author);
        updateField(book_rating, currentBook.rating);
        updateField(book_description, currentBook.description);
        if (!(currentBook.subjects.size() == 0))
            updateField(book_genre, currentBook.subjects.get(0));
        updateField(book_isbn, currentBook.isbn);
        updateField(book_year, currentBook.publishDate);
    }
}
