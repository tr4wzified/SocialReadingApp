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

    public List<Book> getBookList() {
        return bookList;
    }

    public void initBook(Book book) {
        this.bookList.add(book);
    }

    public void delete(Book book) {
        if (ServerConnect.getInstance().deleteBookFromCollectionServer(user.name, name, book.id).successful)
            bookList.remove(book);
        else
            System.out.println("Removing book from collection failed");
    }

    public void add(Book book) {
        if (ServerConnect.getInstance().addBookToCollectionServer(user.name, name, book.id).successful)
            this.bookList.add(book);
        else
            System.out.println("Adding book to collection failed");
    }

    public int length() {
        return bookList.size();
    }

}
