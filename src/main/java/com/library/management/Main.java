package com.library.management;
import javax.swing.SwingUtilities;
import com.library.management.gui.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryLogin().setVisible(true));
    }
}