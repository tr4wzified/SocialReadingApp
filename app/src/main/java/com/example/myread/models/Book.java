package com.example.myread.models;

import java.util.Date;
import java.util.List;

public class Book {
    private int id = 0;
    private final String title;
    private final String author;
    private final String cover;
    private final String description;
    private final List<String> subjects;
    private final Date publishDate;
    private final String authorWiki;
    private final int isbn;
    private final int rating;

    public Book(String title, String author, String cover, String description, List<String> subjects, Date publishDate, String authorWiki, int isbn, int rating) {
        id++;
        this.title = title;
        this.author = author;
        this.cover = cover;
        this.description = description;
        this.subjects = subjects;
        this.publishDate = publishDate;
        this.authorWiki = authorWiki;
        this.isbn = isbn;
        this.rating = rating;
    }
}
