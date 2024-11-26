package com.library.management.classes;
import com.library.management.database.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Author extends Person{
    private int authorId;
    private List<Book> books;

    //Constructor w/ parameters, inheriting from Person Class
    public Author(String name, int age, String address, int authorId) {
        super(name, age, address);
        this.authorId = authorId;
        this.books = new ArrayList<>();
    }

    //Getter
    public int getAuthorId(){
        return authorId;
    }

    //Methods
    public void addBook(Book book){
        if(!books.contains(book)){
            books.add(book);
        }
    }

    public void removeBook(Book book){
        books.remove(book);
    }

    public List<Book> getBooks(){
        return books;
    }

     private void insertBookIntoDatabase(Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Books (title, author_id, ISBN, publication_date, available_copies) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, this.authorId);
            stmt.setString(3, book.getISBN());
            stmt.setString(4, book.getPublicationDate());
            stmt.setInt(5, book.getAvailableCopies());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting book: " + e.getMessage());
        }
    }

    private void deleteBookFromDatabase(Book book) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Books WHERE book_id = ?")) {
            stmt.setInt(1, book.getBookId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }
}
