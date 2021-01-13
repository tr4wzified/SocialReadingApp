package com.example.myread.models;

import com.example.myread.ServerConnect;
import com.example.myread.adapters.BookListItemAdapter;
import com.example.myread.adapters.CollectionAdapter;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.Lambda;

public class User {
    private static User u = null;
    public String name;
    private final List<BookCollection> collectionList;
    private final List<Book> allBooksList;
    private Book tempBook;
    private String tempTitle;

    private User() {
        this.collectionList = new ArrayList<>();
        this.allBooksList = new ArrayList<>();
    }

    /**
     * A function that creates a user instance if none exists and will return the user instance if one already exists.
     * This way only one user instance can be made.
     * @return the user instance.
     */
    public static User getInstance() {
        if (u == null)
            u = new User();
        return u;
    }
    public List<Book> getAllBooksList() {
        return allBooksList;
    }

    public void initAllBooksList() {
        for (BookCollection bc : collectionList) {
            allBooksList.addAll(bc.getBookList());
        }
    }

    public void addBook(Book book) {
        if (!allBooksList.contains(book))
            allBooksList.add(book);
    }

    /**
     * A function that sets the temporary title.
     * @param tempTitle title string
     */
    public void setTempTitle(String tempTitle) {
        this.tempTitle = tempTitle;
    }

    /**
     * A function that gets the temporary title.
     * @return temporary title
     */
    public String getTempTitle() { return tempTitle; }

    /**
     * A function that sets the currently selected book.
     * @param book a book.
     */
    public void setTempBook(Book book) {
        this.tempBook = book;
    }

    /**
     * A function that returns the currently selected book.
     * @return a book.
     */
    public Book getTempBook() {
        return tempBook;
    }

    /**
     * A function that returns the collection list.
     * @return the collection list.
     */
    public List<BookCollection> getCollectionList() {
        return collectionList;
    }

    /**
     * A function that returns a book collection in the collection list by index.
     * @param index index of the book collection.
     * @return a book collection.
     */
    public BookCollection getBookCollection(int index) {
        return collectionList.get(index);
    }

    /**
     * A function that returns a book collection in the collection list by object.
     * @param bookCollection a book collection object.
     * @return a book collection.
     */
    public BookCollection getBookCollection(BookCollection bookCollection) {
        for (BookCollection bc : collectionList)
            if (bc.equals(bookCollection)) return bc;

        return null;
    }

    /**
     * A function that returns a list of books in the collection by name.
     * @param name name of the book collection.
     * @return list of books.
     */
    public List<Book> getBookCollection(String name) {
        for (BookCollection bc : collectionList)
            if (bc.name.equals(name)) return bc.getBookList();

        return null;
    }

    /**
     * A function that returns a book collection in the collection by name.
     * @param name name of the book collection.
     * @return a book collection.
     */
    public BookCollection getBookCollectionByName(String name) {
        for (BookCollection bc : collectionList)
            if (bc.name.equals(name)) return bc;

        return null;
    }

    public Book getBook(String cTitle, String bTitle) {
        for (Book book : getBookCollection(cTitle))
            if (book.title.equals(bTitle)) return book;

        return null;
    }

    /**
     * A function that adds a book collection to the collection list.
     * @param bookCollection a book collection.
     */
    public void initBookCollection(BookCollection bookCollection) {
        collectionList.add(bookCollection);
    }

    /**
     * A function that adds a book collection to the server and in the app.
     * @param bookCollection a book collection.
     * @return true or false based on the success of the request.
     */
    public boolean addBookCollection(BookCollection bookCollection) {
        if (ServerConnect.getInstance().addBookCollectionServer(name, bookCollection.name).successful) {
            collectionList.add(bookCollection);
            return true;
        }
        System.out.println("Adding book collection failed");
        return false;
    }

    /**
     * A function that renames an existing book collection on the server and in the app.
     * @param bookCollection a book collection.
     * @return true or false based on the success of the request.
     */
    public boolean renameBookCollection(BookCollection bookCollection, String newName) {
        if (ServerConnect.getInstance().renameBookCollectionServer(name, bookCollection.name, newName).successful) {
            int i = 0;
            for (BookCollection bc : collectionList) {
                if (bc.equals(bookCollection)) { break; };
                i++;
            }
            collectionList.get(i).name = newName;
            return true;
        }
        System.out.println("Renaming book collection failed");
        return false;
    }

    /**
     * A function that cleans the user.
     */
    public void destroy() {
        name = "";
        collectionList.clear();
        tempBook = null;
        tempTitle = null;
        allBooksList.clear();
    }

    /**
     * A function that deletes a book collection from the server and the collection list.
     * @param bc a book collection.
     */
    public void deleteBookCollection(BookCollection bc) {
        if (ServerConnect.getInstance().deleteBookCollectionServer(name, bc.name).successful)
            collectionList.remove(bc);
        else
            System.out.println("Removing book collection failed");
    }
}
