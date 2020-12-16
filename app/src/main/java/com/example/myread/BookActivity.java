package com.example.myread;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myread.models.Book;
import com.example.myread.models.User;

public class BookActivity extends AppCompatActivity {
    private TextView book_title, book_author, book_rating, book_description, book_genre, book_isbn, book_year;
    private Book currentBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        String bookTitle = getIntent().getStringExtra("Book");
        String collectionTitle = getIntent().getStringExtra("Collection");
        User user = User.getInstance();
        book_title = findViewById(R.id.book_title);
        book_author = findViewById(R.id.book_author);
        book_rating = findViewById(R.id.book_rating);
        book_description = findViewById(R.id.book_description);
        book_genre = findViewById(R.id.book_genre);
        book_isbn = findViewById(R.id.book_isbn);
        book_year = findViewById(R.id.book_year);


        currentBook = user.getBook(collectionTitle, bookTitle);

        initBook();
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
