package com.example.myread;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;


import java.util.ArrayList;
import java.util.List;

public class listviewtest extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewtest);
        constraintLayout = (ConstraintLayout) findViewById(R.id.booklist);
        linearLayout = (LinearLayout)findViewById(R.id.booklist_linear);

        List<View> views = new ArrayList();
        List<String> subjects = new ArrayList<String>();
        BookCollection bookCollection = new BookCollection("Joop");

        bookCollection.addBook("Joost", "Michael", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
        bookCollection.addBook("Title", "author", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
        bookCollection.addBook("Title", "author", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
        bookCollection.addBook("Laatste", "Willem", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);

        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (Book boek : bookCollection.getBookList()) {
            View rowView = layoutInflater.inflate(R.layout.listitem, null);
            TextView book_title = (TextView) rowView.findViewById(R.id.bookTitle);
            TextView book_author = (TextView) rowView.findViewById(R.id.bookAuthor);
            book_title.setText(boek.title);
            book_author.setText(boek.author);
            linearLayout.addView(rowView, (linearLayout.getChildCount() -1));

//            views.add(rowView);
        }
//        for (int i = 0; i < views.size(); i++) {
//            linearLayout.addView(views.get(i));
//        }
    }
}
