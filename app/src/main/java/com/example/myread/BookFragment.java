package com.example.myread;

import android.content.Context;
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

public class BookFragment extends Fragment {
    private TextView book_title, book_author, book_rating, book_description, book_genre, book_isbn, book_year;
    private final User user = User.getInstance();
    private ImageView book_cover;
    private Book currentBook;
    private final Context context = GlobalApplication.getAppContext();

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
        final Button wikiBtn = rootView.findViewById(R.id.wiki_btn);
        final Button amazonBtn = rootView.findViewById(R.id.amazon_btn);

        wikiBtn.setOnClickListener(v -> openWikiLink());
        amazonBtn.setOnClickListener(v -> openAmazonLink());

        initBook();
        return rootView;
    }

    /**
     * A function that updates the field of a view.
     * @param view to be updated view.
     * @param text a string containing the name of the view.
     * @param text a string containing the current text.
     */
    public void updateField(TextView view, String viewName, String text) {
        if (text == null || text.equals("")) {
            if (viewName.length() > 16) {
                view.setText(context.getString(R.string.unknown, viewName).substring(0, 16).concat("..."));
                return;
            }
            view.setText(context.getString(R.string.unknown, viewName));
            return;
        }
        view.setText(text);
    }

    /**
     * A function that opens a web browser to the wikipedia page of the selected book, if it exists.
     */
    public void openWikiLink() {
        if (currentBook.bookWiki.contains("http")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.bookWiki)));
            return;
        }
        Toast.makeText(getActivity(), context.getString(R.string.no_wikipedia, currentBook.title), Toast.LENGTH_SHORT).show();
    }

    /**
     * A function that opens a web browser to the amazon page of the selected book, if it exists.
     */
    public void openAmazonLink() {
        if (currentBook.amazon.contains("http")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.amazon)));
            return;
        }
        Toast.makeText(getActivity(), context.getString(R.string.no_amazon, currentBook.title), Toast.LENGTH_SHORT).show();
    }

    /**
     * A function that updates all fields in the book fragment.
     */
    public void initBook() {
        // Retrieve Data Saver setting - default is off if not retrievable.
        boolean dataSaver = GlobalFunctions.getEncryptedSharedPreferences().getBoolean("dataSaver", false);
        if (currentBook.largecover.contains("http")) {
            // Load low quality covers when dataSaver is true
            if (dataSaver)
                Picasso.get().load(currentBook.smallcover).into(book_cover);
            else
                Picasso.get().load(currentBook.largecover).into(book_cover);
        }

        updateField(book_title, "title", currentBook.title);
        updateField(book_author, "author", currentBook.author);
        updateField(book_rating, "rating", currentBook.rating);
        updateField(book_description, "description", currentBook.description);
        updateField(book_isbn, "isbn", currentBook.isbn);
        updateField(book_year, "publish date", currentBook.publishDate);
        if (currentBook.subjects.size() != 0) updateField(book_genre, "genre", currentBook.subjects.get(0));
        else book_genre.setText(context.getString(R.string.unknown, "genre"));
    }
}
