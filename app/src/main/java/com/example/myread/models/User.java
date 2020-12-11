package com.example.myread.models;

import com.example.myread.ServerConnect;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class User {
    public String name;
    private List<BookCollection> collectionList;

    public User(String name) {
        this.name = name;
        this.collectionList = new ArrayList<>();

//        init();
    }

//    private void init() {
//        index();
//    }

//    private void index() {
//        ServerConnect.Response response = ServerConnect.getUser(name);
//        this.name = response.
//    }

    private void store() {

    }

    private void update() {

    }

    public List<BookCollection> getCollectionList() {
        return collectionList;
    }

    public BookCollection getBookCollection(int index) {
        return collectionList.get(index);
    }

    public void addBookList(String name) {
        collectionList.add(new BookCollection(name));
    }

    public void addBookCollection(BookCollection bookCollection) {
        final RequestBody formBody = new FormBody.Builder()
                .add("collection_name", bookCollection.name)
                .build();

        collectionList.add(bookCollection);
        ServerConnect.addBookCollectionServer(name, bookCollection.name);
//        ServerConnect.postBookCollection(name, formBody);
    }
}
