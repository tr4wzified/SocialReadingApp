package com.example.myread.models;

import java.util.Date;
import java.util.List;

public class Book {
    private int id = 0;
    public String title;
    public String author;
    private String cover;
    private String description;
    private List<String> subjects;
    private String publishDate;
    private String authorWiki;
    private int isbn;
    private int rating;

    public Book(String title, String author, String cover, String description, List<String> subjects, String publishDate, String authorWiki, int isbn, int rating) {
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
