package storage;

import entities.*;

import java.sql.*;

public class DatabaseStorage {

    String DB_HOST = System.getenv("DB_HOST");
    String DB_PORT = System.getenv("DB_PORT");
    String DB_NAME = System.getenv("DB_NAME");
    String DB_USER = System.getenv("DB_USER");
    String DB_PASSWORD = System.getenv("DB_PASSWORD");

    String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String CREATE_BOOKS = """
        CREATE TABLE IF NOT EXISTS books (
            id VARCHAR(255) PRIMARY KEY,
            title TEXT,
            author TEXT,
            genre TEXT,
            copies INT
        );
    """;

    private static final String CREATE_USERS = """
        CREATE TABLE IF NOT EXISTS users (
            id VARCHAR(255) PRIMARY KEY,
            name TEXT,
            role TEXT
        );
    """;

    private static final String CREATE_BORROWED = """
        CREATE TABLE IF NOT EXISTS borrowed (
            userId VARCHAR(255),
            bookId VARCHAR(255),
            PRIMARY KEY (userId, bookId),
            FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY (bookId) REFERENCES books(id) ON DELETE CASCADE
        );
    """;

    private static final String CREATE_HISTORY = """
        CREATE TABLE IF NOT EXISTS history (
            userId VARCHAR(255),
            bookId VARCHAR(255),
            PRIMARY KEY (userId, bookId),
            FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY (bookId) REFERENCES books(id) ON DELETE CASCADE
        );
    """;

    public void saveData(LibrarySystem system){

        try (Connection conn = connect()) {
            createTables(conn);
            clearTables(conn);

            saveBooks(conn, system);
            saveUsers(conn, system);
            saveBorrowedAndHistory(conn, system);

            System.out.println("Data saved to database.");
        } catch (Exception e) {
            System.out.println("Failed to save data:");
            e.printStackTrace();
        }
    }

    public void loadData(LibrarySystem system) throws ClassNotFoundException {
//        System.out.println("DB_HOST=" + DB_HOST);
//        System.out.println("DB_PORT=" + DB_PORT);
//        System.out.println("DB_NAME=" + DB_NAME);
//        System.out.println("DB_USER=" + DB_USER);
//        System.out.println("DB_PASSWORD=" + DB_PASSWORD);
//        System.out.println("DB_URL=" + DB_URL);
//        System.out.println(Class.forName("com.mysql.cj.jdbc.Driver"));

        try (Connection conn = connect()) {
            loadBooks(conn, system);
            loadUsers(conn, system);
            loadBorrowedAndHistory(conn, system);

            System.out.println("Data loaded from database.");
        } catch (Exception e) {
            System.out.println("Failed to load data:");
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println("Connecting to DB with URL: " + DB_URL);
        System.out.println("Using user: " + DB_USER);

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_BOOKS);
            stmt.execute(CREATE_USERS);
            stmt.execute(CREATE_BORROWED);
            stmt.execute(CREATE_HISTORY);
        }
    }

    private void clearTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM borrowed;");
            stmt.execute("DELETE FROM history;");
            stmt.execute("DELETE FROM users;");
            stmt.execute("DELETE FROM books;");
        }
    }

    private void saveBooks(Connection conn, LibrarySystem system) throws SQLException {
        String sql = "INSERT INTO books (id, title, author, genre, copies) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Book b : system.getBooks()) {
                ps.setString(1, b.getId());
                ps.setString(2, b.getTitle());
                ps.setString(3, b.getAuthor());
                ps.setString(4, b.getGenre());
                ps.setInt(5, b.getAvailableCopies());
                ps.executeUpdate();
            }
        }
    }

    private void saveUsers(Connection conn, LibrarySystem system) throws SQLException {
        String sql = "INSERT INTO users (id, name, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (User u : system.getUsers()) {
                ps.setString(1, u.getId());
                ps.setString(2, u.getName());
                ps.setString(3, (u instanceof Admin) ? "admin" : "user");
                ps.executeUpdate();
            }
        }
    }

    private void saveBorrowedAndHistory(Connection conn, LibrarySystem system) throws SQLException {
        String borrowSql = "INSERT INTO borrowed (userId, bookId) VALUES (?, ?)";
        String historySql = "INSERT INTO history (userId, bookId) VALUES (?, ?)";

        try (
                PreparedStatement borrowStmt = conn.prepareStatement(borrowSql);
                PreparedStatement historyStmt = conn.prepareStatement(historySql)
        ) {
            for (User u : system.getUsers()) {
                for (Book b : u.getBorrowedBooks()) {
                    borrowStmt.setString(1, u.getId());
                    borrowStmt.setString(2, b.getId());
                    borrowStmt.executeUpdate();
                }
                for (Book b : u.getHistoryBooks()) {
                    historyStmt.setString(1, u.getId());
                    historyStmt.setString(2, b.getId());
                    historyStmt.executeUpdate();
                }
            }
        }
    }

    private void loadBooks(Connection conn, LibrarySystem system) throws Exception {
        String query = "SELECT * FROM books";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
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
        }
    }

    private void loadUsers(Connection conn, LibrarySystem system) throws SQLException {
        String query = "SELECT * FROM users";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String role = rs.getString("role");

                User u = role.equalsIgnoreCase("admin") ? new Admin(id, name) : new RegularUser(id, name);
                system.addUser(u);
            }
        }
    }

    private void loadBorrowedAndHistory(Connection conn, LibrarySystem system) throws SQLException {
        try (
                Statement borrowStmt = conn.createStatement();
                Statement historyStmt = conn.createStatement();
                ResultSet borrows = borrowStmt.executeQuery("SELECT * FROM borrowed");
                ResultSet historys = historyStmt.executeQuery("SELECT * FROM history")
        ) {
            while (borrows.next()) {
                User u = system.findUserById(borrows.getString("userId"));
                Book b = system.findBookById(borrows.getString("bookId"));
                if (u != null && b != null) u.addBorrowedBook(b);
            }

            while (historys.next()) {
                User u = system.findUserById(historys.getString("userId"));
                Book b = system.findBookById(historys.getString("bookId"));
                if (u != null && b != null) u.getHistoryBooks().add(b);
            }
        }
    }
}
