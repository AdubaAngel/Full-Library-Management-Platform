import java.time.LocalDate;
import java.util.List;

public class GenericMainClass {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY SYSTEM TEST ===\n");

        // ===== STEP 1: Create a library (using interface) =====
        Library library = new PublicLibrary("Downtown Library");
        System.out.println("Created: " + library.getName() + "\n");

        // ===== STEP 2: Add books =====
        System.out.println("--- Adding Books ---");

        Book book1 = new Book("The Hobbit", "J.R.R. Tolkien", "9780547928227", 1937);
        Book book2 = new Book("Dune", "Frank Herbert", "9780441172719", 1965);
        Book book3 = new Book("1984", "George Orwell", "9780451524935", 1949);
        Book book4 = new Book("The Hobbit: An Unexpected Journey", "J.R.R. Tolkien", "9780547928228", 2012);

        int bookId1 = library.addBook(book1);
        int bookId2 = library.addBook(book2);
        int bookId3 = library.addBook(book3);
        int bookId4 = library.addBook(book4);

        System.out.println("Added: " + book1.getTitle() + " (ID: " + bookId1 + ")");
        System.out.println("Added: " + book2.getTitle() + " (ID: " + bookId2 + ")");
        System.out.println("Added: " + book3.getTitle() + " (ID: " + bookId3 + ")");
        System.out.println("Added: " + book4.getTitle() + " (ID: " + bookId4 + ")\n");

        // ===== STEP 3: Register users =====
        System.out.println("--- Registering Users ---");

        User user1 = new User("Alice Smith", "alice@email.com", "555-1234");
        User user2 = new User("Bob Jones", "bob@email.com", "555-5678");
        User user3 = new User("Charlie Brown", "charlie@email.com", "555-9012");

        int userId1 = library.registerUser(user1);
        int userId2 = library.registerUser(user2);
        int userId3 = library.registerUser(user3);

        System.out.println("Registered: " + user1.getName() + " (ID: " + userId1 + ")");
        System.out.println("Registered: " + user2.getName() + " (ID: " + userId2 + ")");
        System.out.println("Registered: " + user3.getName() + " (ID: " + userId3 + ")\n");

        // ===== STEP 4: Test borrowing =====
        System.out.println("--- Testing Borrowing ---");

        System.out.println("Alice borrowing The Hobbit (ID: " + bookId1 + "):");
        BorrowRecord record1 = library.borrowBook(userId1, bookId1);
        if (record1 != null) {
            System.out.println("  ✓ Success! Due: " + record1.getDueDate());
            System.out.println("  ✓ Book available? " + book1.isAvailable());
            System.out.println("  ✓ Alice's book count: " + user1.getBorrowedBookCount());
        }
        System.out.println();

        System.out.println("Bob borrowing Dune (ID: " + bookId2 + "):");
        BorrowRecord record2 = library.borrowBook(userId2, bookId2);
        if (record2 != null) {
            System.out.println("  ✓ Success! Due: " + record2.getDueDate());
            System.out.println("  ✓ Book available? " + book2.isAvailable());
            System.out.println("  ✓ Bob's book count: " + user2.getBorrowedBookCount());
        }
        System.out.println();

        System.out.println("Alice trying to borrow The Hobbit again:");
        BorrowRecord record3 = library.borrowBook(userId1, bookId1);
        if (record3 == null) {
            System.out.println("  ✓ Correctly failed - book unavailable");
        }
        System.out.println();

        // ===== STEP 5: Test returns =====
        System.out.println("--- Testing Returns ---");

        System.out.println("Alice returning The Hobbit (ID: " + bookId1 + "):");
        double fee1 = library.returnBook(userId1, bookId1);
        if (fee1 == -1.0) {
            System.out.println("  ✗ Error: Could not find loan");
        } else if (fee1 > 0) {
            System.out.println("  ✓ Returned with late fee: $" + fee1);
        } else {
            System.out.println("  ✓ Returned on time, no fee");
        }
        System.out.println("  ✓ Book available now? " + book1.isAvailable());
        System.out.println("  ✓ Alice's book count: " + user1.getBorrowedBookCount());
        System.out.println();

        // ===== STEP 6: Test search by title =====
        System.out.println("--- Testing Search by Title ---");

        System.out.println("Searching for 'hobbit':");
        List<Book> results = library.findBooksByTitle("hobbit");
        if (results.isEmpty()) {
            System.out.println("  No books found");
        } else {
            for (Book book : results) {
                System.out.println("  ✓ Found: " + book.getTitle());
            }
        }
        System.out.println();

        // ===== STEP 7: Test overdue books =====
        System.out.println("--- Testing Overdue Books ---");
        List<BorrowRecord> overdue = library.getOverdueBooks();
        System.out.println("  Overdue books: " + overdue.size());
        System.out.println();

        // ===== STEP 8: Test user history =====
        System.out.println("--- Testing User History ---");
        List<BorrowRecord> aliceHistory = library.getUserBorrowHistory(userId1);
        System.out.println("  Alice's history: " + aliceHistory.size() + " records");

        double aliceFees = library.getTotalLateFeesForUser(userId1);
        System.out.println("  Alice's total late fees: $" + aliceFees);
        System.out.println();

        // ===== STEP 9: Check final state =====
        System.out.println("--- Final State ---");
        System.out.println("Books in library: " + library.getBookCount());
        System.out.println("Users registered: " + library.getUserCount());
        System.out.println("Active loans: " + library.getActiveLoanCount());
        System.out.println("Borrow history entries: " + library.getBorrowHistoryCount());

        System.out.println("\n=== TEST COMPLETE ===");
    }
}