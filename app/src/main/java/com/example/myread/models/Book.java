package com.example.myread.models;

import java.util.List;

public class Book {
    public final String id;
    public final String title;
    public final String author;
    public final String largecover;
    public final String smallcover;
    public final String mediumcover;
    public final String description;
    public final List<String> subjects;
    public final String publishDate;
    public final String bookWiki;
    public final String amazon;
    public final String isbn;
    public final String rating;

    public Book(String id, String title, String author, String largecover, String smallcover, String mediumcover, String description, List<String> subjects, String publishDate, String bookWiki, String amazon, String isbn, String rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.largecover = largecover;
        this.smallcover = smallcover;
        this.mediumcover = mediumcover;
        this.description = description;
        this.subjects = subjects;
        this.publishDate = publishDate;
        this.bookWiki = bookWiki;
        this.amazon = amazon;
        this.isbn = isbn;
        this.rating = rating;
    }
}
