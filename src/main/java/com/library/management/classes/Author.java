package com.library.management.classes;

import com.library.management.database.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Author extends Person {
    private int authorId;
    private List<Book> books;

    // Constructor with parameters, inheriting from Person Class
    public Author(String name) {
        super(name);
        this.books = new ArrayList<>();
    }

    // Getter for the author's name
    public String getName() {
        return super.getName();
    }

    // Setter for the author's name
    public void setName(String name) {
        super.setName(name);
        updateAuthorNameInDatabase();
    }

    // Getter for the list of books by this author
    public List<Book> getBooks() {
        return books;
    }

    // Add a book to the author's list
    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            insertBookIntoDatabase(book);
        }
    }

    // Remove a book from the author's list
    public void removeBook(Book book) {
        books.remove(book);
        deleteBookFromDatabase(book);
    }

    // Load author details from the database
    public void loadAuthorFromDatabase() {
        String query = "SELECT name FROM authors WHERE author_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                setName(rs.getString("name")); // Set the name using the setter
            }
        } catch (SQLException e) {
            System.err.println("Error loading author from database: " + e.getMessage());
        }
    }

    // Update the author's name in the database
    private void updateAuthorNameInDatabase() {
        String query = "UPDATE authors SET name = ? WHERE author_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, getName());
            stmt.setInt(2, authorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating author name: " + e.getMessage());
        }
    }

    // Insert a book into the database
    private void insertBookIntoDatabase(Book book) {
        String query = "INSERT INTO Books (title, author_id, ISBN, publication_date, available_copies) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, this.authorId);
            stmt.setString(3, book.getISBN());
            stmt.setString(4, book.getPublicationDate());
            stmt.setInt(5, book.getAvailableCopies());
            stmt.executeUpdate();
            
            // Optionally update the sequence after inserting
            updateBookSequence();
        } catch (SQLException e) {
            System.err.println("Error inserting book: " + e.getMessage());
        }
    }

    // Delete a book from the database
    private void deleteBookFromDatabase(Book book) {
        String query = "DELETE FROM Books WHERE book_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, book.getBookId());
            stmt.executeUpdate();
            
            // Optionally update the sequence after deleting a book
            updateBookSequence();
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }

    // Insert the author into the database
    public void insertAuthorIntoDatabase() {
        String query = "INSERT INTO Authors (name) VALUES (?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, getName());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                authorId = rs.getInt(1);  // Get the generated author_id
            }
            
            // Optionally update the sequence after inserting
            updateAuthorSequence();
        } catch (SQLException e) {
            System.err.println("Error inserting author: " + e.getMessage());
        }
    }

    // Update the author sequence (for author_id)
    private void updateAuthorSequence() {
        String query = "UPDATE sqlite_sequence SET seq = (SELECT MAX(author_id) FROM Authors) WHERE name = 'Authors'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating author sequence: " + e.getMessage());
        }
    }

    // Update the book sequence (for book_id)
    private void updateBookSequence() {
        String query = "UPDATE sqlite_sequence SET seq = (SELECT MAX(book_id) FROM Books) WHERE name = 'Books'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book sequence: " + e.getMessage());
        }
    }

    // Delete an author from the database
    public void deleteAuthorFromDatabase() {
        String deleteAuthorQuery = "DELETE FROM Authors WHERE author_id = ?";
        String resetSequenceQuery = "UPDATE sqlite_sequence SET seq = (SELECT MAX(author_id) FROM Authors) WHERE name = 'Authors'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteAuthorQuery);
             PreparedStatement resetStmt = conn.prepareStatement(resetSequenceQuery)) {
            deleteStmt.setInt(1, authorId);
            deleteStmt.executeUpdate();
            resetStmt.executeUpdate();  // Reset the author sequence
        } catch (SQLException e) {
            System.err.println("Error deleting author: " + e.getMessage());
        }
    }
}
