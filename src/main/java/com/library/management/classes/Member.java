package com.library.management.classes;
import com.library.management.database.databaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member extends Person{
    private int memberID;
    private List<Book> borrowedBooks;

    //Constructor
    public Member(String name, int age, String address, int memberId){
        super(name, age, address);
        this.memberID = memberId;
        this.borrowedBooks = new ArrayList<>();
    }

    //Getter
    public int getMemberId(){
        return memberID;
    }

    public List<Book> getBorrowedBooks(){
        return borrowedBooks;
    }

    //Method
    public void borrowBook(Book book){
        if(!borrowedBooks.contains(book) && book.getAvailableCopies() > 0){
            borrowedBooks.add(book);
            book.borrowBook();
            insertBorrowingRecordInDatabase(book, this);
            System.out.println("Borrowed Book");
        }else{
            System.out.println("No book available");
        }
    }

    public void returnBook(Book book){
        if(borrowedBooks.contains(book)){
            borrowedBooks.remove(book);
            book.returnBook();
            deleteBorrowingRecordFromDatabase(book, this);
            System.out.println("Returned book");
        }else{
            System.out.println("No book in your list");
        }
    }
    
    private void insertBorrowingRecordInDatabase(Book book, Member member) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO BorrowingRecords (book_id, member_id, checkout_date, due_date) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, book.getBookId());
            stmt.setInt(2, member.getMemberId());
            stmt.setString(3, LocalDate.now().toString());
            stmt.setString(4, LocalDate.now().plusWeeks(2).toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting borrowing record: " + e.getMessage());
        }
    }

    private void deleteBorrowingRecordFromDatabase(Book book, Member member) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM BorrowingRecords WHERE book_id = ? AND member_id = ?")) {
            stmt.setInt(1, book.getBookId());
            stmt.setInt(2, member.getMemberId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting borrowing record: " + e.getMessage());
        }
    }
}
