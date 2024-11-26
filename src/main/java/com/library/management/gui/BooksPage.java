package com.library.management.gui;

import com.library.management.classes.Book;
import com.library.management.classes.Author;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BooksPage extends JFrame {

    private JTable booksTable;
    private DefaultTableModel tableModel;
    private List<Book> bookList; // List to hold Book objects
    private List<JTextField> inputFields; // List to hold input fields

    // Define your theme color
    private static final Color THEME_COLOR = new Color(60, 106, 117); // Example: Steel Blue
    private static final Color DARKER_THEME_COLOR = THEME_COLOR.darker(); // Darker shade of the theme color
    private static final Color TEXT_FIELD_COLOR = Color.WHITE; // Light gray for text fields
    private static final Color TABLE_TEXT_COLOR = Color.WHITE; // White text for table contents
    private static final Color TABLE_HEADER_COLOR = new Color(60, 106, 117); // Dark gray for table header
    private static final Color TABLE_BACKGROUND_COLOR = new Color(30, 30, 30); // Dark background for table

    public BooksPage() {
        bookList = new ArrayList<>(); // Initialize the book list
        setTitle("Books Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Title", "Author", "ISBN", "Publication Date", "Available Copies"};
        tableModel = new DefaultTableModel(columnNames, 0);
        booksTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(THEME_COLOR); // Change background color for selected row
                } else {
                    c.setBackground(TABLE_BACKGROUND_COLOR); // Set default background color for table cells
                }
                c.setForeground(TABLE_TEXT_COLOR); // Set text color for table cells
                return c;
            }
        };

        // Set table header color
        booksTable.getTableHeader().setBackground(TABLE_HEADER_COLOR);
        booksTable.getTableHeader().setForeground(TABLE_TEXT_COLOR);
        booksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Set the border color of the table header
        booksTable.getTableHeader().setBorder(new LineBorder(DARKER_THEME_COLOR, 1));

        // Set the background color of the table
        booksTable.setBackground(TABLE_BACKGROUND_COLOR);
        booksTable.setForeground(TABLE_TEXT_COLOR);

        JScrollPane scrollPane = new JScrollPane(booksTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(8, 2)); // Adjusted for more fields
        inputFields = new ArrayList<>(); // Initialize input fields list

        // Create input fields and add them to the panel and list
        inputFields.add(createInputField(inputPanel, "Title:"));
        inputFields.add(createInputField(inputPanel, "Author Name:"));
        inputFields.add(createInputField(inputPanel, "Author Age:"));
        inputFields.add(createInputField(inputPanel, "Author Address:"));
        inputFields.add(createInputField(inputPanel, "Author ID:"));
        inputFields.add(createInputField(inputPanel, "ISBN:"));
        inputFields.add(createInputField(inputPanel, "Publication Date:"));
        inputFields.add(createInputField(inputPanel, "Available Copies:"));

        JButton addButton = new JButton("Add Book");
        addButton.setBackground(THEME_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addBook());

        JButton removeButton = new JButton("Remove Book");
        removeButton.setBackground(THEME_COLOR);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> removeBook());

        JButton updateButton = new JButton("Update Book ");
        updateButton.setBackground(THEME_COLOR);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> updateBook());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Set background color for input fields
        for (JTextField field : inputFields) {
            field.setBackground(TEXT_FIELD_COLOR);
        }

        add(mainPanel);
    }

    private JTextField createInputField(JPanel panel, String label) {
        panel.add(new JLabel(label));
        JTextField textField = new JTextField();
        textField.setBackground(TEXT_FIELD_COLOR); // Set background color for the text field
        panel.add(textField);
        return textField;
    }

    private void addBook() {
        if (validateInputs()) {
            Book newBook = createBookFromInputs();
            bookList.add(newBook); // Add the Book object to the list
            tableModel.addRow(new Object[]{newBook.getTitle(), newBook.getAuthor().getName(), newBook.getISBN(), newBook.getPublicationDate(), newBook.getAvailableCopies()});
            clearFields();
        }
    }

    private void updateBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1 && validateInputs()) {
            Book updatedBook = createBookFromInputs();
            bookList.set(selectedRow, updatedBook); // Update the Book object in the list
            tableModel.setValueAt(updatedBook.getTitle(), selectedRow, 0);
            tableModel.setValueAt(updatedBook.getAuthor().getName(), selectedRow, 1);
            tableModel.setValueAt(updatedBook.getISBN(), selectedRow, 2);
            tableModel.setValueAt(updatedBook.getPublicationDate(), selectedRow, 3);
            tableModel.setValueAt(updatedBook.getAvailableCopies(), selectedRow, 4);
            clearFields();
        } else {
            showError("Please select a book to update ");
        }
    }

    private void removeBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            bookList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            showError("Please select a book to remove");
        }
    }

    private boolean validateInputs() {
        try {
            String title = inputFields.get(0).getText();
            String authorName = inputFields.get(1).getText();
            int authorAge = Integer.parseInt(inputFields.get(2).getText());
            String authorAddress = inputFields.get(3).getText();
            int authorId = Integer.parseInt(inputFields.get(4).getText());
            String isbn = inputFields.get(5).getText();
            String pubDate = inputFields.get(6).getText();
            int availableCopies = Integer.parseInt(inputFields.get(7).getText());

            if (title.isEmpty() || authorName.isEmpty() || authorAge <= 0 || authorAddress.isEmpty() || authorId <= 0 || isbn.isEmpty() || pubDate.isEmpty() || availableCopies < 0) {
                showError("Please fill in all fields correctly");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for age, author ID, and available copies");
            return false;
        }
    }

    private Book createBookFromInputs() {
        String title = inputFields.get(0).getText();
        String authorName = inputFields.get(1).getText();
        int authorAge = Integer.parseInt(inputFields.get(2).getText());
        String authorAddress = inputFields.get(3).getText();
        int authorId = Integer.parseInt(inputFields.get(4).getText());
        String isbn = inputFields.get(5).getText();
        String pubDate = inputFields.get(6).getText();
        int availableCopies = Integer.parseInt(inputFields.get(7).getText());

        Author author = new Author(authorName, authorAge, authorAddress, authorId);
        return new Book(title, author, isbn, pubDate, availableCopies);
    }

    private void clearFields() {
        for (JTextField field : inputFields) {
            field.setText("");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}