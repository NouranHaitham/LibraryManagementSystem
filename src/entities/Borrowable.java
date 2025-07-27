package entities;

public interface Borrowable {
    void borrowBook(String bookId) throws Exception;
    void returnBook(String bookId) throws Exception;
}
