package com.library.management.gui;

import javax.swing.*;
import java.awt.*;

public class TransactionsPage extends JFrame {

    public TransactionsPage() {
        setTitle("Transactions Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Dropdowns for selecting books and members
        JComboBox<String> booksComboBox = new JComboBox<>(new String[]{"Book 1", "Book 2", "Book 3"});
        JComboBox<String> membersComboBox = new JComboBox<>(new String[]{"Member 1", "Member 2", "Member 3"});

        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(e -> borrowBook(booksComboBox, membersComboBox));
        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> returnBook(booksComboBox, membersComboBox));

        JPanel transactionPanel = new JPanel();
        transactionPanel.add(new JLabel("Select Book:"));
        transactionPanel.add(booksComboBox);
        transactionPanel.add(new JLabel("Select Member:"));
        transactionPanel.add(membersComboBox);
        transactionPanel.add(borrowButton);
        transactionPanel.add(returnButton);

        mainPanel.add(transactionPanel, BorderLayout.NORTH);
        add(mainPanel);
    }

    private void borrowBook(JComboBox<String> booksComboBox, JComboBox<String> membersComboBox) {
        String selectedBook = (String) booksComboBox.getSelectedItem();
        String selectedMember = (String) membersComboBox.getSelectedItem();
        // Logic to handle borrowing the book
        JOptionPane.showMessageDialog(this, selectedMember + " borrowed " + selectedBook);
    }

    private void returnBook(JComboBox<String> booksComboBox, JComboBox<String> membersComboBox) {
        String selectedBook = (String) booksComboBox.getSelectedItem();
        String selectedMember = (String) membersComboBox.getSelectedItem();
        // Logic to handle returning the book
        JOptionPane.showMessageDialog(this, selectedMember + " returned " + selectedBook);
    }
}