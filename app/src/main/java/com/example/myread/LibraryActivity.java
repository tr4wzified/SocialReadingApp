package com.example.myread;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.myread.models.Book;
import com.example.myread.models.BookCollection;
import com.example.myread.models.User;

public class LibraryActivity extends AppCompatActivity implements BookCollectionDialog.NoticeDialogListener {
    private LinearLayout linearLayout;
    private LinearLayout booklistcomp;
    private LayoutInflater layoutInflater;
    private User user;
    private SharedPreferences prf;

    private Button bookcollection_btn;
//    private EditText inputCollectionName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        linearLayout = (LinearLayout)findViewById(R.id.bookScroll);
        booklistcomp = (LinearLayout)findViewById(R.id.booklistScroll);
        bookcollection_btn = (Button)findViewById(R.id.bookCollectionButton);
//        inputCollectionName = (EditText)findViewById(R.id.newCollectionName);


        layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        prf = getSharedPreferences("user_details", MODE_PRIVATE);
        user = User.getInstance();
//        user.name = pref.getString("username", "");

        initBookList(user);

        bookcollection_btn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            BookCollectionDialog bookCollectionDialog = new BookCollectionDialog();
            bookCollectionDialog.show(fragmentManager, "dialog");
        });

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
//            initBookItem(bc);
            booklistcomp.addView(listRow, (booklistcomp.getChildCount() - 1) );
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
//        View dialogComp = layoutInflater.inflate(R.layout.dialog_bookcollection, null);
        Dialog dialogView = dialog.getDialog();
        assert dialogView != null;
        EditText inputCollectionName = dialogView.findViewById(R.id.newCollectionName);
        String name = inputCollectionName.getText().toString();
        user.addBookCollection(new BookCollection(name));
    }
}
