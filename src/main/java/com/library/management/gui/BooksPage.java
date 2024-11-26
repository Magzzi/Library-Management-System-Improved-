package com.library.management.gui;
import com.library.management.database.*;
import com.library.management.classes.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BooksPage extends JFrame {
    //Static Attributes
    private static final Color THEME_COLOR = new Color(60, 106, 117); 
    private static final Color DARKER_THEME_COLOR = THEME_COLOR.darker(); 
    private static final Color TEXT_FIELD_COLOR = Color.WHITE; 
    private static final Color TABLE_TEXT_COLOR = Color.WHITE; 
    private static final Color TABLE_HEADER_COLOR = new Color(60, 106, 117); 
    private static final Color TABLE_BACKGROUND_COLOR = new Color(30, 30, 30);

    //Attributes 
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private List<Book> bookList;
    private List<JTextField> inputFields;

    //Constructor
    public BooksPage() {
        bookList = new ArrayList<>();
        setTitle("Books Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
        loadBooksFromDatabase();
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
            try (Connection connection = databaseConnection.getConnection()) {
                // Start a transaction
                connection.setAutoCommit(false);

                // Insert Author first
                int authorId = insertAuthor(connection);

                // Insert Book with the author ID
                Book newBook = createBookFromInputs(authorId);
                insertBookToDatabase(connection, newBook);

                // Commit the transaction
                connection.commit();

                // Add to local list and table model
                bookList.add(newBook);
                tableModel.addRow(new Object[]{
                    newBook.getTitle(), 
                    newBook.getAuthor().getName(), 
                    newBook.getISBN(), 
                    newBook.getPublicationDate(), 
                    newBook.getAvailableCopies()
                });

                clearFields();
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1 && validateInputs()) {
            try (Connection connection = databaseConnection.getConnection()) {
                // Start a transaction
                connection.setAutoCommit(false);

                // Insert or update Author first
                int authorId = insertOrUpdateAuthor(connection);

                // Create updated book with new author ID
                Book updatedBook = createBookFromInputs(authorId);

                // Update book in database
                updateBookInDatabase(connection, updatedBook, selectedRow);

                // Commit the transaction
                connection.commit();

                // Update local list and table model
                bookList.set(selectedRow, updatedBook);
                tableModel.setValueAt(updatedBook.getTitle(), selectedRow, 0);
                tableModel.setValueAt(updatedBook.getAuthor().getName(), selectedRow, 1);
                tableModel.setValueAt(updatedBook.getISBN(), selectedRow, 2);
                tableModel.setValueAt(updatedBook.getPublicationDate(), selectedRow, 3);
                tableModel.setValueAt(updatedBook.getAvailableCopies(), selectedRow, 4);

                clearFields();
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Please select a book to update");
        }
    }

    private void removeBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            try {
                // Get book ID to delete from database
                int bookId = getBookIdFromRow(selectedRow);
                
                try (Connection connection = databaseConnection.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Books WHERE book_id = ?")) {
                    
                    pstmt.setInt(1, bookId);
                    pstmt.executeUpdate();
                }

                // Remove from local list and table model
                bookList.remove(selectedRow);
                tableModel.removeRow(selectedRow);
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Please select a book to remove");
        }
    }

    // New method to insert author into the database
    private int insertAuthor(Connection connection) throws SQLException {
        String authorName = inputFields.get(1).getText();
        String insertAuthorQuery = "INSERT INTO Authors (name) VALUES (?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertAuthorQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, authorName);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating author failed, no ID obtained.");
                }
            }
        }
    }

    // New method to insert or update author
    private int insertOrUpdateAuthor(Connection connection) throws SQLException {
        String authorName = inputFields.get(1).getText();
        String upsertAuthorQuery = "INSERT OR REPLACE INTO Authors (name) VALUES (?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(upsertAuthorQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, authorName);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating/updating author failed, no ID obtained.");
                }
            }
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

    private Book createBookFromInputs(int authorId) {
        String title = inputFields.get(0).getText();
        String authorName = inputFields.get(1).getText();
        int authorAge = Integer.parseInt(inputFields.get(2).getText());
        String authorAddress = inputFields.get(3).getText();
        String isbn = inputFields.get(5).getText();
        String pubDate = inputFields.get(6).getText();
        int availableCopies = Integer.parseInt(inputFields.get(7).getText());

        Author author = new Author(authorName, authorAge, authorAddress, authorId);
        return new Book(title, author, isbn, pubDate, availableCopies);
    }

    // New method to insert book into the database
    private void insertBookToDatabase(Connection connection, Book book) throws SQLException {
        String insertBookQuery = "INSERT INTO Books (title, author_id, ISBN, publication_date, available_copies) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertBookQuery)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getAuthor().getAuthorId());
            pstmt.setString(3, book.getISBN());
            pstmt.setString(4, book.getPublicationDate());
            pstmt.setInt(5, book.getAvailableCopies());
            pstmt.executeUpdate();
        }
    }

    // New method to update book in database
    private void updateBookInDatabase(Connection connection, Book book, int selectedRow) throws SQLException {
        String updateBookQuery = "UPDATE Books SET title = ?, author_id = ?, ISBN = ?, publication_date = ?, available_copies = ? WHERE book_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateBookQuery)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getAuthor().getAuthorId());
            pstmt.setString(3, book.getISBN());
            pstmt.setString(4, book.getPublicationDate());
            pstmt.setInt(5, book.getAvailableCopies());
            
            // Get the book_id of the selected row from the database
            int bookId = getBookIdFromRow(selectedRow);
            pstmt.setInt(6, bookId);
            
            pstmt.executeUpdate();
        }
    }

    // New method to get book_id from the selected row
    private int getBookIdFromRow(int selectedRow) throws SQLException {
        String title = (String) tableModel.getValueAt(selectedRow, 0);
        String isbn = (String) tableModel.getValueAt(selectedRow, 2);
        
        String query = "SELECT book_id FROM Books WHERE title = ? AND ISBN = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, isbn);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("book_id");
                } else {
                    throw new SQLException("Book not found in database");
                }
            }
        }
    }

    private void clearFields() {
        for (JTextField field : inputFields) {
            field.setText("");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void loadBooksFromDatabase() {
        String query = "SELECT b.book_id, b.title, b.author_id, b.ISBN, b.publication_date, b.available_copies, a.name AS author_name " +
                       "FROM Books b " +
                       "JOIN Authors a ON b.author_id = a.author_id";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
    
            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                int authorId = resultSet.getInt("author_id");
                String authorName = resultSet.getString("author_name");
                String isbn = resultSet.getString("ISBN");
                String publicationDate = resultSet.getString("publication_date");
                int availableCopies = resultSet.getInt("available_copies");
    
                // Create Author object with the actual name from the database
                Author author = new Author(authorName, 0, "", authorId); 
    
                // Create a Book object and add it to the list
                Book book = new Book(title, author, isbn, publicationDate, availableCopies);
                bookList.add(book);
                tableModel.addRow(new Object[]{title, authorName, isbn, publicationDate, availableCopies});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load books from the database.");
        }
    }
}