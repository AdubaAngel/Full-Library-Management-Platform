import java.time.LocalDate;
import java.util.*;

public class PublicLibrary implements Library {
    private String name;
    private Map<Integer, Book> books;
    private Map<Integer, User> users;
    private List<BorrowRecord> borrowHistory;
    private List<BorrowRecord> activeLoans;

    private static final int MAX_BOOKS_PER_USER = 7;
    private static final double DAILY_LATE_FEE = 0.50;
    private static final int LOAN_DURATION_DAYS = 14;

    private int nextBookId = 1000;
    private int nextUserId = 1000;
    private int idIncrements = 7;

    public PublicLibrary(String name) {
        this.name = name;
        this.books = new HashMap<>();
        this.users = new HashMap<>();
        this.borrowHistory = new ArrayList<>();
        this.activeLoans = new ArrayList<>();
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

        if (user.getBorrowedBookCount() >= MAX_BOOKS_PER_USER) {
            System.out.println("User has reached maximum books limit!");
            return null;
        }

        user.addBorrowedBook(bookId);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(LOAN_DURATION_DAYS);

        BorrowRecord record = new BorrowRecord(
                bookId, userId, book.getTitle(), borrowDate, dueDate
        );

        activeLoans.add(record);
        borrowHistory.add(record);
        book.setAvailable(false);

        return record;
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

            activeLoan.returnBook(LocalDate.now(), DAILY_LATE_FEE);
            double fee = activeLoan.getLateFee();

            book.setAvailable(true);
            user.removeBorrowedBook(bookId);
            activeLoans.remove(activeLoan);

            return fee;
        }

        System.out.println("No active loan found for user " + userId + " and book " + bookId);
        return -1.0;
    }

    private boolean isValidLibraryId(int bookId) {
        return (bookId - 1000) % 7 == 0;
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
}