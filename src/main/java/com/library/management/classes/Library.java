package com.library.management.classes;

import com.library.management.database.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Library {
    private List<Book> books;
    private List<Member> members;

    // Constructor
    public Library() { 
        this.books = getAllBooksFromDatabase();
        this.members = getAllMembersFromDatabase();
    }

    // Getters
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }
    
    // Methods to add and remove books
    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            insertBookIntoDatabase(book); // Add this method to insert the book into the database
            System.out.println("Book added to the library.");
        } else {
            System.out.println("This book is already in the library.");
        }
    }

    public void removeBook(Book book) {
        if (books.contains(book)) {
            books.remove(book);
            deleteBookFromDatabase(book); // Add this method to delete the book from the database
            System.out.println("Book removed from the library.");
        } else {
            System.out.println("This book is not in the library.");
        }
    }

    // Methods to add and remove members
    public void addMember(Member member) {
        if (!members.contains(member)) {
            members.add(member);
            insertMemberIntoDatabase(member);
            System.out.println("Member added to the library.");
        } else {
            System.out.println("This member is already registered.");
        }
    }

    public void removeMember(Member member) {
        if (members.contains(member)) {
            members.remove(member);
            deleteMemberFromDatabase(member);
            System.out.println("Member removed from the library.");
        } else {
            System.out.println("This member is not registered in the library.");
        }
    }

    // Borrow a book for a member
    public boolean borrowBook(Member member, Book book) {
        if (books.contains(book) && book.getAvailableCopies() > 0) {
            member.borrowBook(book);
            book.borrowBook();
            insertBorrowingRecordInDatabase(member, book);
            return true; // Successful borrowing
        } else {
            return false; // Book is not available for borrowing
        }
    }

    // Return a book for a member
    public boolean returnBook(Member member, Book book) {
        if (member.getBorrowedBooks().contains(book)) {
            member.returnBook(book);
            book.returnBook();
            deleteBorrowingRecordFromDatabase(member, book);
            return true; // Successful return
        } else {
            return false; // This book was not borrowed by the member
        }
    }

    private List<Book> getAllBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                String isbn = rs.getString("ISBN");
                String publicationDate = rs.getString("publication_date");
                int availableCopies = rs.getInt("available_copies");
                int authorId = rs.getInt("author_id");
    
                // Get author's name from the Authors table
                String authorName = getAuthorNameFromDatabase(authorId);
    
                Author author = new Author(authorName);
                Book book = new Book(title, author, isbn, publicationDate, availableCopies);
                book.setBookId(bookId); // Assuming setBookId method exists in Book class
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error getting books from database: " + e.getMessage());
        }
        return books;
    }

    private List<Member> getAllMembersFromDatabase() {
        List<Member> members = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Members");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("member_name");
                Member member = new Member(name);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error getting members from database: " + e.getMessage());
        }
        return members;
    }

    private String getAuthorNameFromDatabase(int authorId) {
        String authorName = null;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM Authors WHERE author_id = ?")) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    authorName = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting author name from database: " + e.getMessage());
        }
        return authorName;
    }

    private void insertMemberIntoDatabase(Member member) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Members (member_name) VALUES (?)")) {
            stmt.setString(1, member.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting member into database: " + e.getMessage());
        }
    }

    private void deleteMemberFromDatabase(Member member) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Members WHERE member_name = ?")) {
            stmt.setString(1, member.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting member from database: " + e.getMessage());
        }
    }

    private void insertBorrowingRecordInDatabase(Member member, Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO BorrowingRecords (book_id, member_id, checkout_date, due_date) VALUES (?, (SELECT member_id FROM Members WHERE member_name = ?), ?, ?)")) {
            stmt.setInt(1, book.getBookId());
            stmt.setString(2, member.getName());
            stmt.setString(3, LocalDate.now().toString());
            stmt.setString(4, LocalDate.now().plusWeeks(2).toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting borrowing record: " + e.getMessage());
        }
    }

    private void deleteBorrowingRecordFromDatabase(Member member, Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM BorrowingRecords WHERE book_id = ? AND member_id = (SELECT member_id FROM Members WHERE member_name = ?)")) {
            stmt.setInt(1, book.getBookId());
            stmt.setString(2, member.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting borrowing record: " + e.getMessage());
        }
    }

    private void insertBookIntoDatabase(Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Books (title, ISBN, publication_date, available_copies) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getISBN());
            stmt.setString(3, book.getPublicationDate());
            stmt.setInt(4, book.getAvailableCopies());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting book into database: " + e.getMessage());
        }
    }

    private void deleteBookFromDatabase(Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Books WHERE book_id = ?")) {
            stmt.setInt(1, book.getBookId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting book from database: " + e.getMessage());
        }
    }
}