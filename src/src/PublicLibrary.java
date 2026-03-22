import java.time.LocalDate;
import java.util.*;

public class PublicLibrary implements Library {
    private String name;
    private int startId;
    private int idIncrements;
    private int employeeBaseLimit;
    private int userBaseLimit;

    private Map<Integer, Book> books;
    private Map<Integer, User> users;
    private List<BorrowRecord> borrowHistory;
    private List<BorrowRecord> activeLoans;

    private int nextBookId;
    private int nextUserId;
    private Map<UserRole, Integer> maxBooksByRole;
    private Map<UserRole, Integer> loanDaysByRole;
    private Map<UserRole, Double> lateFeeByRole;
    private Map<UserRole, Integer> infractionLimitByRole;

    public PublicLibrary(String name, int startId, int idIncrements,
                         int employeeBaseLimit, int userBaseLimit) {
        this.name = name;
        this.startId = startId;
        this.idIncrements = idIncrements;
        this.employeeBaseLimit = employeeBaseLimit;
        this.userBaseLimit = userBaseLimit;
        this.nextBookId = startId;
        this.nextUserId = startId;

        maxBooksByRole = new HashMap<>();
        loanDaysByRole = new HashMap<>();
        lateFeeByRole = new HashMap<>();
        infractionLimitByRole = new HashMap<>();

        this.books = new HashMap<>();
        this.users = new HashMap<>();
        this.borrowHistory = new ArrayList<>();
        this.activeLoans = new ArrayList<>();

        initializeRoleRules();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int addBook(Book book) {
        int localId = nextBookId;
        books.put(localId, book);
        nextBookId += idIncrements;
        return localId;
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        List<Book> matchingBooks = new ArrayList<>();
        String searchLower = title.toLowerCase();

        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(searchLower)) {
                matchingBooks.add(book);
            }
        }
        return matchingBooks;
    }

    @Override
    public int registerUser(User user) {
        int localUserID = nextUserId;
        user.setId(localUserID);
        users.put(localUserID, user);
        nextUserId += idIncrements;
        return localUserID;
    }

    @Override
    public User findUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public BorrowRecord borrowBook(int userId, int bookId) {
        if (!users.containsKey(userId)) {
            System.out.println("User " + userId + " not found!");
            return null;
        }

        if (!books.containsKey(bookId)) {
            System.out.println("Book " + bookId + " not found!");
            return null;
        }

        User user = users.get(userId);
        Book book = books.get(bookId);

        if (!book.isAvailable()) {
            System.out.println("Book " + bookId + " is not available!");
            return null;
        }

        // Check if user is suspended
        if (user.isCurrentlySuspended()) {
            System.out.println("User " + user.getName() + " is currently suspended until " +
                    user.getSuspensionEndDate());
            return null;
        }

        Integer maxBooks = maxBooksByRole.get(user.getRole());
        if (maxBooks == null) {
            maxBooks = 5; // Default fallback
        }

        if (user.getBorrowedBookCount() >= maxBooks) {
            System.out.println("User has reached maximum books limit of " + maxBooks + "!");
            return null;
        }

        user.addBorrowedBook(bookId);

        Integer loanDays = loanDaysByRole.get(user.getRole());
        if (loanDays == null) {
            loanDays = 14; // Default fallback
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(loanDays);

        BorrowRecord record = new BorrowRecord(
                bookId, userId, book.getTitle(), borrowDate, dueDate
        );

        activeLoans.add(record);
        borrowHistory.add(record);
        book.setAvailable(false);

        return record;
    }

    private void initializeRoleRules(){
        // Borrowing rules
        maxBooksByRole.put(UserRole.PATRON, 7);
        maxBooksByRole.put(UserRole.GUEST, 2);
        maxBooksByRole.put(UserRole.EMPLOYEE, 10);
        maxBooksByRole.put(UserRole.MANAGER, 30);
        maxBooksByRole.put(UserRole.JUNIOR_EMPLOYEE, 8);
        maxBooksByRole.put(UserRole.VOLUNTEER, 12);

        loanDaysByRole.put(UserRole.PATRON, 14);
        loanDaysByRole.put(UserRole.GUEST, 7);
        loanDaysByRole.put(UserRole.EMPLOYEE, 20);
        loanDaysByRole.put(UserRole.MANAGER, 30);
        loanDaysByRole.put(UserRole.JUNIOR_EMPLOYEE, 15);
        loanDaysByRole.put(UserRole.VOLUNTEER, 10);

        lateFeeByRole.put(UserRole.PATRON, 0.50);
        lateFeeByRole.put(UserRole.GUEST, 1.00);
        lateFeeByRole.put(UserRole.EMPLOYEE, 0.25);
        lateFeeByRole.put(UserRole.MANAGER, 1.00);
        lateFeeByRole.put(UserRole.JUNIOR_EMPLOYEE, 0.15);
        lateFeeByRole.put(UserRole.VOLUNTEER, 0.10);

        // Infraction limits (configurable per library)
        infractionLimitByRole.put(UserRole.PATRON, userBaseLimit);
        infractionLimitByRole.put(UserRole.GUEST, userBaseLimit - 5);
        infractionLimitByRole.put(UserRole.EMPLOYEE, employeeBaseLimit);
        infractionLimitByRole.put(UserRole.JUNIOR_EMPLOYEE, employeeBaseLimit);
        infractionLimitByRole.put(UserRole.VOLUNTEER, employeeBaseLimit);
        infractionLimitByRole.put(UserRole.MANAGER, employeeBaseLimit + 3);
    }

    @Override
    public double returnBook(int userId, int bookId) {
        if (!isValidLibraryId(bookId)) {
            System.out.println("Invalid book ID format for this library!");
            return -1.0;
        }

        BorrowRecord activeLoan = null;

        for (BorrowRecord record : activeLoans) {
            if (record.getUserId() == userId &&
                    record.getBookId() == bookId &&
                    record.getReturnDate() == null) {
                activeLoan = record;
                break;
            }
        }

        if (activeLoan != null) {
            Book book = books.get(bookId);
            User user = users.get(userId);

            // Check if book is overdue
            boolean wasOverdue = LocalDate.now().isAfter(activeLoan.getDueDate());

            if (wasOverdue) {
                // Add infraction for late return
                user.addLateReturn();

                // Add infraction points based on role
                int points = getLateReturnPoints(user.getRole());
                addInfraction(userId, points);
            }

            Double lateFee = lateFeeByRole.get(user.getRole());
            if (lateFee == null) {
                lateFee = 0.50; // Default fallback
            }

            activeLoan.returnBook(LocalDate.now(), lateFee);
            double fee = activeLoan.getLateFee();

            book.setAvailable(true);
            user.removeBorrowedBook(bookId);
            activeLoans.remove(activeLoan);

            return fee;
        }

        System.out.println("No active loan found for user " + userId + " and book " + bookId);
        return -1.0;
    }

    private int getLateReturnPoints(UserRole role) {
        // Different points based on role (managers get higher points)
        switch (role) {
            case MANAGER:
                return 3;
            case EMPLOYEE:
            case JUNIOR_EMPLOYEE:
            case VOLUNTEER:
                return 1;
            default:
                return 1;
        }
    }

    public void addInfraction(int userId, int points) {
        User user = users.get(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        user.addInfractionPoints(points);

        int limit = infractionLimitByRole.get(user.getRole());
        int currentPoints = user.getInfractionPoints();

        // Check thresholds
        double warningThreshold = limit * 0.15;
        double suspensionThreshold = limit * 0.50;

        if (currentPoints >= limit) {
            user.setUserStatus(UserStatus.TERMINATED);
            System.out.println(user.getName() + " has been TERMINATED due to reaching infraction limit!");
        } else if (currentPoints >= suspensionThreshold) {
            if (user.getRole() == UserRole.EMPLOYEE || user.getRole() == UserRole.MANAGER) {
                user.setUserStatus(UserStatus.TEMPORARY_LEAVE);
                int weeks = user.getRole() == UserRole.MANAGER ? 0 : 2; // Manager: 3 days
                user.applySuspension(weeks);
                System.out.println(user.getName() + " has been placed on TEMPORARY LEAVE!");
            } else {
                user.setUserStatus(UserStatus.SUSPENDED);
                user.applySuspension(1); // 1 week for patrons
                System.out.println(user.getName() + " has been SUSPENDED for 1 week!");
            }
        } else if (currentPoints >= warningThreshold) {
            user.setUserStatus(UserStatus.WARNING);
            System.out.println(user.getName() + " has received a WARNING!");
        }
    }

    private boolean isValidLibraryId(int bookId) {
        if (bookId < startId) return false;
        return (bookId - startId) % idIncrements == 0;
    }

    @Override
    public List<BorrowRecord> getOverdueBooks() {
        List<BorrowRecord> overdueBooks = new ArrayList<>();

        for (BorrowRecord record : activeLoans) {
            if (record.isOverdue()) {
                overdueBooks.add(record);
            }
        }

        return overdueBooks;
    }

    @Override
    public List<BorrowRecord> getUserBorrowHistory(int userId) {
        List<BorrowRecord> userHistory = new ArrayList<>();

        for (BorrowRecord record : borrowHistory) {
            if (record.getUserId() == userId) {
                userHistory.add(record);
            }
        }

        return userHistory;
    }

    @Override
    public double getTotalLateFeesForUser(int userId) {
        double totalFees = 0.0;

        for (BorrowRecord record : borrowHistory) {
            if (record.getUserId() == userId) {
                totalFees += record.getLateFee();
            }
        }

        return totalFees;
    }

    @Override
    public int getBookCount() {
        return books.size();
    }

    @Override
    public int getUserCount() {
        return users.size();
    }

    @Override
    public int getActiveLoanCount() {
        return activeLoans.size();
    }

    @Override
    public int getBorrowHistoryCount() {
        return borrowHistory.size();
    }

    @Override
    public boolean hasPermission(int userId, String action) {
        User user = users.get(userId);
        if (user == null) return false;

        UserRole role = user.getRole();

        switch (action) {
            case "borrow":
                return role != UserRole.GUEST;
            case "view_all_users":
                return role == UserRole.EMPLOYEE || role == UserRole.MANAGER;
            case "add_book":
                return role == UserRole.EMPLOYEE || role == UserRole.MANAGER;
            case "view_employee_info":
                return role == UserRole.MANAGER;
            case "view_own_profile":
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canViewUserInfo(int viewerId, int targetUserId) {
        User viewer = users.get(viewerId);
        User target = users.get(targetUserId);

        if (viewer == null || target == null) return false;

        // Can view own info
        if (viewerId == targetUserId) return true;

        UserRole viewerRole = viewer.getRole();

        switch (viewerRole) {
            case MANAGER:
                return true; // Manager can see everyone
            case EMPLOYEE:
                // Employee can see patrons and guests
                return target.getRole() == UserRole.PATRON ||
                        target.getRole() == UserRole.GUEST;
            default:
                return false;
        }
    }

    @Override
    public boolean canChangeUserRole(int managerId, int targetUserId, UserRole newRole) {
        User manager = users.get(managerId);
        if (manager == null || manager.getRole() != UserRole.MANAGER) {
            return false;
        }

        // Manager cannot change their own role
        if (managerId == targetUserId) {
            return false;
        }

        return true;
    }

    @Override
    public Collection<Book> getAllBooks() {
        return books.values();
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }
}