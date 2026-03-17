import java.time.LocalDate;
import java.util.*;

public class BaseLibrary {
    private String name;
    private Map<Integer, Book> books;           // bookId -> Book
    private Map<Integer, User> users;            // userId -> User
    private List<BorrowRecord> borrowHistory;    // All borrow records
    private List<BorrowRecord> activeLoans;      // Currently borrowed books

    public BaseLibrary(String name) {
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

    public String getName() {
        return name;
    }

    public int getBookCount() {
        return books.size();
    }

    public int getUserCount() {
        return users.size();
    }

    public int getActiveLoanCount() {
        return activeLoans.size();
    }

    public int getBorrowHistoryCount() {
        return borrowHistory.size();
    }

    // Book management
    public int addBook(Book book) {
        // What info do you need? How to generate bookId?
        int localId = nextBookId;
        books.put(localId, book);
        nextBookId+=idIncrements;
        return localId;

    }

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
        User user = null;
        Book book = null;
        if(users.containsKey(userId)) {
            System.out.println("User " + userId + " exists!!");
            user = users.get(userId);
        }else{
            System.out.println("Apologies we were unable to find a user with the id " + userId + "!");
            return null;
        }

        // Check if book exists and is available
        if(books.containsKey(bookId)) {
            System.out.println("The book " + bookId + " exists!!");
            book = books.get(bookId);

            if(!book.isAvailable()){
                System.out.println("The book " + bookId + " is not available!");
                return null;
            }
        }else{
            System.out.println("Apologies we were unable to find a book with the id " + bookId + "!");
            return null;
        }

        user.addBorrowedBook(bookId);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(LOAN_DURATION_DAYS);

        BorrowRecord record = new BorrowRecord(
                bookId,
                userId,
                book.getTitle(),
                borrowDate,
                dueDate
        );

        activeLoans.add(record);
        borrowHistory.add(record);

        book.setAvailable(false);

        return record;
    }

    // Returning logic
    public double returnBook(int userId, int bookId) {
        // Step 1: Verify ID pattern
        if (!isValidLibraryId(bookId)) {
            System.out.println("Invalid book ID format for this library!");
            return -1.0;
        }

        // Step 2: Find active loan
        BorrowRecord activeLoan = null;
        for (BorrowRecord record : activeLoans) {
            if (record.getUserId() == userId &&
                    record.getBookId() == bookId &&
                    record.getReturnDate() == null) {
                activeLoan = record;
                break;
            }
        }

        // Step 3: Process if found
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

        // Step 4: Not found (no extra return needed!)
        System.out.println("No active loan found for user " + userId + " and book " + bookId);
        return -1.0;
    }


    private boolean isValidLibraryId(int bookId) {
        //This method verifies that the id of the book the user wants to return matches the id used in this library for the books
        if((bookId - 1000) % 7 == 0){
            return true;
        }else{
            return false;
        }

    }

    // Reports
    public List<BorrowRecord> getOverdueBooks() {
        // Create a new list to hold overdue records
        List<BorrowRecord> overdueBooks = new ArrayList<>();

        // Loop through activeLoans
        for (BorrowRecord record : activeLoans) {
            // Check if this record is overdue
            if(record.isOverdue()){
                overdueBooks.add(record);
            }
            // Use a method from BorrowRecord class!
        }

        // Return the list
        return overdueBooks;
    }

    public List<BorrowRecord> getUserBorrowHistory(int userId) {
        // Create a new list to hold this user's records
        List<BorrowRecord> userHistory = new ArrayList<>();
        // Loop through borrowHistory (all transactions)
        for (BorrowRecord record : borrowHistory) {
            // Check if this record belongs to the user
            if(record.getUserId() == userId){
                userHistory.add(record);
            }
        }
        // Return the list (empty if none found)
        return userHistory;
    }
    public double getTotalLateFeesForUser(int userId) {
        double totalFees = 0.0;
        for (BorrowRecord record : borrowHistory) {
            if (record.getUserId() == userId) {
                totalFees += record.getLateFee();
            }
        }
        return totalFees;
    }
}
