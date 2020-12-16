package com.example.myread.models;

import com.example.myread.ServerConnect;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class User {
    private static User u = null;
    public String name;
    private List<BookCollection> collectionList;

    private User() {
        this.collectionList = new ArrayList<>();
    }

    public static User getInstance() {
        if (u == null)
            u = new User();
        return u;
    }

    public List<BookCollection> getCollectionList() {
        return collectionList;
    }

    public BookCollection getBookCollection(int index) {
        return collectionList.get(index);
    }

    public List<Book> getBookCollection(String name) {
        for (BookCollection bc : collectionList) {
            if (bc.name.equals(name)) return bc.getBookList();
        }
        return null;
    }

    public Book getBook(String cTitle, String bTitle) {
        List<Book> bc = getBookCollection(cTitle);
        for (Book book: bc) {
            if (book.title.equals(bTitle)) return book;
        }
        return null;
    }

    public void addBookList(String name) {
        collectionList.add(new BookCollection(name));
    }

    public void addBookCollection(BookCollection bookCollection) {
        final RequestBody formBody = new FormBody.Builder()
                .add("collection_name", bookCollection.name)
                .build();

        collectionList.add(bookCollection);
        ServerConnect.getInstance().addBookCollectionServer(name, bookCollection.name);
//        ServerConnect.postBookCollection(name, formBody);
    }

    public void deleteBookCollection(BookCollection bc) {
        collectionList.remove(bc);
        ServerConnect.getInstance().deleteBookCollectionServer(name, bc.name);
    }
}
