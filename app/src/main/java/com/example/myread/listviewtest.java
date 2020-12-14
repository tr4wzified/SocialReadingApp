package com.example.myread;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class listviewtest extends AppCompatActivity {
    private LinearLayout linearLayout;
    private LinearLayout booklistcomp;
    private LayoutInflater layoutInflater;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewtest);
        linearLayout = (LinearLayout)findViewById(R.id.bookScroll);
        booklistcomp = (LinearLayout)findViewById(R.id.booklistScroll);

        layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            user = ServerConnect.getInstance().getUser("Petertje");

            getUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        List<String> subjects = new ArrayList<String>();
//        BookCollection bookCollection = new BookCollection("Joop");
//        this.user = new User("Geert");

//        user.getCollectionList().add(bookCollection);
//        user.getCollectionList().add(bookCollection);
//        user.getCollectionList().add(bookCollection);

//        bookCollection.addBook("Joost", "Michael", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
//        bookCollection.addBook("Title", "author", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
//        bookCollection.addBook("Title", "author", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
//        bookCollection.addBook("Laatste", "Willem", "cover", "description", subjects, "9-12-2020", "Willem", 9, 3);
//
//        initBookItem(user.getBookCollections());
//        initBookList(user);
    }

    private void getUser() throws JSONException {
//        user = ServerConnect.getUser("Petertje");

        if (user != null) {
            initBookList(user);
//            initBookItem(user.get);
        }
    }

    private void initBookItem(BookCollection bookCollection) {
        for (Book boek : bookCollection.getBookList()) {
            View rowView = layoutInflater.inflate(R.layout.listitem, null);
            TextView book_title = (TextView) rowView.findViewById(R.id.bookTitle);
            TextView book_author = (TextView) rowView.findViewById(R.id.bookAuthor);
            book_title.setText(boek.title);
            book_author.setText(boek.author);
            linearLayout.addView(rowView, (linearLayout.getChildCount() -1));
        }
    }

    private void initBookList(User user) {
        for (BookCollection bc : user.getCollectionList()){
            View listRow = layoutInflater.inflate(R.layout.booklist_component, null);
            TextView listName = (TextView) listRow.findViewById(R.id.list_title);
            TextView bookAmount = (TextView) listRow.findViewById(R.id.book_amount);
            listName.setText(bc.name);
            bookAmount.setText(Integer.toString(bc.length()));
            initBookItem(bc);
            booklistcomp.addView(listRow, (booklistcomp.getChildCount() - 1) );
        }
    }
}
