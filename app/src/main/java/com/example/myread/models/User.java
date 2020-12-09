package com.example.myread.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name;
    private final List<BookCollection> collectionList;

    public User(String name) {
        this.name = name;
        this.collectionList = new ArrayList<>();
    }

    public List<BookCollection> getBookCollection() {
        return collectionList;
    }

    public void addBookList(String name) {
        collectionList.add(new BookCollection(name));
    }

    public void addBookList(BookCollection bookCollection) {
        collectionList.add(bookCollection);
    }
}
