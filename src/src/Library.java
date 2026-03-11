import java.util.*;

public class Library {
    private String name;
    private Map<Integer, Book> books;           // bookId -> Book
    private Map<Integer, User> users;            // userId -> User
    private List<BorrowRecord> borrowHistory;    // All borrow records
    private List<BorrowRecord> activeLoans;      // Currently borrowed books

    public Library(String name) {
        this.name = name;
        this.books = new HashMap<>();
        this.users = new HashMap<>();
        this.borrowHistory = new ArrayList<>();
        this.activeLoans = new ArrayList<>();
    }

    private static final int MAX_BOOKS_PER_USER = 7;
    private static final double DAILY_LATE_FEE = 0.50;
    private static final int LOAN_DURATION_DAYS = 14;

    // Counters for IDs
    private int nextBookId = 1000;
    private int nextUserId = 1000;
    private int idIncrements = 7;

    // Book management
    public int addBook(Book book) {
        // What info do you need? How to generate bookId?
        int localId = nextBookId;
        books.put(localId, book);
        nextBookId+=idIncrements;
        return localId;

    }

    public Book findBookByTitle(String title) {
        // Search through books
    }

    // User management
    public int registerUser(User user) {
        // Add new user
        int localUserID = nextUserId;
        user.setId(localUserID);
        users.put(localUserID, user);
        nextUserId+=idIncrements;
        return localUserID;
    }

    public User findUserById(int userId) {
        return users.get(userId);
    }

    // Borrowing logic
    public BorrowRecord borrowBook(int userId, int bookId) {
        // Check if user exists
        if(users.containsKey(userId)) {
            System.out.println("User " + userId + " exists!!");
        }else{
            System.out.println("Apologies we were unable to find a user with the id " + userId + "!");
            return null;
        }
        // Check if book exists and is available
        if(books.containsKey(bookId)) {
            System.out.println("The book " + userId + " exists!!");
        }else{
            System.out.println("Apologies we were unable to find a book with the id " + bookId + "!");
            return null;
        }

        // Check if user under limit
        // Create borrow record
        // Update book availability
        // Add to activeLoans
    }

    // Returning logic
    public double returnBook(int userId, int bookId) {
        // Find active loan
        // Call returnBook() on the record
        // Update book availability
        // Move from activeLoans to borrowHistory
        // Return the fee
    }

    // Reports
    public List<BorrowRecord> getOverdueBooks() {
        // Return all active loans where isOverdue() is true
    }

    public List<BorrowRecord> getUserBorrowHistory(int userId) {
        // Return all records for a specific user
    }

    public double getTotalLateFeesForUser(int userId) {
        // Sum all late fees from user's history
        return 0;
    }
}
