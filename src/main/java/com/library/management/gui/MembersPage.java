package com.library.management.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MembersPage extends JFrame {

    private JTable membersTable;
    private DefaultTableModel tableModel;

    public MembersPage() {
        setTitle("Members Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Member Name", "Member ID", "Borrowed Books"};
        tableModel = new DefaultTableModel(columnNames, 0);
        membersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(membersTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField borrowedBooksField = new JTextField();

        inputPanel.add(new JLabel("Member Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Member ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Borrowed Books:"));
        inputPanel.add(borrowedBooksField);

        JButton addButton = new JButton("Add Member");
        addButton.addActionListener(e -> addMember(nameField, idField, borrowedBooksField));
        JButton removeButton = new JButton("Remove Member");
        removeButton.addActionListener(e -> removeMember());
        JButton updateButton = new JButton("Update Member");
        updateButton.addActionListener(e -> updateMember(nameField, idField, borrowedBooksField));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void addMember(JTextField nameField, JTextField idField, JTextField borrowedBooksField) {
        String name = nameField.getText();
        String id = idField.getText();
        String borrowedBooks = borrowedBooksField.getText();

        if (!name.isEmpty() && !id.isEmpty()) {
            tableModel.addRow(new Object[]{name, id, borrowedBooks});
            clearFields(nameField, idField, borrowedBooksField);
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeMember() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member to remove", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember(JTextField nameField, JTextField idField, JTextField borrowedBooksField) {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.setValueAt(nameField.getText(), selectedRow, 0);
            tableModel.setValueAt(idField.getText(), selectedRow, 1);
            tableModel.setValueAt(borrowedBooksField.getText(), selectedRow, 2);
            clearFields(nameField, idField, borrowedBooksField);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member to update", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}