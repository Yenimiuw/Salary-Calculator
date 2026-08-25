package com.payyoupayroll;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Exceptions {

    public interface GeneralAction {
        void execute() throws Exception;
    }

    public static void handleDatabaseError(Component parent, SQLException e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void handleDatabaseErrorWithMessage(Component parent, String customMessage, SQLException e) {
        JOptionPane.showMessageDialog(parent, customMessage + ": " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void handleInputError(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void handleUpdateError(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent, "Error updating employee: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean handleDuplicateEmployee(Component parent, Exception ex, String employeeNumber, GeneralAction updateAction) {
        if (ex.getMessage() != null && (ex.getMessage().toLowerCase().contains("duplicate entry") || ex.getMessage().toLowerCase().contains("integrity constraint"))) {
            int choice = JOptionPane.showConfirmDialog(parent, 
                "An employee with number " + employeeNumber + " already exists. Do you want to update their details with these new calculations?", 
                "Duplicate Employee", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    updateAction.execute();
                } catch (Exception updateEx) {
                    handleUpdateError(parent, updateEx);
                }
            }
            return true; 
        }
        return false; 
    }
}

