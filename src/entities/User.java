package entities;

import services.SearchService;
import services.Searchable;
import storage.LibrarySystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class User implements Searchable , Borrowable {

    private String id, name;
    private final List<Book> borrowedBooks;
    private final List<Book> historyBooks;
    private final LibrarySystem lib;

    public User(String id, String name)
    {
        this.id = id;
        this.name = name;
        borrowedBooks = new ArrayList<>();
        historyBooks = new ArrayList<>();
        lib = LibrarySystem.getInstance();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", borrowedBooks=" + borrowedBooks +
                ", historyBooks=" + historyBooks +
                '}';
    }

    public String getId() {
        return id;
    }
    @Override
    public String getID() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getHistoryBooks() {
        return historyBooks;
    }

    public void addBorrowedBook(Book book)
    {
        this.borrowedBooks.add(book);
    }
    public void removeBorrowedBook(Book book)
    {
        this.borrowedBooks.remove(book);
        this.historyBooks.add(book);
    }

    public void removeBook(Book book)
    {
        this.borrowedBooks.remove(book);
        this.historyBooks.remove(book);
    }
    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    @Override
    public void borrowBook(String bookId) throws Exception {
        // must not be borrowed before
        // must have copies, so i dec

        Book bookL = lib.findBookById(bookId);

        if(bookL == null)
        {
            throw new Exception("Book doesn't exist.");
        }

        SearchService<Book> borrowService = new SearchService<>(this.borrowedBooks);
        Book bookB = borrowService.searchByID(bookId);

        SearchService<Book> historyService = new SearchService<>(this.historyBooks);
        Book bookH = historyService.searchByID(bookId);

        if(bookB != null)
        {
            throw new Exception("You already borrowed the book.");
        }
        else if(bookH != null)
        {
            throw new Exception("You can't borrow book twice.");
        }
        else if (bookL.getAvailableCopies() <= 0)
        {
            throw new Exception("There are no available copies.");
        }

        addBorrowedBook(bookL);
        bookL.decreaseCopies();
    }

    @Override
    public void returnBook(String bookId) throws Exception {
        // must be in borrowed list
        // must have copies, so i inc

        Book bookL = lib.findBookById(bookId);

        if(bookL == null)
        {
            throw new Exception("Book doesn't exist.");
        }

        SearchService<Book> borrowService = new SearchService<>(this.borrowedBooks);
        Book bookB = borrowService.searchByID(bookId);

        if(bookB == null)
        {
            throw new Exception("You didn't borrowed the book.");
        }

        removeBorrowedBook(bookL);
        bookL.increaseCopies();
    }

}
