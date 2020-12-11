package com.example.myread.models;

import java.util.Date;
import java.util.List;

public class Book {
    private int id = 0;
    public String title;
    public String author;
    public String cover;
    public String description;
    public List<String> subjects;
    public String publishDate;
    public String authorWiki;
    public String isbn;
    public String rating;

    public Book(String title, String author, String cover, String description, List<String> subjects, String publishDate, String authorWiki, String isbn, String rating) {
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
