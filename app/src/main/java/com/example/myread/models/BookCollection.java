package com.example.myread.models;

import com.example.myread.ServerConnect;

import java.util.ArrayList;
import java.util.List;

public class BookCollection {
    public String name;
    private final List<Book> bookList;
    private final User user = User.getInstance();

    public BookCollection(String name) {
        this.name = name;
        this.bookList = new ArrayList<>();
    }

    /**
     * A function that gets the list of books in the collection.
     * @return the book list.
     */
    public List<Book> getBookList() {
        return bookList;
    }

    /**
     * A function that adds a book to the book list.
     * @param book a book.
     */
    public void initBook(Book book) {
        this.bookList.add(book);
    }

    /**
     * A function that deletes a book from the server and the book list.
     * @param book a book.
     */
    public void delete(Book book) {
        ServerConnect.Response r = ServerConnect.getInstance().deleteBookFromCollectionServer(user.name, name, book.id);
        if (r.successful) {
            bookList.remove(book);
            return;
        }
        System.out.println("Removing book from collection failed");

    }

    /**
     * A function that adds a book to the server and to the book list.
     * @param book a book.
     */
    public void add(Book book) {
        ServerConnect.Response r = ServerConnect.getInstance().addBookToCollectionServer(user.name, name, book.id);
        if (r.successful) {
            this.bookList.add(book);
            return;
        }
        System.out.println("Adding book to collection failed");
    }

    /**
     * A function that returns the size of the book list.
     * @return size of the book list.
     */
    public int length() {
        return bookList.size();
    }

}
