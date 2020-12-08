package com.example.myread.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookCollection {
    private String name;
    private int id = 0;
    private List<Book> bookList;

    public BookCollection(String name) {
        this.name = name;
        id++;
        this.bookList = new ArrayList<>();
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void addBook(Book book) {
        this.bookList.add(book);
    }

    public void addBook(String title, String author, String cover, String description, List<String> subjects, Date publishDate, String authorWiki, int isbn, int rating) {
        this.bookList.add(new Book(title, author, cover, description, subjects, publishDate, authorWiki, isbn, rating));
    }
}
