package storage;

import entities.Admin;
import entities.Book;
import entities.User;

import java.util.*;

public class LibrarySystem {

    private static final LibrarySystem INSTANCE = new LibrarySystem();

    // Fast lookUps
    private final Map<String, Book> booksById = new HashMap<>();
    private final Map<String, User> usersById = new HashMap<>();

    // Unique Genres
    private final Set<String> genres = new HashSet<>();

    // For generic operations
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    // Singleton
    private LibrarySystem() {}

    public static LibrarySystem getInstance() {
        return INSTANCE;
    }

    public Map<String, Book> getBooksById() {
        return booksById;
    }

    public Map<String, User> getUsersById() {
        return usersById;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<User> getUsers() {
        return users;
    }

    public void viewBooks() {

        System.out.println("\n📚 --- Book Catalog ---");
        System.out.printf("%-10s | %-25s | %-20s | %-15s | %-6s\n",
                "ID", "Title", "Author", "Genre", "Copies");
        System.out.println("-------------------------------------------------------------------------------");

        for (Book b : books) {
            System.out.printf("%-10s | %-25s | %-20s | %-15s | %-6d\n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(), b.getAvailableCopies());
        }
    }

    public void viewUsers()
    {
        System.out.println("\n📚 --- Users Menu ---");
        System.out.printf("%-13s | %-22s | %-5s\n", "ID", "Name", "Role");
        System.out.println("------------------------------------------------");

        for (User user : users) {
            String role = (user instanceof Admin) ? "Admin" : "User";
            System.out.printf("👤 ID: %-10s | Name: %-20s | Role: %-5s\n", user.getId(), user.getName(), role);
        }
    }

    public int usersSize() { return users.size();}
    public int booksSize() { return books.size();}


    public Book findBookById(String id) {
        return booksById.get(id);
    }

    public User findUserById(String id) {
        return usersById.get(id);
    }

    // For books
    public void addBook(Book book) {
        if (!booksById.containsKey(book.getId())) {
            booksById.put(book.getId(), book);
            books.add(book);
            genres.add(book.getGenre());
        }
        else System.out.println("Book exists.");
    }

    public void removeBook(String bookId) {

        if (booksById.containsKey(bookId)) {
            Book book = booksById.get(bookId);

            booksById.remove(book.getId());
            books.remove(book);
            if(books.stream().noneMatch(item -> item.getGenre().equals(book.getGenre())))
            {
                genres.remove(book.getGenre());
            }

            // remove book from regular users and from history
            users.forEach(user -> user.removeBook(book));
            usersById.values().forEach(user -> user.removeBook(book));

        }
        else System.out.println("Book doesn't exists.");
    }

    public boolean hasBook(String bookId) {
        return booksById.containsKey(bookId);
    }

    // For Users
    public void addUser(User user) {
        if (!usersById.containsKey(user.getId())) {
            usersById.put(user.getId(), user);
            users.add(user);
        }
        else System.out.println("User exists.");
    }

    public void removeUser(String userId) {

        if (usersById.containsKey(userId)) {
            User user = usersById.get(userId);
            usersById.remove(user.getId());
            users.remove(user);

            // return the books of that user
            List<Book> borrowed = new ArrayList<>(user.getBorrowedBooks());
            for (Book book : borrowed) {
                try {
                    user.returnBook(book.getID());
                } catch (Exception e) {
                    System.out.println("Failed to return book " + book.getID() + ": " + e.getMessage());
                }
            }

        }
        else System.out.println("User doesn't exists.");
    }

    public boolean hasUser(String userId) {
        return usersById.containsKey(userId);
    }

}
