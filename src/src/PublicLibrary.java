import java.time.LocalDate;
import java.util.*;

public class PublicLibrary implements Library {
    private String name;
    private int startId;
    private int idIncrements;
    private int maxBooksPerUser;
    private double dailyLateFee;
    private int loanDurationDays;

    private Map<Integer, Book> books;
    private Map<Integer, User> users;
    private List<BorrowRecord> borrowHistory;
    private List<BorrowRecord> activeLoans;

    private int nextBookId;
    private int nextUserId;

    public PublicLibrary(String name, int startId, int idIncrements,
                         int maxBooksPerUser, double dailyLateFee, int loanDurationDays) {
        this.name = name;
        this.startId = startId;
        this.idIncrements = idIncrements;
        this.maxBooksPerUser = maxBooksPerUser;
        this.dailyLateFee = dailyLateFee;
        this.loanDurationDays = loanDurationDays;
        this.nextBookId = startId;
        this.nextUserId = startId;
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

        if (user.getBorrowedBookCount() >= maxBooksPerUser) {
            System.out.println("User has reached maximum books limit of " + maxBooksPerUser + "!");
            return null;
        }

        user.addBorrowedBook(bookId);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(loanDurationDays);

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

            activeLoan.returnBook(LocalDate.now(), dailyLateFee);
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
    public Collection<Book> getAllBooks() {
        return books.values();  // books.values() returns all books in the map
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();  // users.values() returns all users in the map
    }
}