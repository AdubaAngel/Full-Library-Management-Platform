import java.time.LocalDate;
import java.util.*;

public class PublicLibrary implements Library {
    private String name;
    private int startId;
    private int idIncrements;

    private Map<Integer, Book> books;
    private Map<Integer, User> users;
    private List<BorrowRecord> borrowHistory;
    private List<BorrowRecord> activeLoans;

    private int nextBookId;
    private int nextUserId;
    private Map<UserRole, Integer> maxBooksByRole;
    private Map<UserRole, Integer> loanDaysByRole;
    private Map<UserRole, Double> lateFeeByRole;

    public PublicLibrary(String name, int startId, int idIncrements) {
        this.name = name;
        this.startId = startId;
        this.idIncrements = idIncrements;
        this.nextBookId = startId;
        this.nextUserId = startId;
        maxBooksByRole = new HashMap<>();
        loanDaysByRole = new HashMap<>();
        lateFeeByRole = new HashMap<>();
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

        if (user.getBorrowedBookCount() >= maxBooksByRole.get(user.getRole())) {
            System.out.println("User has reached maximum books limit of " + maxBooksByRole.get(user.getRole()) + "!");
            return null;
        }

        user.addBorrowedBook(bookId);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(loanDaysByRole.get(user.getRole()));

        BorrowRecord record = new BorrowRecord(
                bookId, userId, book.getTitle(), borrowDate, dueDate
        );

        activeLoans.add(record);
        borrowHistory.add(record);
        book.setAvailable(false);

        return record;
    }

    private void initializeRoleRules(){
        maxBooksByRole = new HashMap<>();
        loanDaysByRole = new HashMap<>();
        lateFeeByRole = new HashMap<>();

        //PATRON AND GUEST
        maxBooksByRole.put(UserRole.PATRON, 7);
        maxBooksByRole.put(UserRole.GUEST, 2);

        loanDaysByRole.put(UserRole.PATRON, 14);
        loanDaysByRole.put(UserRole.GUEST, 7);

        lateFeeByRole.put(UserRole.PATRON, 0.50);
        lateFeeByRole.put(UserRole.GUEST, 1.00);

        //EMPLOYEES E.G (MANAGER, JUNIOR EMPLOYEE VOLUNTEER)
        maxBooksByRole.put(UserRole.EMPLOYEE, 10);
        loanDaysByRole.put(UserRole.EMPLOYEE, 20);
        lateFeeByRole.put(UserRole.EMPLOYEE, 0.25);

        maxBooksByRole.put(UserRole.MANAGER, 30);
        loanDaysByRole.put(UserRole.MANAGER, 30);
        lateFeeByRole.put(UserRole.MANAGER, 1.00);

        maxBooksByRole.put(UserRole.JUNIOR_EMPLOYEE, 8);
        loanDaysByRole.put(UserRole.JUNIOR_EMPLOYEE, 15);
        lateFeeByRole.put(UserRole.JUNIOR_EMPLOYEE, 0.15);

        maxBooksByRole.put(UserRole.VOLUNTEER, 12);
        loanDaysByRole.put(UserRole.VOLUNTEER, 10);
        lateFeeByRole.put(UserRole.VOLUNTEER, 0.10);
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

            double userLateFee = lateFeeByRole.get(user.getRole());
            activeLoan.returnBook(LocalDate.now(), userLateFee);
            double fee = activeLoan.getLateFee();

            book.setAvailable(true);
            user.removeBorrowedBook(bookId);
            activeLoans.remove(activeLoan);

            return fee;  // Return the captured fee
        }

        System.out.println("No active loan found for user " + userId + " and book " + bookId);
        return -1.0;
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
        return false;
    }

    @Override
    public boolean canViewUserInfo(int viewerId, int targetUserId) {
        return false;
    }

    @Override
    public boolean canChangeUserRole(int managerId, int targetUserId, UserRole newRole) {
        return false;
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