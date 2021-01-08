package com.example.myread.models;

import com.example.myread.ServerConnect;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static User u = null;
    public String name;
    private final List<BookCollection> collectionList;
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
        for (Book book : bc) {
            if (book.title.equals(bTitle)) return book;
        }
        return null;
    }

    public void initBookCollection(BookCollection bookCollection) {
        collectionList.add(bookCollection);
    }

    public boolean addBookCollection(BookCollection bookCollection) {
        ServerConnect.Response r = ServerConnect.getInstance().addBookCollectionServer(name, bookCollection.name);
        if (r.successful) {
            collectionList.add(bookCollection);
            return true;
        }
        System.out.println("Adding book collection failed");
        return false;
//      ServerConnect.postBookCollection(name, formBody);
    }

    public void destroy() {
        name = "";
        collectionList.clear();
        tempBook = null;
    }

    public void deleteBookCollection(BookCollection bc) {
        ServerConnect.Response r = ServerConnect.getInstance().deleteBookCollectionServer(name, bc.name);
        if (r.successful) {
            collectionList.remove(bc);
            return;
        }
        System.out.println("Removing book collection failed");
    }
}
