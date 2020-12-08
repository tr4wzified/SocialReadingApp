package com.example.myread.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<BookCollection> collectionList;

    public User(String name) {
        this.name = name;
        this.collectionList = new ArrayList<BookCollection>();
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
