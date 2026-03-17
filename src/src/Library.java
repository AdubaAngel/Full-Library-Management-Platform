import java.time.LocalDate;
import java.util.List;

public interface Library {
    // Basic info
    String getName();

    // Book management
    int addBook(Book book);
    List<Book> findBooksByTitle(String title);

    // User management
    int registerUser(User user);
    User findUserById(int userId);

    // Borrowing operations
    BorrowRecord borrowBook(int userId, int bookId);
    double returnBook(int userId, int bookId);

    // Reports
    List<BorrowRecord> getOverdueBooks();
    List<BorrowRecord> getUserBorrowHistory(int userId);
    double getTotalLateFeesForUser(int userId);

    // Count methods (for testing)
    int getBookCount();
    int getUserCount();
    int getActiveLoanCount();
    int getBorrowHistoryCount();
}