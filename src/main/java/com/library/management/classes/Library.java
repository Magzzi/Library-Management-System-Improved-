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
    public Library() throws SQLException { 
        try {
            this.books = getAllBooksFromDatabase();
            this.members = getAllMembersFromDatabase();
            System.out.println("Library initialized with " + books.size() + " books and " + members.size() + " members.");
        } catch (SQLException e) {
            System.err.println("Error initializing Library: " + e.getMessage());
            throw e; // Ensure the exception propagates for visibility
        }
    }

    // Getters
    public List<Book> getAllBooks() throws SQLException{
        return new ArrayList<>(books);
    }

    public List<Member> getAllMembers() throws SQLException{
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

    private List<Book> getAllBooksFromDatabase() throws SQLException {
        List<Book> books = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books");
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No books found in the database.");
            }
            while (rs.next()) {
                String title = rs.getString("title");
                String isbn = rs.getString("ISBN");
                String publicationDate = rs.getString("publication_date");
                int availableCopies = rs.getInt("available_copies");
                int authorId = rs.getInt("author_id");
    
                // Debugging output
                System.out.println("Title=" + title + ", Author ID=" + authorId);
    
                // Get author's name from the Authors table
                String authorName = getAuthorNameFromDatabase(authorId);
                if (authorName == null) {
                    System.err.println("Warning: No author found for author_id=" + authorId);
                    continue; // Skip this book if no author is found
                }
    
                Author author = new Author(authorName);
                Book book = new Book(title, author, isbn, publicationDate, availableCopies);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error getting books from database: " + e.getMessage());
            throw e; // Rethrow the exception
        }
        System.out.println("Books retrieved from the library: " + books.size());
        return books;
    }
    
    private List<Member> getAllMembersFromDatabase() throws SQLException {
        List<Member> members = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Members");
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No members found in the database.");
            }
            while (rs.next()) {
                String name = rs.getString("member_name");
                Member member = new Member(name);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error getting members from database: " + e.getMessage());
            throw e; // Rethrow the exception
        }
        System.out.println("Members retrieved from the library: " + members.size());
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
                } else {
                    System.err.println("No author found for author_id=" + authorId);
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
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO BorrowedBooks (book_id, member_id, borrow_date, return_date) VALUES (?, (SELECT member_id FROM members WHERE member_name = ?), ?, ?)")) {
            stmt.setInt(1, book.getBookId()); // Book ID from the Book object
            stmt.setString(2, member.getName()); // Member name to fetch member ID
            stmt.setString(3, LocalDate.now().toString()); // Current date
            stmt.setString(4, LocalDate.now().plusWeeks(2).toString()); // Due date
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Inserted borrowing record, rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error inserting borrowing record: " + e.getMessage());
        }
    }

    private void deleteBorrowingRecordFromDatabase(Member member, Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM BorrowedBooks WHERE book_id = ? AND member_id = (SELECT member_id FROM members WHERE member_name = ?)")) {
            stmt.setInt(1, book.getBookId()); // Book ID from the Book object
            stmt.setString(2, member.getName()); // Member name to fetch member ID
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Deleted borrowing record, rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error deleting borrowing record: " + e.getMessage());
        }
    }
    
    private void insertBookIntoDatabase(Book book) {
        // Check if the book already exists based on ISBN
        if (bookExistsInDatabase(book.getISBN())) {
            System.out.println("Book with ISBN " + book.getISBN() + " already exists. Skipping insertion.");
            return; // Skip insertion if the book already exists
        }
    
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

    private boolean bookExistsInDatabase(String isbn) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Books WHERE ISBN = ?")) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if at least one book exists with this ISBN
            }
        } catch (SQLException e) {
            System.err.println("Error checking if book exists in database: " + e.getMessage());
        }
        return false; // Return false if an error occurs or no book is found
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

    // Method to get all transactions (borrowed books)
public List<Object[]> getAllTransactions() throws SQLException {
    List<Object[]> transactions = new ArrayList<>();
    String query = "SELECT bb.borrow_id, bb.borrow_date, bb.return_date, " +
                   "m.member_name, b.title " +
                   "FROM BorrowedBooks bb " +
                   "JOIN members m ON bb.member_id = m.member_id " +
                   "JOIN Books b ON bb.book_id = b.book_id";

    try (Connection conn = databaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Object[] transaction = new Object[5]; // Adjust size based on required fields
            transaction[0] = rs.getInt("borrow_id"); // Borrow ID
            transaction[1] = rs.getString("member_name"); // Member Name
            transaction[2] = rs.getString("title"); // Book Title
            transaction[3] = rs.getString("borrow_date"); // Borrow Date
            transaction[4] = rs.getString("return_date"); // Return Date
            transactions.add(transaction);
        }
    } catch (SQLException e) {
        System.err.println("Error getting transactions from database: " + e.getMessage());
        throw e; // Rethrow the exception for further handling
    }
    
    return transactions;
}
}