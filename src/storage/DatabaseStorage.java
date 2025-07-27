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
        Statement stmt = conn.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS books (
                id TEXT PRIMARY KEY,
                title TEXT,
                author TEXT,
                genre TEXT,
                copies INTEGER
            );
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                name TEXT,
                role TEXT
            );
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS borrowed (
                userId TEXT,
                bookId TEXT
            );
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS history (
                userId TEXT,
                bookId TEXT
            );
        """);

        stmt.close();
    }

    private void clearTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DELETE FROM books;");
        stmt.execute("DELETE FROM users;");
        stmt.execute("DELETE FROM borrowed;");
        stmt.execute("DELETE FROM history;");
        stmt.close();
    }

    private void saveBooks(Connection conn, LibrarySystem system) throws SQLException {
        String sql = "INSERT INTO books (id, title, author, genre, copies) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        for (Book b : system.getBooks()) {
            pstmt.setString(1, b.getId());
            pstmt.setString(2, b.getTitle());
            pstmt.setString(3, b.getAuthor());
            pstmt.setString(4, b.getGenre());
            pstmt.setInt(5, b.getAvailableCopies());
            pstmt.executeUpdate();
        }
    }

    private void saveUsers(Connection conn, LibrarySystem system) throws SQLException {
        String sql = "INSERT INTO users (id, name, role) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        for (User u : system.getUsers()) {
            pstmt.setString(1, u.getId());
            pstmt.setString(2, u.getName());
            pstmt.setString(3, (u instanceof Admin) ? "admin" : "user");
            pstmt.executeUpdate();
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
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM books");

        while (rs.next()) {
            Book b = new Book(
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getInt("copies")
            );
            system.addBook(b);
        }
        rs.close();
        stmt.close();
    }

    private void loadUsers(Connection conn, LibrarySystem system) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");

        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            String role = rs.getString("role");

            User u = role.equals("admin") ? new Admin(id, name) : new RegularUser(id, name);
            system.addUser(u);
        }

        rs.close();
        stmt.close();
    }

    private void loadBorrowedAndHistory(Connection conn, LibrarySystem system) throws SQLException {
        PreparedStatement borrowedStmt = conn.prepareStatement("SELECT * FROM borrowed");
        PreparedStatement historyStmt = conn.prepareStatement("SELECT * FROM history");

        ResultSet brs = borrowedStmt.executeQuery();
        while (brs.next()) {
            User u = system.findUserById(brs.getString("userId"));
            Book b = system.findBookById(brs.getString("bookId"));
            if (u != null && b != null) u.addBorrowedBook(b); // custom method if needed
        }

        ResultSet hrs = historyStmt.executeQuery();
        while (hrs.next()) {
            User u = system.findUserById(hrs.getString("userId"));
            Book b = system.findBookById(hrs.getString("bookId"));
            if (u != null && b != null) u.getHistoryBooks().add(b);
        }

        brs.close();
        hrs.close();
    }
}
