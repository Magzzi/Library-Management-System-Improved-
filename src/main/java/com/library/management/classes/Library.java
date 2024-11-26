package com.library.management.classes;
import com.library.management.database.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private List<Member> members;

    //Constructor
    public Library(){ 
        this.books = getAllBooksFromDatabase();
        this.members = getAllMembersFromDatabase();
    }

    // Getters
    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }
    
     // Methods to add and remove books
     public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
            System.out.println("Book added to the library.");
        } else {
            System.out.println("This book is already in the library.");
        }
    }

    public void removeBook(Book book) {
        if (books.contains(book)) {
            books.remove(book);
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

    private List<Book> getAllBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Books");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String title = rs.getString("title");
                int authorId = rs.getInt("author_id");
                String isbn = rs.getString("ISBN");
                String publicationDate = rs.getString("publication_date");
                int availableCopies = rs.getInt("available_copies");
                Author author = getAuthorFromDatabase(authorId);
                Book book = new Book(title, author, isbn, publicationDate, availableCopies);
                book.bookId = bookId;
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
                int memberId = rs.getInt("member_id");
                String name = rs.getString("member_name");
                Member member = new Member(name, 0, "", memberId);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error getting members from database: " + e.getMessage());
        }
        return members;
    }

    private Author getAuthorFromDatabase(int authorId) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Authors WHERE author_id = ?")) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    return new Author(name, 0, "", authorId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting author from database: " + e.getMessage());
        }
        return null;
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
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Members WHERE member_id = ?")) {
            stmt.setInt(1, member.getMemberId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting member from database: " + e.getMessage());
        }
    }
}
