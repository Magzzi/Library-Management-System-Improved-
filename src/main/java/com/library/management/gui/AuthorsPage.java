package com.library.management.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AuthorsPage extends JFrame {

    private JTable authorsTable;
    private DefaultTableModel tableModel;

    public AuthorsPage() {
        setTitle("Authors Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Author Name", "Author ID", "Books"};
        tableModel = new DefaultTableModel(columnNames, 0);
        authorsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(authorsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();

        inputPanel.add(new JLabel("Author Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Author ID:"));
        inputPanel.add(idField);

        JButton addButton = new JButton("Add Author");
        addButton.addActionListener(e -> addAuthor(nameField, idField));
        JButton removeButton = new JButton("Remove Author");
        removeButton.addActionListener(e -> removeAuthor());
        JButton viewBooksButton = new JButton("View Books by Author");
        viewBooksButton.addActionListener(e -> viewBooksByAuthor());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(viewBooksButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void addAuthor(JTextField nameField, JTextField idField) {
        String name = nameField.getText();
        String id = idField.getText();

        if (!name.isEmpty() && !id.isEmpty()) {
            tableModel.addRow(new Object[]{name, id, ""});
            clearFields(nameField, idField);
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeAuthor() {
        int selectedRow = authorsTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an author to remove", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewBooksByAuthor() {
        int selectedRow = authorsTable.getSelectedRow();
        if (selectedRow != -1) {
            String authorName = (String) tableModel.getValueAt(selectedRow, 0);
            // Logic to fetch and display books by the selected author
            JOptionPane.showMessageDialog(this, "Displaying books for author: " + authorName);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an author to view their books", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}