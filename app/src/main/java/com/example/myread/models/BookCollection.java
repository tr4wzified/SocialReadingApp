package com.example.myread.models;

import com.example.myread.ServerConnect;

import java.util.ArrayList;
import java.util.List;

public class BookCollection {
    public String name;
    private final List<Book> bookList;
    private User user = User.getInstance();

    public BookCollection(String name) {
        this.name = name;
        this.bookList = new ArrayList<>();
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void addBook(Book book) {
        this.bookList.add(book);
    }

    public void delete(Book book) {
        bookList.remove(book);
        ServerConnect.getInstance().deleteBookFromCollectionServer(user.name, name, book.id);
    }

    public void addBookToServer(Book book) {
        this.bookList.add(book);
        ServerConnect.getInstance().addBookToCollectionServer(user.name, name, book.id);
    }

    public void addBook(String user_name, String book_id, String title, String author, String cover, String description, List<String> subjects, String publishDate, String authorWiki, String isbn, String rating) {
        this.bookList.add(new Book(book_id, title, author, cover, description, subjects, publishDate, authorWiki, isbn, rating));
//        ServerConnect.addBookToCollectionServer(user_name, name, book_id);
    }

    public int length() {
        return bookList.size();
    }

    public Book at(int number) {
        return bookList.get(number);
    }
}
