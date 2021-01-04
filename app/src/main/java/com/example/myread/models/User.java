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
    private Book tempBook;

    private User() {
        this.collectionList = new ArrayList<>();
    }

    public static User getInstance() {
        if (u == null)
            u = new User();
        return u;
    }

    public void setTempBook(Book book) {
        this.tempBook = book;
    }

    public Book getTempBook() {
        return tempBook;
    }

    public List<BookCollection> getCollectionList() {
        return collectionList;
    }

    public BookCollection getBookCollection(int index) {
        return collectionList.get(index);
    }

    public BookCollection getBookCollection(BookCollection bookCollection) {
        for (BookCollection bc : collectionList) {
            if (bc.equals(bookCollection)) return bc;
        }
        return null;
    }

    public List<Book> getBookCollection(String name) {
        for (BookCollection bc : collectionList) {
            if (bc.name.equals(name)) return bc.getBookList();
        }
        return null;
    }

    public BookCollection getBookCollectionByName(String name) {
        for (BookCollection bc : collectionList) {
            if (bc.name.equals(name)) return bc;
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
        ServerConnect.Response r = ServerConnect.getInstance().addBookCollectionServer(name, bookCollection.name);
        if (r.successful) {
            collectionList.add(bookCollection);
            System.out.println("Succesfully added book collection");
            return;
        }
        System.out.println("Adding book collection failed");
//      ServerConnect.postBookCollection(name, formBody);
    }

    public void deleteBookCollection(BookCollection bc) {
        ServerConnect.Response r = ServerConnect.getInstance().deleteBookCollectionServer(name, bc.name);
        if (r.successful) {
            collectionList.remove(bc);
            System.out.println("Succesfully removed book collection");
            return;
        }
        System.out.println("Removing book collection failed");
    }
}
