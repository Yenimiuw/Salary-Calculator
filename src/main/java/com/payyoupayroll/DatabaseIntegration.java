package com.payyoupayroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class DatabaseIntegration {

    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        Properties props = loadProperties();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        ensureDriverLoaded();
        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    private static void ensureDriverLoaded() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        File connectorJar = findConnectorJar();
        if (connectorJar == null) {
            throw new SQLException("MySQL JDBC Driver not found. Place the MySQL Connector/J JAR in the project lib folder.");
        }

        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{connectorJar.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(loader);
            Class.forName("com.mysql.cj.jdbc.Driver", true, loader);
        } catch (Exception e) {
            throw new SQLException("Could not load the MySQL JDBC driver from " + connectorJar.getAbsolutePath(), e);
        }
    }

    private static File findConnectorJar() {
        String classPath = System.getProperty("java.class.path");
        if (classPath != null && !classPath.isBlank()) {
            for (String entry : classPath.split(Pattern.quote(File.pathSeparator))) {
                if (entry == null || entry.isBlank()) {
                    continue;
                }
                File candidate = new File(entry);
                if (candidate.isFile() && candidate.getName().startsWith("mysql-connector") && candidate.getName().endsWith(".jar")) {
                    return candidate;
                }
            }
        }

        File currentDir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        File[] candidateDirs = {currentDir, new File(currentDir, "lib"), new File(currentDir, "src"), new File(currentDir.getParentFile(), "lib")};

        for (File dir : candidateDirs) {
            if (dir == null || !dir.exists()) {
                continue;
            }

            File[] files = dir.listFiles();
            if (files == null) {
                continue;
            }

            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("mysql-connector") && file.getName().endsWith(".jar")) {
                    return file;
                }
            }
        }

        File workspaceRoot = currentDir;
        File[] foldersToSearch = {workspaceRoot, new File(workspaceRoot, "lib")};
        for (File folder : foldersToSearch) {
            if (folder == null || !folder.exists()) {
                continue;
            }
            File[] matches = folder.listFiles((dir, name) -> name.startsWith("mysql-connector") && name.endsWith(".jar"));
            if (matches != null && matches.length > 0) {
                return matches[0];
            }
        }

        return null;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        try (var input = DatabaseIntegration.class
                .getClassLoader()
                .getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
                return props;
            }
        } catch (IOException ignored) {}

        try (FileInputStream input = new FileInputStream(DB_PROPERTIES_FILE)) {
            props.load(input);
            return props;
        } catch (IOException ignored) {}

        try (FileInputStream input = new FileInputStream("src/main/resources/" + DB_PROPERTIES_FILE)) {
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not load db.properties. Make sure it exists in the project folder.", e);
        }
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS payroll_system");
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("USE payroll_system");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS hr_accounts (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(100) UNIQUE NOT NULL, password VARCHAR(100) NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee_details (id INT AUTO_INCREMENT PRIMARY KEY, employee_name VARCHAR(150) NOT NULL, employee_number VARCHAR(50) UNIQUE NOT NULL, department VARCHAR(100) NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee_base_pay (id INT AUTO_INCREMENT PRIMARY KEY, employee_number VARCHAR(50) NOT NULL, monthly_pay DOUBLE NOT NULL, daily_rate DOUBLE NOT NULL, hourly_rate DOUBLE NOT NULL, regular_hours DOUBLE NOT NULL, special_hours DOUBLE NOT NULL, holiday_hours DOUBLE NOT NULL, regular_day_pay DOUBLE NOT NULL, special_day_pay DOUBLE NOT NULL, holiday_day_pay DOUBLE NOT NULL, total_hours DOUBLE NOT NULL, total_overtime DOUBLE NOT NULL DEFAULT 0, total_base_pay DOUBLE NOT NULL, working_days_year DOUBLE NOT NULL DEFAULT 261, regular_days DOUBLE NOT NULL DEFAULT 0, special_days DOUBLE NOT NULL DEFAULT 0, holiday_days DOUBLE NOT NULL DEFAULT 0, FOREIGN KEY (employee_number) REFERENCES employee_details(employee_number) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee_deductions (id INT AUTO_INCREMENT PRIMARY KEY, employee_number VARCHAR(50) NOT NULL, sss DOUBLE NOT NULL, philhealth DOUBLE NOT NULL, pagibig DOUBLE NOT NULL, tax DOUBLE NOT NULL, total_deductions DOUBLE NOT NULL, total_salary_this_month DOUBLE NOT NULL, FOREIGN KEY (employee_number) REFERENCES employee_details(employee_number) ON DELETE CASCADE)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee_records_summary (id INT AUTO_INCREMENT PRIMARY KEY, employee_number VARCHAR(50) NOT NULL, employee_name VARCHAR(150) NOT NULL, department VARCHAR(100) NOT NULL, monthly_pay DOUBLE NOT NULL, daily_rate DOUBLE NOT NULL, hourly_rate DOUBLE NOT NULL, regular_hours DOUBLE NOT NULL, special_hours DOUBLE NOT NULL, holiday_hours DOUBLE NOT NULL, regular_day_pay DOUBLE NOT NULL, special_day_pay DOUBLE NOT NULL, holiday_day_pay DOUBLE NOT NULL, total_hours DOUBLE NOT NULL, total_overtime DOUBLE NOT NULL DEFAULT 0, total_base_pay DOUBLE NOT NULL, sss DOUBLE NOT NULL, philhealth DOUBLE NOT NULL, pagibig DOUBLE NOT NULL, tax DOUBLE NOT NULL, total_deductions DOUBLE NOT NULL, total_salary_this_month DOUBLE NOT NULL, working_days_year DOUBLE NOT NULL DEFAULT 261, regular_days DOUBLE NOT NULL DEFAULT 0, special_days DOUBLE NOT NULL DEFAULT 0, holiday_days DOUBLE NOT NULL DEFAULT 0, FOREIGN KEY (employee_number) REFERENCES employee_details(employee_number) ON DELETE CASCADE)");

            addColumnIfNotExists(stmt, "payroll_system.employee_base_pay", "working_days_year", "DOUBLE NOT NULL DEFAULT 261");
            addColumnIfNotExists(stmt, "payroll_system.employee_base_pay", "regular_days", "DOUBLE NOT NULL DEFAULT 0");
            addColumnIfNotExists(stmt, "payroll_system.employee_base_pay", "special_days", "DOUBLE NOT NULL DEFAULT 0");
            addColumnIfNotExists(stmt, "payroll_system.employee_base_pay", "holiday_days", "DOUBLE NOT NULL DEFAULT 0");
            addColumnIfNotExists(stmt, "payroll_system.employee_base_pay", "total_overtime", "DOUBLE NOT NULL DEFAULT 0");

            addColumnIfNotExists(stmt, "payroll_system.employee_records_summary", "working_days_year", "DOUBLE NOT NULL DEFAULT 261");
            addColumnIfNotExists(stmt, "payroll_system.employee_records_summary", "regular_days", "DOUBLE NOT NULL DEFAULT 0");
            addColumnIfNotExists(stmt, "payroll_system.employee_records_summary", "special_days", "DOUBLE NOT NULL DEFAULT 0");
            addColumnIfNotExists(stmt, "payroll_system.employee_records_summary", "holiday_days", "DOUBLE NOT NULL DEFAULT 0");
            addColumnIfNotExists(stmt, "payroll_system.employee_records_summary", "total_overtime", "DOUBLE NOT NULL DEFAULT 0");

            renameColumnIfExists(stmt, "payroll_system.employee_deductions", "total_salary", "total_salary_this_month", "DOUBLE NOT NULL");
            renameColumnIfExists(stmt, "payroll_system.employee_records_summary", "total_salary", "total_salary_this_month", "DOUBLE NOT NULL");
        }
    }

    private static void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String columnType) {
        try {
            stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column") && !e.getSQLState().equals("42S21") && e.getErrorCode() != 1060) {
                System.err.println("Warning: failed to add column " + columnName + " to " + tableName + ": " + e.getMessage());
            }
        }
    }

    private static void renameColumnIfExists(Statement stmt, String tableName, String oldName, String newName, String columnType) {
        try {
            stmt.executeUpdate("ALTER TABLE " + tableName + " CHANGE COLUMN " + oldName + " " + newName + " " + columnType);
        } catch (SQLException e) {
        }
    }

    public static boolean createAccount(String username, String password) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("INSERT INTO payroll_system.hr_accounts (username, password) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean validateAccount(String username, String password) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT id FROM payroll_system.hr_accounts WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean accountExists(String username) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT id FROM payroll_system.hr_accounts WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean addEmployee(String employeeName, String employeeNumber, String department, double monthlyPay, double dailyRate, double hourlyRate,
                                      double regularHours, double specialHours, double holidayHours, double regularDayPay, double specialDayPay, double holidayDayPay,
                                      double totalHours, double totalOvertime, double totalBasePay, double sss, double philhealth, double pagibig, double tax, double totalDeductions, double totalSalaryThisMonth,
                                      double workingDaysYear, double regularDays, double specialDays, double holidayDays) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement detailStmt = conn.prepareStatement("INSERT INTO payroll_system.employee_details (employee_name, employee_number, department) VALUES (?, ?, ?)")) {
                    detailStmt.setString(1, employeeName);
                    detailStmt.setString(2, employeeNumber);
                    detailStmt.setString(3, department);
                    detailStmt.executeUpdate();
                }

                try (PreparedStatement baseStmt = conn.prepareStatement("INSERT INTO payroll_system.employee_base_pay (employee_number, monthly_pay, daily_rate, hourly_rate, regular_hours, special_hours, holiday_hours, regular_day_pay, special_day_pay, holiday_day_pay, total_hours, total_overtime, total_base_pay, working_days_year, regular_days, special_days, holiday_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    baseStmt.setString(1, employeeNumber);
                    baseStmt.setDouble(2, monthlyPay);
                    baseStmt.setDouble(3, dailyRate);
                    baseStmt.setDouble(4, hourlyRate);
                    baseStmt.setDouble(5, regularHours);
                    baseStmt.setDouble(6, specialHours);
                    baseStmt.setDouble(7, holidayHours);
                    baseStmt.setDouble(8, regularDayPay);
                    baseStmt.setDouble(9, specialDayPay);
                    baseStmt.setDouble(10, holidayDayPay);
                    baseStmt.setDouble(11, totalHours);
                    baseStmt.setDouble(12, totalOvertime);
                    baseStmt.setDouble(13, totalBasePay);
                    baseStmt.setDouble(14, workingDaysYear);
                    baseStmt.setDouble(15, regularDays);
                    baseStmt.setDouble(16, specialDays);
                    baseStmt.setDouble(17, holidayDays);
                    baseStmt.executeUpdate();
                }

                try (PreparedStatement deductionStmt = conn.prepareStatement("INSERT INTO payroll_system.employee_deductions (employee_number, sss, philhealth, pagibig, tax, total_deductions, total_salary_this_month) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    deductionStmt.setString(1, employeeNumber);
                    deductionStmt.setDouble(2, sss);
                    deductionStmt.setDouble(3, philhealth);
                    deductionStmt.setDouble(4, pagibig);
                    deductionStmt.setDouble(5, tax);
                    deductionStmt.setDouble(6, totalDeductions);
                    deductionStmt.setDouble(7, totalSalaryThisMonth);
                    deductionStmt.executeUpdate();
                }

                try (PreparedStatement summaryStmt = conn.prepareStatement("INSERT INTO payroll_system.employee_records_summary (employee_number, employee_name, department, monthly_pay, daily_rate, hourly_rate, regular_hours, special_hours, holiday_hours, regular_day_pay, special_day_pay, holiday_day_pay, total_hours, total_overtime, total_base_pay, sss, philhealth, pagibig, tax, total_deductions, total_salary_this_month, working_days_year, regular_days, special_days, holiday_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    summaryStmt.setString(1, employeeNumber);
                    summaryStmt.setString(2, employeeName);
                    summaryStmt.setString(3, department);
                    summaryStmt.setDouble(4, monthlyPay);
                    summaryStmt.setDouble(5, dailyRate);
                    summaryStmt.setDouble(6, hourlyRate);
                    summaryStmt.setDouble(7, regularHours);
                    summaryStmt.setDouble(8, specialHours);
                    summaryStmt.setDouble(9, holidayHours);
                    summaryStmt.setDouble(10, regularDayPay);
                    summaryStmt.setDouble(11, specialDayPay);
                    summaryStmt.setDouble(12, holidayDayPay);
                    summaryStmt.setDouble(13, totalHours);
                    summaryStmt.setDouble(14, totalOvertime);
                    summaryStmt.setDouble(15, totalBasePay);
                    summaryStmt.setDouble(16, sss);
                    summaryStmt.setDouble(17, philhealth);
                    summaryStmt.setDouble(18, pagibig);
                    summaryStmt.setDouble(19, tax);
                    summaryStmt.setDouble(20, totalDeductions);
                    summaryStmt.setDouble(21, totalSalaryThisMonth);
                    summaryStmt.setDouble(22, workingDaysYear);
                    summaryStmt.setDouble(23, regularDays);
                    summaryStmt.setDouble(24, specialDays);
                    summaryStmt.setDouble(25, holidayDays);
                    summaryStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }



    public static List<String[]> getEmployeeSummaryRows() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT employee_name, employee_number, department, monthly_pay, daily_rate, hourly_rate, " +
                     "regular_hours, special_hours, holiday_hours, regular_day_pay, special_day_pay, holiday_day_pay, " +
                     "total_hours, total_overtime, total_base_pay, sss, philhealth, pagibig, tax, total_deductions, " +
                     "total_salary_this_month, working_days_year, regular_days, special_days, holiday_days " +
                     "FROM payroll_system.employee_records_summary ORDER BY employee_name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("employee_name"),
                    rs.getString("employee_number"),
                    rs.getString("department"),
                    String.format("%.2f", rs.getDouble("monthly_pay")),
                    String.format("%.2f", rs.getDouble("daily_rate")),
                    String.format("%.2f", rs.getDouble("hourly_rate")),
                    String.format("%.0f", rs.getDouble("working_days_year")),
                    String.format("%.2f", rs.getDouble("regular_days")),
                    String.format("%.2f", rs.getDouble("regular_hours")),
                    String.format("%.2f", rs.getDouble("regular_day_pay")),
                    String.format("%.2f", rs.getDouble("special_days")),
                    String.format("%.2f", rs.getDouble("special_hours")),
                    String.format("%.2f", rs.getDouble("special_day_pay")),
                    String.format("%.2f", rs.getDouble("holiday_days")),
                    String.format("%.2f", rs.getDouble("holiday_hours")),
                    String.format("%.2f", rs.getDouble("holiday_day_pay")),
                    String.format("%.2f", rs.getDouble("total_hours")),
                    String.format("%.2f", rs.getDouble("total_overtime")),
                    String.format("%.2f", rs.getDouble("total_base_pay")),
                    String.format("%.2f", rs.getDouble("sss")),
                    String.format("%.2f", rs.getDouble("philhealth")),
                    String.format("%.2f", rs.getDouble("pagibig")),
                    String.format("%.2f", rs.getDouble("tax")),
                    String.format("%.2f", rs.getDouble("total_deductions")),
                    String.format("%.2f", rs.getDouble("total_salary_this_month"))
                });
            }
        }
        return rows;
    }

    public static boolean deleteEmployee(String identifier) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM payroll_system.employee_details WHERE employee_number = ? OR employee_name LIKE ?")) {
            ps.setString(1, identifier);
            ps.setString(2, "%" + identifier + "%");
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean employeeExists(String employeeNumber) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM payroll_system.employee_details WHERE employee_number = ?")) {
            ps.setString(1, employeeNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static String[] findEmployeeNameAndNumber(String identifier) throws SQLException {
        String sql = "SELECT employee_name, employee_number FROM payroll_system.employee_details WHERE employee_number = ? OR employee_name LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, "%" + identifier + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("employee_name"), rs.getString("employee_number")};
                }
            }
        }
        return null;
    }

    public static class EmployeeRecord {
        public String name;
        public String number;
        public String department;
        public double monthlyPay;
        public double dailyRate;
        public double hourlyRate;
        public double regularHours;
        public double specialHours;
        public double holidayHours;
        public double regularDayPay;
        public double specialDayPay;
        public double holidayDayPay;
        public double totalHours;
        public double totalOvertime;
        public double totalBasePay;
        public double sss;
        public double philhealth;
        public double pagibig;
        public double tax;
        public double totalDeductions;
        public double totalSalaryThisMonth;
        public double workingDaysYear;
        public double regularDays;
        public double specialDays;
        public double holidayDays;
    }

    public static EmployeeRecord getEmployeeRecord(String identifier) throws SQLException {
        String sql = "SELECT r.employee_name, r.employee_number, r.department, r.monthly_pay, r.daily_rate, r.hourly_rate, " +
                     "r.regular_hours, r.special_hours, r.holiday_hours, r.regular_day_pay, r.special_day_pay, r.holiday_day_pay, " +
                     "r.total_hours, r.total_overtime, r.total_base_pay, r.sss, r.philhealth, r.pagibig, r.tax, r.total_deductions, r.total_salary_this_month, " +
                     "r.working_days_year, r.regular_days, r.special_days, r.holiday_days " +
                     "FROM payroll_system.employee_records_summary r WHERE r.employee_number = ? OR r.employee_name LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, "%" + identifier + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EmployeeRecord rec = new EmployeeRecord();
                    rec.name = rs.getString("employee_name");
                    rec.number = rs.getString("employee_number");
                    rec.department = rs.getString("department");
                    rec.monthlyPay = rs.getDouble("monthly_pay");
                    rec.dailyRate = rs.getDouble("daily_rate");
                    rec.hourlyRate = rs.getDouble("hourly_rate");
                    rec.regularHours = rs.getDouble("regular_hours");
                    rec.specialHours = rs.getDouble("special_hours");
                    rec.holidayHours = rs.getDouble("holiday_hours");
                    rec.regularDayPay = rs.getDouble("regular_day_pay");
                    rec.specialDayPay = rs.getDouble("special_day_pay");
                    rec.holidayDayPay = rs.getDouble("holiday_day_pay");
                    rec.totalHours = rs.getDouble("total_hours");
                    rec.totalOvertime = rs.getDouble("total_overtime");
                    rec.totalBasePay = rs.getDouble("total_base_pay");
                    rec.sss = rs.getDouble("sss");
                    rec.philhealth = rs.getDouble("philhealth");
                    rec.pagibig = rs.getDouble("pagibig");
                    rec.tax = rs.getDouble("tax");
                    rec.totalDeductions = rs.getDouble("total_deductions");
                    rec.totalSalaryThisMonth = rs.getDouble("total_salary_this_month");
                    rec.workingDaysYear = rs.getDouble("working_days_year");
                    rec.regularDays = rs.getDouble("regular_days");
                    rec.specialDays = rs.getDouble("special_days");
                    rec.holidayDays = rs.getDouble("holiday_days");
                    return rec;
                }
            }
        }
        return null;
    }

    public static List<EmployeeRecord> getAllEmployeeRecords() throws SQLException {
        List<EmployeeRecord> records = new ArrayList<>();
        String sql = "SELECT employee_name, employee_number, department, monthly_pay, daily_rate, hourly_rate, " +
                     "regular_hours, special_hours, holiday_hours, regular_day_pay, special_day_pay, holiday_day_pay, " +
                     "total_hours, total_overtime, total_base_pay, sss, philhealth, pagibig, tax, total_deductions, total_salary_this_month, " +
                     "working_days_year, regular_days, special_days, holiday_days " +
                     "FROM payroll_system.employee_records_summary ORDER BY employee_name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EmployeeRecord rec = new EmployeeRecord();
                rec.name = rs.getString("employee_name");
                rec.number = rs.getString("employee_number");
                rec.department = rs.getString("department");
                rec.monthlyPay = rs.getDouble("monthly_pay");
                rec.dailyRate = rs.getDouble("daily_rate");
                rec.hourlyRate = rs.getDouble("hourly_rate");
                rec.regularHours = rs.getDouble("regular_hours");
                rec.specialHours = rs.getDouble("special_hours");
                rec.holidayHours = rs.getDouble("holiday_hours");
                rec.regularDayPay = rs.getDouble("regular_day_pay");
                rec.specialDayPay = rs.getDouble("special_day_pay");
                rec.holidayDayPay = rs.getDouble("holiday_day_pay");
                rec.totalHours = rs.getDouble("total_hours");
                rec.totalOvertime = rs.getDouble("total_overtime");
                rec.totalBasePay = rs.getDouble("total_base_pay");
                rec.sss = rs.getDouble("sss");
                rec.philhealth = rs.getDouble("philhealth");
                rec.pagibig = rs.getDouble("pagibig");
                rec.tax = rs.getDouble("tax");
                rec.totalDeductions = rs.getDouble("total_deductions");
                rec.totalSalaryThisMonth = rs.getDouble("total_salary_this_month");
                rec.workingDaysYear = rs.getDouble("working_days_year");
                rec.regularDays = rs.getDouble("regular_days");
                rec.specialDays = rs.getDouble("special_days");
                rec.holidayDays = rs.getDouble("holiday_days");
                records.add(rec);
            }
        }
        return records;
    }

    public static boolean saveEmployeeRecord(EmployeeRecord record, String oldEmployeeNumber) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (Statement checkStmt = conn.createStatement()) {
                checkStmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0");

                try (PreparedStatement ps = conn.prepareStatement("UPDATE payroll_system.employee_details SET employee_name = ?, employee_number = ?, department = ? WHERE employee_number = ?")) {
                    ps.setString(1, record.name);
                    ps.setString(2, record.number);
                    ps.setString(3, record.department);
                    ps.setString(4, oldEmployeeNumber);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("UPDATE payroll_system.employee_base_pay SET employee_number = ?, monthly_pay = ?, daily_rate = ?, hourly_rate = ?, regular_hours = ?, special_hours = ?, holiday_hours = ?, regular_day_pay = ?, special_day_pay = ?, holiday_day_pay = ?, total_hours = ?, total_overtime = ?, total_base_pay = ?, working_days_year = ?, regular_days = ?, special_days = ?, holiday_days = ? WHERE employee_number = ?")) {
                    ps.setString(1, record.number);
                    ps.setDouble(2, record.monthlyPay);
                    ps.setDouble(3, record.dailyRate);
                    ps.setDouble(4, record.hourlyRate);
                    ps.setDouble(5, record.regularHours);
                    ps.setDouble(6, record.specialHours);
                    ps.setDouble(7, record.holidayHours);
                    ps.setDouble(8, record.regularDayPay);
                    ps.setDouble(9, record.specialDayPay);
                    ps.setDouble(10, record.holidayDayPay);
                    ps.setDouble(11, record.totalHours);
                    ps.setDouble(12, record.totalOvertime);
                    ps.setDouble(13, record.totalBasePay);
                    ps.setDouble(14, record.workingDaysYear);
                    ps.setDouble(15, record.regularDays);
                    ps.setDouble(16, record.specialDays);
                    ps.setDouble(17, record.holidayDays);
                    ps.setString(18, oldEmployeeNumber);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("UPDATE payroll_system.employee_deductions SET employee_number = ?, sss = ?, philhealth = ?, pagibig = ?, tax = ?, total_deductions = ?, total_salary_this_month = ? WHERE employee_number = ?")) {
                    ps.setString(1, record.number);
                    ps.setDouble(2, record.sss);
                    ps.setDouble(3, record.philhealth);
                    ps.setDouble(4, record.pagibig);
                    ps.setDouble(5, record.tax);
                    ps.setDouble(6, record.totalDeductions);
                    ps.setDouble(7, record.totalSalaryThisMonth);
                    ps.setString(8, oldEmployeeNumber);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("UPDATE payroll_system.employee_records_summary SET employee_number = ?, employee_name = ?, department = ?, monthly_pay = ?, daily_rate = ?, hourly_rate = ?, regular_hours = ?, special_hours = ?, holiday_hours = ?, regular_day_pay = ?, special_day_pay = ?, holiday_day_pay = ?, total_hours = ?, total_overtime = ?, total_base_pay = ?, sss = ?, philhealth = ?, pagibig = ?, tax = ?, total_deductions = ?, total_salary_this_month = ?, working_days_year = ?, regular_days = ?, special_days = ?, holiday_days = ? WHERE employee_number = ?")) {
                    ps.setString(1, record.number);
                    ps.setString(2, record.name);
                    ps.setString(3, record.department);
                    ps.setDouble(4, record.monthlyPay);
                    ps.setDouble(5, record.dailyRate);
                    ps.setDouble(6, record.hourlyRate);
                    ps.setDouble(7, record.regularHours);
                    ps.setDouble(8, record.specialHours);
                    ps.setDouble(9, record.holidayHours);
                    ps.setDouble(10, record.regularDayPay);
                    ps.setDouble(11, record.specialDayPay);
                    ps.setDouble(12, record.holidayDayPay);
                    ps.setDouble(13, record.totalHours);
                    ps.setDouble(14, record.totalOvertime);
                    ps.setDouble(15, record.totalBasePay);
                    ps.setDouble(16, record.sss);
                    ps.setDouble(17, record.philhealth);
                    ps.setDouble(18, record.pagibig);
                    ps.setDouble(19, record.tax);
                    ps.setDouble(20, record.totalDeductions);
                    ps.setDouble(21, record.totalSalaryThisMonth);
                    ps.setDouble(22, record.workingDaysYear);
                    ps.setDouble(23, record.regularDays);
                    ps.setDouble(24, record.specialDays);
                    ps.setDouble(25, record.holidayDays);
                    ps.setString(26, oldEmployeeNumber);
                    ps.executeUpdate();
                }

                checkStmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static String[][] getEmployeeDetailsAsTable(String identifier) throws SQLException {
        String sql = "SELECT employee_name, employee_number, department, monthly_pay, daily_rate, hourly_rate, " +
                     "regular_days, regular_hours, regular_day_pay, " +
                     "special_days, special_hours, special_day_pay, " +
                     "holiday_days, holiday_hours, holiday_day_pay, " +
                     "working_days_year, total_hours, total_overtime, total_base_pay, " +
                     "sss, philhealth, pagibig, tax, total_deductions, total_salary_this_month " +
                     "FROM payroll_system.employee_records_summary WHERE employee_number = ? OR employee_name LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, "%" + identifier + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[][]{
                        {"Employee Name", rs.getString("employee_name")},
                        {"Employee Number", rs.getString("employee_number")},
                        {"Department", rs.getString("department")},
                        {"Monthly Salary", String.format("₱%.2f", rs.getDouble("monthly_pay"))},
                        {"Working Days / Year", String.format("%.0f", rs.getDouble("working_days_year"))},
                        {"Daily Rate", String.format("₱%.2f", rs.getDouble("daily_rate"))},
                        {"Hourly Rate", String.format("₱%.2f", rs.getDouble("hourly_rate"))},
                        {"Regular Days Worked", String.format("%.2f", rs.getDouble("regular_days"))},
                        {"Regular Hours Worked", String.format("%.2f", rs.getDouble("regular_hours"))},
                        {"Regular Day Pay", String.format("₱%.2f", rs.getDouble("regular_day_pay"))},
                        {"Special Days Worked", String.format("%.2f", rs.getDouble("special_days"))},
                        {"Special Hours Worked", String.format("%.2f", rs.getDouble("special_hours"))},
                        {"Special Day Pay", String.format("₱%.2f", rs.getDouble("special_day_pay"))},
                        {"Holiday Days Worked", String.format("%.2f", rs.getDouble("holiday_days"))},
                        {"Holiday Hours Worked", String.format("%.2f", rs.getDouble("holiday_hours"))},
                        {"Holiday Day Pay", String.format("₱%.2f", rs.getDouble("holiday_day_pay"))},
                        {"Total Days Worked", String.format("%.2f", rs.getDouble("total_hours"))},
                        {"Total Overtime", String.format("₱%.2f", rs.getDouble("total_overtime"))},
                        {"Total Base Pay", String.format("₱%.2f", rs.getDouble("total_base_pay"))},
                        {"SSS Deduction", String.format("₱%.2f", rs.getDouble("sss"))},
                        {"PhilHealth Deduction", String.format("₱%.2f", rs.getDouble("philhealth"))},
                        {"Pag-IBIG Deduction", String.format("₱%.2f", rs.getDouble("pagibig"))},
                        {"Tax Deduction", String.format("₱%.2f", rs.getDouble("tax"))},
                        {"Total Deductions", String.format("₱%.2f", rs.getDouble("total_deductions"))},
                        {"Total Salary this month", String.format("₱%.2f", rs.getDouble("total_salary_this_month"))}
                    };
                }
            }
        }
        return null;
    }
}
