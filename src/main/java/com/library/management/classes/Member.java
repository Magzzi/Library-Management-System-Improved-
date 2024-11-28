package com.library.management.classes;

import java.util.ArrayList;
import java.util.List;

public class Member extends Person {
    private List<Book> borrowedBooks;

    // Constructor
    public Member(String name) {
        super(name); // Assuming Person has a constructor that accepts name
        this.borrowedBooks = new ArrayList<>();
    }

    // Getter
    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    @Override
    public String toString() {
        return getName() + " (Borrowed Books: " + borrowedBooks.size() + ")";
    }

    // Method to borrow a book
    public void borrowBook(Book book) {
        if (!borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
            System.out.println("Borrowed Book: " + book.getTitle());
        } else {
            System.out.println("You have already borrowed this book.");
        }
    }

    // Method to return a book
    public void returnBook(Book book) {
        if (borrowedBooks.contains(book)) {
            borrowedBooks.remove(book);
            System.out.println("Returned Book: " + book.getTitle());
        } else {
            System.out.println("You have not borrowed this book.");
        }
    }
}