package com.library.management.gui;

import com.library.management.classes.Book;
import com.library.management.classes.Member;
import com.library.management.classes.User;
import com.library.management.classes.Library;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionsPage extends LibraryDashboard {
    // Static Attributes
    private static final Color THEME_COLOR = new Color(60, 106, 117);
    private static final Color DARKER_THEME_COLOR = Color.BLACK;
    private static final Color TABLE_TEXT_COLOR = Color.WHITE;
    private static final Color TABLE_HEADER_COLOR = new Color(60, 106, 117);
    private static final Color TABLE_BACKGROUND_COLOR = new Color(60, 106, 117);

    // Attributes
    private JComboBox<Book> booksComboBox;
    private JComboBox<Member> membersComboBox;
    private Library library;
    private DefaultTableModel tableModel;
    private JTable transactionsTable;
    private List<Book> bookList;
    private List<Member> memberList;

    // Constructor
    public TransactionsPage(User user, Library library) {
        super(user, library);
        this.library = library;
        bookList = new ArrayList<>();
        memberList = new ArrayList<>();
        setTitle("Transactions Management");
        setupUI();
        loadBooksAndMembers(); // Load books and members from the database
        setVisible(true);
    }

    // GUI SET UP METHODS
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create dropdowns for selecting books and members
        booksComboBox = new JComboBox<>();
        membersComboBox = new JComboBox<>();

        // Create buttons for borrowing and returning books
        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(e -> borrowBook());
        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(e -> returnBook());

        // Create transaction panel
        JPanel transactionPanel = new JPanel();
        transactionPanel.add(new JLabel("Select Book:"));
        transactionPanel.add(booksComboBox);
        transactionPanel.add(new JLabel("Select Member:"));
        transactionPanel.add(membersComboBox);
        transactionPanel.add(borrowButton);
        transactionPanel.add(returnButton);

        // Create a table for displaying transactions
        String[] columnNames = {"Transaction", "Member", "Book"};
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionsTable = createTransactionsTable();

        // Create a JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        scrollPane.setMaximumSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.5), Integer.MAX_VALUE));

        // Create a panel to add padding around the JScrollPane
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 10, 50));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(transactionPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Create transactions table
    private JTable createTransactionsTable() {
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(THEME_COLOR);
                } else {
                    c.setBackground(TABLE_BACKGROUND_COLOR);
                }
                c.setForeground(TABLE_TEXT_COLOR);
                return c;
            }
        };

        // Set table color
        table.getTableHeader().setBackground(TABLE_HEADER_COLOR);
        table.getTableHeader().setForeground(TABLE_TEXT_COLOR);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setBorder(new LineBorder(Color.BLACK, 1));
        table.setBackground(TABLE_BACKGROUND_COLOR);
        table.setForeground(TABLE_TEXT_COLOR);

        return table;
    }

    // Load books and members from the database
    private void loadBooksAndMembers() {
        try {
            List<Book> books = library.getAllBooks();
            for (Book book : books) {
 booksComboBox.addItem(book);
                bookList.add(book);
            }

            List<Member> members = library.getAllMembers();
            for (Member member : members) {
                membersComboBox.addItem(member);
                memberList.add(member);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load books and members: " + e.getMessage());
        }
    }

    private void borrowBook() {
        Book selectedBook = (Book) booksComboBox.getSelectedItem();
        Member selectedMember = (Member) membersComboBox.getSelectedItem();

        if (selectedBook != null && selectedMember != null) {
            try {
                if (library.borrowBook(selectedMember, selectedBook)) {
                    tableModel.addRow(new Object[]{"Borrowed", selectedMember.getName(), selectedBook.getTitle()});
                    JOptionPane.showMessageDialog(this, selectedMember.getName() + " borrowed " + selectedBook.getTitle());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to borrow the book. It may not be available.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to borrow the book: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select both a book and a member.");
        }
    }

    private void returnBook() {
        Book selectedBook = (Book) booksComboBox.getSelectedItem();
        Member selectedMember = (Member) membersComboBox.getSelectedItem();

        if (selectedBook != null && selectedMember != null) {
            try {
                if (library.returnBook(selectedMember, selectedBook)) {
                    tableModel.addRow(new Object[]{"Returned", selectedMember.getName(), selectedBook.getTitle()});
                    JOptionPane.showMessageDialog(this, selectedMember.getName() + " returned " + selectedBook.getTitle());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to return the book. It may not be borrowed by the member.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to return the book: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select both a book and a member.");
        }
    }
}