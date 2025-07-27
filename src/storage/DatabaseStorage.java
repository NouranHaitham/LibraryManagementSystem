package storage;

import entities.*;
import java.sql.*;
import java.util.*;

public class DatabaseStorage {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    public void saveData(LibrarySystem system) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTables(conn);
            clearTables(conn);

            saveBooks(conn, system);
            saveUsers(conn, system);
            saveBorrowedAndHistory(conn, system);

            System.out.println("Data saved to database.");
        } catch (SQLException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    public void loadData(LibrarySystem system) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            loadBooks(conn, system);
            loadUsers(conn, system);
            loadBorrowedAndHistory(conn, system);

            System.out.println("Data loaded from database.");
        } catch (SQLException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables(Connection conn) throws SQLException {
        Statement stat = conn.createStatement();

        stat.execute("""
            CREATE TABLE IF NOT EXISTS books (
                id TEXT PRIMARY KEY,
                title TEXT,
                author TEXT,
                genre TEXT,
                copies INTEGER);
          """);

        stat.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                name TEXT,
                role TEXT );
        """);

        stat.execute("""
            CREATE TABLE IF NOT EXISTS borrowed (
                userId TEXT,
                bookId TEXT);
        """);

        stat.execute("""
            CREATE TABLE IF NOT EXISTS history (
                userId TEXT,
                bookId TEXT);
        """);

        stat.close();
    }

    private void clearTables(Connection conn) throws SQLException {
        Statement stat = conn.createStatement();
        stat.execute("DELETE FROM books;");
        stat.execute("DELETE FROM users;");
        stat.execute("DELETE FROM borrowed;");
        stat.execute("DELETE FROM history;");
        stat.close();
    }

    private void saveBooks(Connection conn, LibrarySystem system) throws SQLException {
        String sql = "INSERT INTO books (id, title, author, genre, copies) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement prepared = conn.prepareStatement(sql);

        for (Book b : system.getBooks()) {
            prepared.setString(1, b.getId());
            prepared.setString(2, b.getTitle());
            prepared.setString(3, b.getAuthor());
            prepared.setString(4, b.getGenre());
            prepared.setInt(5, b.getAvailableCopies());
            prepared.executeUpdate();
        }
    }

    private void saveUsers(Connection conn, LibrarySystem system) throws SQLException {
        String sql = "INSERT INTO users (id, name, role) VALUES (?, ?, ?)";
        PreparedStatement prepared = conn.prepareStatement(sql);

        for (User u : system.getUsers()) {
            prepared.setString(1, u.getId());
            prepared.setString(2, u.getName());
            prepared.setString(3, (u instanceof Admin) ? "admin" : "user");
            prepared.executeUpdate();
        }
    }

    private void saveBorrowedAndHistory(Connection conn, LibrarySystem system) throws SQLException {
        PreparedStatement borrowedStmt = conn.prepareStatement("INSERT INTO borrowed (userId, bookId) VALUES (?, ?)");
        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO history (userId, bookId) VALUES (?, ?)");

        for (User u : system.getUsers()) {
            for (Book b : u.getBorrowedBooks()) {
                borrowedStmt.setString(1, u.getId());
                borrowedStmt.setString(2, b.getId());
                borrowedStmt.executeUpdate();
            }
            for (Book b : u.getHistoryBooks()) {
                historyStmt.setString(1, u.getId());
                historyStmt.setString(2, b.getId());
                historyStmt.executeUpdate();
            }
        }
    }

    private void loadBooks(Connection conn, LibrarySystem system) throws Exception {
        Statement stat = conn.createStatement();
        ResultSet res = stat.executeQuery("SELECT * FROM books");

        while (res.next()) {
            Book b = new Book(
                    res.getString("id"),
                    res.getString("title"),
                    res.getString("author"),
                    res.getString("genre"),
                    res.getInt("copies")
            );
            system.addBook(b);
        }
        res.close();
        stat.close();
    }

    private void loadUsers(Connection conn, LibrarySystem system) throws SQLException {
        Statement stat = conn.createStatement();
        ResultSet res = stat.executeQuery("SELECT * FROM users");

        while (res.next()) {
            String id = res.getString("id");
            String name = res.getString("name");
            String role = res.getString("role");

            User u = role.equals("admin") ? new Admin(id, name) : new RegularUser(id, name);
            system.addUser(u);
        }

        res.close();
        stat.close();
    }

    private void loadBorrowedAndHistory(Connection conn, LibrarySystem system) throws SQLException {
        PreparedStatement borrowedStmt = conn.prepareStatement("SELECT * FROM borrowed");
        PreparedStatement historyStmt = conn.prepareStatement("SELECT * FROM history");

        ResultSet borrows = borrowedStmt.executeQuery();
        while (borrows.next()) {
            User u = system.findUserById(borrows.getString("userId"));
            Book b = system.findBookById(borrows.getString("bookId"));
            if (u != null && b != null) u.addBorrowedBook(b); // custom method if needed
        }

        ResultSet historys = historyStmt.executeQuery();
        while (historys.next()) {
            User u = system.findUserById(historys.getString("userId"));
            Book b = system.findBookById(historys.getString("bookId"));
            if (u != null && b != null) u.getHistoryBooks().add(b);
        }

        borrows.close();
        historys.close();
    }
}
