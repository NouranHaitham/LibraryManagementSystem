package ui;

import entities.Admin;
import entities.Book;
import entities.RegularUser;
import entities.User;
import storage.LibrarySystem;

import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner = new Scanner(System.in);
    private final LibrarySystem library = LibrarySystem.getInstance();
    private void showWelcomePage() {
        System.out.println("======================================");
        System.out.println("Welcome to the Library System!");
        System.out.println("======================================");
        System.out.println("Please choose an option:");
        System.out.println("1. Login");
        System.out.println("2. Exit");
    }
    public void handleWelcomeMenu() throws Exception {

        while (true) {
            showWelcomePage();

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    return;
                case "2":
                    System.out.println("Goodbye! Thanks for using the Library System.");
                    return;
                default:
                    System.out.println("Invalid input. Please enter 1 or 2");
            }
        }
    }

    private void register() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        String id;
        while (true) {
            System.out.print("Enter ID: ");
            id = scanner.nextLine();
            if (library.hasUser(id)) {
                System.out.println("ID already exists.");
            } else break;
        }

        String isAdmin = "";
        while(!isAdmin.equals("y") && !isAdmin.equals("n")) {
            System.out.print("Admin Role? (y/n): ");
            isAdmin = scanner.nextLine().trim().toLowerCase();
        }

        User newUser;
        if (isAdmin.equals("y")) {
            newUser = new Admin(id, name);
        } else {
            newUser = new RegularUser(id, name);
        }

        library.addUser(newUser);
        System.out.println("Registration successful!");
    }


    private void login() throws Exception {

        User user;
       do {

           System.out.print("Enter your ID: ");
           String id = scanner.nextLine();

           user = library.findUserById(id);
           if (user == null) {
               System.out.println("User not found.");
           }

       }while(user == null);

        System.out.println("Login successful! Welcome, " + user.getName());

        if (user instanceof Admin admin) {
            handleAdminMenu(admin);
        } else if (user instanceof RegularUser regularUser) {
            handleRegularUserMenu(regularUser);
        }
    }


    /// Regular user stuff

    public void handleRegularUserMenu(User user) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String choice;

        do {
            System.out.println("\n--- Regular User Menu ---");
            System.out.println("1. View Book Catalog");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. View My Borrowed Books");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewCatalog();
                case "2" -> borrowBook(user);
                case "3" -> returnBook(user);
                case "4" -> viewBorrowedBooks(user);
                case "0" -> {System.out.println("Logging out..."); handleWelcomeMenu();}
                default -> System.out.println("Invalid option.");
            }
        } while (!choice.equals("0"));
    }

    public void viewCatalog() {
        library.viewBooks();
    }

    public void borrowBook(User user) {
        // ask for bookId, call user.borrowBook(bookId), handle exception
        System.out.print("Enter the Book ID to borrow: ");
        String bookId = scanner.nextLine().trim();

        try {
            user.borrowBook(bookId);
            System.out.println("Book borrowed successfully!");
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
        }

    }

    public void returnBook(User user) {
        // ask for bookId, call user.returnBook(bookId), handle exception
        System.out.print(" Enter the Book ID to return: ");
        String bookId = scanner.nextLine().trim();

        try {
            user.returnBook(bookId);
            System.out.println(" Book returned successfully!");
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
        }

    }

    public void viewBorrowedBooks(User user) {

        System.out.println("\n--- Borrowed Books ---");
        System.out.printf("%-10s | %-25s | %-20s | %-15s \n","ID", "Title", "Author", "Genre");
        System.out.println("-------------------------------------------------------------------------------");
        for (Book b : user.getBorrowedBooks()) {
            System.out.printf("%-10s | %-25s | %-20s | %-15s \n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getGenre());
        }
    }

    /// Admin stuff
    public void handleAdminMenu(Admin admin) throws Exception {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1️.  Add Book");
            System.out.println("2.  Edit Book");
            System.out.println("3.  Delete Book");
            System.out.println("4.  Register New User");
            System.out.println("5.  View All Books");
            System.out.println("6.  View All Users");
            System.out.println("0.  Logout");
            System.out.print(" Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addBook();
                case "2" -> editBook();
                case "3" -> deleteBook();
                case "4" -> register();
                case "5" -> viewCatalog();
                case "6" -> viewUsers();
                case "0" -> {
                    System.out.println(" Logging out...");
                    handleWelcomeMenu();
                    return;
                }
                default -> System.out.println(" Invalid option. Try again.");
            }
        }
    }
    public void viewUsers()
    {
        library.viewUsers();
    }

    private void addBook() throws Exception {
        System.out.println("\n Add New Book:");

        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Author: ");
        String author = scanner.nextLine();

        System.out.print("Enter Genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter Available Copies: ");
        int copies = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Book book = new Book(id, title, author, genre, copies);
        library.addBook(book);
        if(!library.hasBook(book.getID())) System.out.println(" Book added successfully!");
    }

    private void deleteBook() {

        if(library.booksSize() == 0)
        {
            System.out.println("There are no books to delete.");
            return;
        }

        System.out.print("\n🗑 Enter the ID of the book to delete: ");
        String bookId = scanner.nextLine();

        if (library.hasBook(bookId)) {
            library.removeBook(bookId);
            System.out.println(" Book deleted successfully.");
        } else {
            System.out.println(" Book not found or couldn't be deleted.");
        }
    }

    private void editBook() throws Exception {

        if(library.booksSize() == 0)
        {
            System.out.println("There are no books to edit.");
            return;
        }

        System.out.print("\n Enter the ID of the book to edit: ");
        String bookId = scanner.nextLine();

        Book book = LibrarySystem.getInstance().findBookById(bookId);
        if (book == null) {
            System.out.println(" Book not found.");
            return;
        }

        System.out.println("Editing Book: " + book.getTitle());

        System.out.print("Enter new title (or press Enter to keep '" + book.getTitle() + "'): ");
        String title = scanner.nextLine();
        if (!title.isBlank()) book.setTitle(title);

        System.out.print("Enter new author (or press Enter to keep '" + book.getAuthor() + "'): ");
        String author = scanner.nextLine();
        if (!author.isBlank()) book.setAuthor(author);

        System.out.print("Enter new genre (or press Enter to keep '" + book.getGenre() + "'): ");
        String genre = scanner.nextLine();
        if (!genre.isBlank()) book.setGenre(genre);

        System.out.print("Enter new available copies (or -1 to keep '" + book.getAvailableCopies() + "'): ");
        int copies = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (copies != -1) book.setAvailableCopies(copies);


        System.out.println(" Book updated successfully!");
    }

}
