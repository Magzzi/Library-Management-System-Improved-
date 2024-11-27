package com.library.management.gui;
import com.library.management.database.*;
import com.library.management.classes.Member;
import com.library.management.classes.User;

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

public class MembersPage extends LibraryDashboard {
    // Static Attributes
    private static final Color THEME_COLOR = new Color(60, 106, 117);
    private static final Color DARKER_THEME_COLOR = Color.BLACK;
    private static final Color TABLE_TEXT_COLOR = Color.WHITE;
    private static final Color TABLE_HEADER_COLOR = new Color(60, 106, 117);
    private static final Color TABLE_BACKGROUND_COLOR = new Color(60, 106, 117);

    // Attributes
    private JTable membersTable;
    private DefaultTableModel tableModel;
    private List<Member> memberList;
    private List<JTextField> inputFields;

    // Constructor
    public MembersPage(User user) {
        super(user, null);
        memberList = new ArrayList<>();
        setTitle("Members Management");
        setupUI();
        loadMembersFromDatabase(); // Load members from the database
        setVisible(true);
    }

    // GUI SET UP METHODS
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Member Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        membersTable = createMembersTable();

        // Create a JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(membersTable);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        scrollPane.setMaximumSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.5), Integer.MAX_VALUE));

        // Create a panel to add padding around the JScrollPane
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 10, 50));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Create buttons to add, remove, and update members
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    // Create members table
    private JTable createMembersTable() {
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

    // Create Custom Button Panel
    private JPanel createButtonPanel() {
        JButton addButton = new JButton("Add Member");
        JButton removeButton = new JButton("Remove Member");
        JButton updateButton = new JButton("Update Member");

        // Set common properties for all buttons
        for (JButton button : new JButton[]{addButton, removeButton, updateButton}) {
            button.setBackground(THEME_COLOR);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new Dimension(120, 40));
        }

        // Add action listeners
        addButton.addActionListener(e -> showMemberInputDialog("Add", null, THEME_COLOR, DARKER_THEME_COLOR));
        removeButton.addActionListener(e -> removeMember());
        updateButton.addActionListener(e -> {
            int selectedRow = membersTable.getSelectedRow();
            if (selectedRow != -1) {
                Member selectedMember = memberList.get(selectedRow);
                showMemberInputDialog("Update", selectedMember, THEME_COLOR, DARKER_THEME_COLOR);
            } else {
                showError("Please select a member to update");
            }
        });

        // Create button panel and add buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        // Add padding to the bottom of the button panel
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0)); // 10px padding at the bottom

        return buttonPanel;
    }

    // Input dialog for inserting and updating member values
    private void showMemberInputDialog(String action, Member member, Color themeColor, Color darkerThemeColor) {
        JDialog dialog = new JDialog(this, action + " Member", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        inputFields = new ArrayList<>();
        String[] labels = {"Member Name:"};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            dialog.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            JTextField textField = new JTextField(20);
            if (member != null) {
                textField.setText(member.getName());
            }
            inputFields.add(textField);
            textField.setBackground(Color.WHITE);
            textField.setForeground(darkerThemeColor);
            dialog.add(textField, gbc);
        }

        JButton confirmButton = new JButton(action);
        confirmButton.addActionListener(e -> {
            if (validateInputs()) {
                if (action.equals("Add")) {
                    addMember();
                } else {
                    updateMember(member);
                }
                dialog.dispose();
            }
        });
        confirmButton.setBackground(THEME_COLOR);
        confirmButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        dialog.add(confirmButton, gbc);

        dialog.setBackground(themeColor);
        dialog.setForeground(darkerThemeColor);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Input Validation Methods
    private boolean validateInputs() {
        String name = inputFields.get(0).getText();
        if (name.isEmpty()) {
            showError("Please fill in all fields correctly");
            return false;
        }
        return true;
    }

    // Database Methods (if applicable)
    private void loadMembersFromDatabase() {
    String sql = "SELECT member_name FROM members"; // Adjust the SQL query as per your database schema
    try (Connection conn = databaseConnection.getConnection(); // Assuming you have a method to get a DB connection
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String memberName = rs.getString("member_name");
            Member member = new Member(memberName); // Assuming Member has a constructor that accepts name
            memberList.add(member);
            tableModel.addRow(new Object[]{memberName}); // Add member name to table model
        }
    } catch (SQLException e) {
        showError("Error loading members from database: " + e.getMessage());
    }
}

    // Add member to the table
    private void addMember() {
        if (validateInputs()) {
            String name = inputFields.get(0).getText();
            Member newMember = new Member(name);
            memberList.add(newMember);
            tableModel.addRow(new Object[]{name});
            insertMemberIntoDatabase(newMember);
            clearFields();
        }
    }

    // Update member in the table
    private void updateMember(Member member) {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = inputFields.get(0).getText();
            memberList.get(selectedRow).setName(name); // Assuming setName method exists in Member class
            tableModel.setValueAt(name, selectedRow, 0);
            clearFields();
        } else {
            showError("Please select a member to update");
        }
    }

    // Remove member from the table
    private void removeMember() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow != -1) {
            memberList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            showError("Please select a member to remove");
        }
    }

    // Database Methods
    private void insertMemberIntoDatabase(Member member) {
    String sql = "INSERT INTO members (member_name) VALUES (?)";
    try (Connection conn = databaseConnection.getConnection(); // Assuming you have a method to get a DB connection
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, member.getName());
        pstmt.executeUpdate();
    } catch (SQLException e) {
        showError("Error adding member to database: " + e.getMessage());
    }
}

    // Utility Methods
    private void clearFields() {
        for (JTextField field : inputFields) {
            field.setText("");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}