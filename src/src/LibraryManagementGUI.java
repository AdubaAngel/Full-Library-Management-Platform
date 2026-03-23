import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class LibraryManagementGUI extends JFrame {
    private LibraryRegistry registry;
    private Library currentLibrary;
    private User currentUser;

    // Main panels
    private JTabbedPane tabbedPane;
    private JTextArea outputArea;
    private JTable userTable;
    private JTable bookTable;
    private JTable borrowTable;

    // Input fields
    private JTextField userIdField;
    private JTextField bookIdField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField searchField;
    private JComboBox<UserRole> roleCombo;
    private JComboBox<String> libraryCombo;
    private JPasswordField passwordField;

    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(241, 196, 15);
    private Color dangerColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);

    public LibraryManagementGUI() {
        registry = new LibraryRegistry();

        // Create sample libraries
        registry.registerLibrary("Downtown Library", 1000, 7, 17, 20);
        registry.registerLibrary("University Library", 2000, 5, 15, 25);
        registry.registerLibrary("School Library", 3000, 10, 14, 18);

        currentLibrary = registry.getLibraryById(1);

        setTitle("📚 Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        setupActions();
        refreshDisplays();

        // Login dialog on startup
        showLoginDialog();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        createDashboardPanel();
        createUserManagementPanel();
        createBookManagementPanel();
        createBorrowPanel();
        createReportsPanel();
        createOutputPanel();
    }

    private void createDashboardPanel() {
        JPanel dashboard = new JPanel(new GridBagLayout());
        dashboard.setBackground(bgColor);
        dashboard.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Library selector
        gbc.gridx = 0; gbc.gridy = 0;
        dashboard.add(new JLabel("Select Library:"), gbc);
        gbc.gridx = 1;
        libraryCombo = new JComboBox<>();
        libraryCombo.setPreferredSize(new Dimension(200, 30));
        for (Library lib : registry.getAllLibraries()) {
            libraryCombo.addItem(lib.getName());
        }
        dashboard.add(libraryCombo, gbc);

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setBackground(bgColor);
        statsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        statsPanel.add(createStatCard("📚 Total Books", "0", primaryColor));
        statsPanel.add(createStatCard("👥 Total Users", "0", primaryColor));
        statsPanel.add(createStatCard("📖 Active Loans", "0", warningColor));
        statsPanel.add(createStatCard("⚠️ Overdue Books", "0", dangerColor));
        statsPanel.add(createStatCard("💰 Total Late Fees", "$0.00", primaryColor));
        statsPanel.add(createStatCard("👤 Current User", "Not logged in", successColor));
        statsPanel.add(createStatCard("📋 Borrow History", "0", primaryColor));
        statsPanel.add(createStatCard("⭐ User Status", "N/A", warningColor));

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        dashboard.add(statsPanel, gbc);

        // Quick actions
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        quickActions.setBackground(bgColor);
        quickActions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        quickActions.add(createQuickButton("📖 Borrow Book", successColor));
        quickActions.add(createQuickButton("📥 Return Book", successColor));
        quickActions.add(createQuickButton("👤 Register User", primaryColor));
        quickActions.add(createQuickButton("📚 Add Book", primaryColor));

        gbc.gridx = 0; gbc.gridy = 2;
        dashboard.add(quickActions, gbc);

        tabbedPane.addTab("📊 Dashboard", dashboard);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JButton createQuickButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

    private void createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"ID", "Name", "Email", "Role", "Status", "Infractions", "Books", "Fees"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        userTable = new JTable(model);
        userTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("User Actions"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        emailField = new JTextField(15);
        inputPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        inputPanel.add(phoneField, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3;
        roleCombo = new JComboBox<>(UserRole.values());
        inputPanel.add(roleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        JButton registerBtn = new JButton("Register User");
        registerBtn.setBackground(successColor);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(e -> registerUser());
        inputPanel.add(registerBtn, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("👥 Users", panel);
    }

    private void createBookManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Title", "Author", "ISBN", "Available", "Due Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        bookTable = new JTable(model);
        bookTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Actions"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(15);
        inputPanel.add(titleField, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 3;
        authorField = new JTextField(15);
        inputPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        isbnField = new JTextField(15);
        inputPanel.add(isbnField, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 3;
        yearField = new JTextField(15);
        inputPanel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        JButton addBookBtn = new JButton("Add Book");
        addBookBtn.setBackground(successColor);
        addBookBtn.setForeground(Color.WHITE);
        addBookBtn.addActionListener(e -> addBook());
        inputPanel.add(addBookBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Search Title:"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(15);
        inputPanel.add(searchField, gbc);

        gbc.gridx = 2;
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchBooks());
        inputPanel.add(searchBtn, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("📚 Books", panel);
    }

    private void createBorrowPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel borrowPanel = new JPanel(new GridBagLayout());
        borrowPanel.setBackground(Color.WHITE);
        borrowPanel.setBorder(BorderFactory.createTitledBorder("Borrow Book"));

        GridBagConstraints bgc = new GridBagConstraints();
        bgc.insets = new Insets(5, 5, 5, 5);

        bgc.gridx = 0; bgc.gridy = 0;
        borrowPanel.add(new JLabel("User ID:"), bgc);
        bgc.gridx = 1;
        userIdField = new JTextField(10);
        borrowPanel.add(userIdField, bgc);

        bgc.gridx = 2;
        borrowPanel.add(new JLabel("Book ID:"), bgc);
        bgc.gridx = 3;
        bookIdField = new JTextField(10);
        borrowPanel.add(bookIdField, bgc);

        bgc.gridx = 0; bgc.gridy = 1;
        bgc.gridwidth = 4;
        JButton borrowBtn = new JButton("Borrow Book");
        borrowBtn.setBackground(successColor);
        borrowBtn.setForeground(Color.WHITE);
        borrowBtn.addActionListener(e -> borrowBook());
        borrowPanel.add(borrowBtn, bgc);

        JPanel returnPanel = new JPanel(new GridBagLayout());
        returnPanel.setBackground(Color.WHITE);
        returnPanel.setBorder(BorderFactory.createTitledBorder("Return Book"));

        bgc = new GridBagConstraints();
        bgc.insets = new Insets(5, 5, 5, 5);

        bgc.gridx = 0; bgc.gridy = 0;
        returnPanel.add(new JLabel("User ID:"), bgc);
        bgc.gridx = 1;
        JTextField returnUserIdField = new JTextField(10);
        returnPanel.add(returnUserIdField, bgc);

        bgc.gridx = 2;
        returnPanel.add(new JLabel("Book ID:"), bgc);
        bgc.gridx = 3;
        JTextField returnBookIdField = new JTextField(10);
        returnPanel.add(returnBookIdField, bgc);

        bgc.gridx = 0; bgc.gridy = 1;
        bgc.gridwidth = 4;
        JButton returnBtn = new JButton("Return Book");
        returnBtn.setBackground(warningColor);
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(e -> {
            try {
                int uid = Integer.parseInt(returnUserIdField.getText());
                int bid = Integer.parseInt(returnBookIdField.getText());
                double fee = currentLibrary.returnBook(uid, bid);
                if (fee >= 0) {
                    JOptionPane.showMessageDialog(this, "Book returned. Fee: $" + fee);
                    refreshDisplays();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format!");
            }
        });
        returnPanel.add(returnBtn, bgc);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(borrowPanel, gbc);
        gbc.gridx = 1;
        panel.add(returnPanel, gbc);

        tabbedPane.addTab("📖 Borrow/Return", panel);
    }

    private void createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"Record ID", "Book", "User ID", "Borrow Date", "Due Date", "Return Date", "Fee"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        borrowTable = new JTable(model);
        borrowTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(borrowTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton overdueBtn = new JButton("Show Overdue Books");
        overdueBtn.addActionListener(e -> showOverdue());
        JButton userHistoryBtn = new JButton("Show User History");
        userHistoryBtn.addActionListener(e -> showUserHistory());

        buttonPanel.add(overdueBtn);
        buttonPanel.add(userHistoryBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("📋 Reports", panel);
    }

    private void createOutputPanel() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(800, 150));

        tabbedPane.addTab("📝 Output", scrollPane);
    }

    private void layoutComponents() {
        add(tabbedPane);
    }

    private void setupActions() {
        libraryCombo.addActionListener(e -> {
            String selected = (String) libraryCombo.getSelectedItem();
            if (selected != null) {
                currentLibrary = registry.getLibraryByName(selected);
                refreshDisplays();
                outputArea.append("✓ Switched to: " + selected + "\n");
            }
        });
    }

    private void showLoginDialog() {
        JDialog dialog = new JDialog(this, "Login", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        JTextField loginIdField = new JTextField(10);
        dialog.add(loginIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField loginPassField = new JPasswordField(10);
        dialog.add(loginPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login as Guest");
        loginBtn.addActionListener(e -> {
            currentUser = null;
            dialog.dispose();
            outputArea.append("Logged in as GUEST (limited access)\n");
        });
        dialog.add(loginBtn, gbc);

        dialog.setVisible(true);
    }

    private void registerUser() {
        if (currentLibrary == null) {
            outputArea.append("❌ Please select a library first!\n");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        UserRole role = (UserRole) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            outputArea.append("❌ Please fill all user fields!\n");
            return;
        }

        User user = new User(name, email, phone, role);
        int userId = currentLibrary.registerUser(user);
        outputArea.append("✅ User registered: " + name + " (ID: " + userId + ", Role: " + role + ")\n");
        refreshDisplays();
        clearFields();
    }

    private void addBook() {
        if (currentLibrary == null) {
            outputArea.append("❌ Please select a library first!\n");
            return;
        }

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String yearText = yearField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || yearText.isEmpty()) {
            outputArea.append("❌ Please fill all book fields!\n");
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            Book book = new Book(title, author, isbn, year);
            int bookId = currentLibrary.addBook(book);
            outputArea.append("✅ Book added: \"" + title + "\" (ID: " + bookId + ")\n");
            refreshDisplays();
            clearFields();
        } catch (NumberFormatException e) {
            outputArea.append("❌ Invalid year format!\n");
        }
    }

    private void borrowBook() {
        if (currentLibrary == null) {
            outputArea.append("❌ Please select a library first!\n");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            int bookId = Integer.parseInt(bookIdField.getText().trim());

            BorrowRecord record = currentLibrary.borrowBook(userId, bookId);
            if (record != null) {
                outputArea.append("✅ Book borrowed! Due: " + record.getDueDate() + "\n");
                refreshDisplays();
            } else {
                outputArea.append("❌ Borrowing failed!\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("❌ Invalid ID format!\n");
        }
    }

    private void searchBooks() {
        if (currentLibrary == null) {
            outputArea.append("❌ Please select a library first!\n");
            return;
        }

        String searchTitle = searchField.getText().trim();
        if (searchTitle.isEmpty()) {
            outputArea.append("❌ Please enter a title to search!\n");
            return;
        }

        List<Book> results = currentLibrary.findBooksByTitle(searchTitle);

        if (results.isEmpty()) {
            outputArea.append("📌 No books found matching: \"" + searchTitle + "\"\n");
        } else {
            outputArea.append("📚 Found " + results.size() + " book(s):\n");
            for (Book book : results) {
                outputArea.append("   • " + book.getTitle() + " by " + book.getAuthor() + " - " +
                        (book.isAvailable() ? "Available" : "Checked Out") + "\n");
            }
        }
    }

    private void showOverdue() {
        if (currentLibrary == null) return;

        List<BorrowRecord> overdue = currentLibrary.getOverdueBooks();
        DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
        model.setRowCount(0);

        if (overdue.isEmpty()) {
            outputArea.append("✅ No overdue books!\n");
        } else {
            for (BorrowRecord record : overdue) {
                model.addRow(new Object[]{
                        record.getBookId(),
                        record.getBookName(),
                        record.getUserId(),
                        record.getBorrowDate(),
                        record.getDueDate(),
                        record.getReturnDate() != null ? record.getReturnDate() : "Not returned",
                        record.getLateFee()
                });
            }
            outputArea.append("⚠️ Showing " + overdue.size() + " overdue books\n");
        }
    }

    private void showUserHistory() {
        if (currentLibrary == null) return;

        String userIdStr = JOptionPane.showInputDialog(this, "Enter User ID:");
        if (userIdStr == null) return;

        try {
            int userId = Integer.parseInt(userIdStr);
            List<BorrowRecord> history = currentLibrary.getUserBorrowHistory(userId);
            double totalFees = currentLibrary.getTotalLateFeesForUser(userId);
            User user = currentLibrary.findUserById(userId);

            DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
            model.setRowCount(0);

            if (user != null) {
                outputArea.append("📋 History for " + user.getName() + " (Total fees: $" + totalFees + ")\n");
                for (BorrowRecord record : history) {
                    model.addRow(new Object[]{
                            record.getBookId(),
                            record.getBookName(),
                            record.getUserId(),
                            record.getBorrowDate(),
                            record.getDueDate(),
                            record.getReturnDate() != null ? record.getReturnDate() : "Not returned",
                            record.getLateFee()
                    });
                }
            } else {
                outputArea.append("❌ User ID " + userId + " not found!\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("❌ Invalid User ID format!\n");
        }
    }

    private void refreshDisplays() {
        if (currentLibrary == null) return;

        // Refresh user table
        DefaultTableModel userModel = (DefaultTableModel) userTable.getModel();
        userModel.setRowCount(0);
        for (User user : currentLibrary.getAllUsers()) {
            userModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getUserStatus(),
                    user.getInfractionPoints(),
                    user.getBorrowedBookCount(),
                    user.getPendingLateFees()
            });
        }

        // Refresh book table
        DefaultTableModel bookModel = (DefaultTableModel) bookTable.getModel();
        bookModel.setRowCount(0);
        for (Book book : currentLibrary.getAllBooks()) {
            bookModel.addRow(new Object[]{
                    "?", // Book ID - would need findBookId method
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.isAvailable() ? "Yes" : "No",
                    book.getDueDate() != null ? book.getDueDate() : "N/A"
            });
        }
    }

    private void clearFields() {
        userIdField.setText("");
        bookIdField.setText("");
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        yearField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        searchField.setText("");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LibraryManagementGUI().setVisible(true);
        });
    }
}