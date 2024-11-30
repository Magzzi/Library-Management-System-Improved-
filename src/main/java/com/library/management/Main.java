package com.library.management;

import javax.swing.SwingUtilities;
import com.library.management.gui.LibraryLogin;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryLogin().setVisible(true); // Pass it to LibraryLogin
        });
    }   
}