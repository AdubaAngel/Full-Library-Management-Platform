import java.time.LocalDate;
import java.util.List;

public class GenericMainClass {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY SYSTEM TEST ===\n");

        // ===== STEP 1: Create multiple libraries with different rules =====
        System.out.println("--- Creating Libraries ---");

        Library downtown = new PublicLibrary("Downtown Library", 1000, 7, 7, 0.50, 14);
        Library university = new PublicLibrary("University Library", 2000, 5, 15, 0.25, 30);
        Library school = new PublicLibrary("School Library", 3000, 10, 3, 0.10, 10);

        System.out.println("Created: " + downtown.getName() + " (Start: 1000, Inc: 7, Max: 7, Fee: $0.50, Days: 14)");
        System.out.println("Created: " + university.getName() + " (Start: 2000, Inc: 5, Max: 15, Fee: $0.25, Days: 30)");
        System.out.println("Created: " + school.getName() + " (Start: 3000, Inc: 10, Max: 3, Fee: $0.10, Days: 10)\n");

        // ===== STEP 2: Test Downtown Library =====
        System.out.println("=== TESTING DOWNTOWN LIBRARY ===\n");
        testLibrary(downtown, "Alice", "Bob");

        // ===== STEP 3: Test University Library =====
        System.out.println("\n=== TESTING UNIVERSITY LIBRARY ===\n");
        testLibrary(university, "Charlie", "Diana");

        // ===== STEP 4: Test School Library =====
        System.out.println("\n=== TESTING SCHOOL LIBRARY ===\n");
        testLibrary(school, "Ethan", "Fiona");
    }

    private static void testLibrary(Library library, String userName1, String userName2) {
        // Add books
        System.out.println("--- Adding Books to " + library.getName() + " ---");

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

        // Register users
        System.out.println("--- Registering Users in " + library.getName() + " ---");

        User user1 = new User(userName1 + " Smith", userName1.toLowerCase() + "@email.com", "555-1234");
        User user2 = new User(userName2 + " Jones", userName2.toLowerCase() + "@email.com", "555-5678");

        int userId1 = library.registerUser(user1);
        int userId2 = library.registerUser(user2);

        System.out.println("Registered: " + user1.getName() + " (ID: " + userId1 + ")");
        System.out.println("Registered: " + user2.getName() + " (ID: " + userId2 + ")\n");

        // Test borrowing
        System.out.println("--- Testing Borrowing in " + library.getName() + " ---");

        System.out.println(user1.getName() + " borrowing " + book1.getTitle() + " (ID: " + bookId1 + "):");
        BorrowRecord record1 = library.borrowBook(userId1, bookId1);
        if (record1 != null) {
            System.out.println("  ✓ Success! Due: " + record1.getDueDate());
            System.out.println("  ✓ Book available? " + book1.isAvailable());
            System.out.println("  ✓ " + user1.getName() + "'s book count: " + user1.getBorrowedBookCount());
        }
        System.out.println();

        System.out.println(user2.getName() + " borrowing " + book2.getTitle() + " (ID: " + bookId2 + "):");
        BorrowRecord record2 = library.borrowBook(userId2, bookId2);
        if (record2 != null) {
            System.out.println("  ✓ Success! Due: " + record2.getDueDate());
            System.out.println("  ✓ Book available? " + book2.isAvailable());
            System.out.println("  ✓ " + user2.getName() + "'s book count: " + user2.getBorrowedBookCount());
        }
        System.out.println();

        // Test returns
        System.out.println("--- Testing Returns in " + library.getName() + " ---");

        System.out.println(user1.getName() + " returning " + book1.getTitle() + " (ID: " + bookId1 + "):");
        double fee1 = library.returnBook(userId1, bookId1);
        if (fee1 == -1.0) {
            System.out.println("  ✗ Error: Could not find loan");
        } else if (fee1 > 0) {
            System.out.println("  ✓ Returned with late fee: $" + String.format("%.2f", fee1));
        } else {
            System.out.println("  ✓ Returned on time, no fee");
        }
        System.out.println("  ✓ Book available now? " + book1.isAvailable());
        System.out.println("  ✓ " + user1.getName() + "'s book count: " + user1.getBorrowedBookCount());
        System.out.println();

        // Test search by title
        System.out.println("--- Testing Search in " + library.getName() + " ---");

        System.out.println("Searching for 'hobbit':");
        List<Book> results = library.findBooksByTitle("hobbit");
        if (results.isEmpty()) {
            System.out.println("  No books found");
        } else {
            for (Book book : results) {
                System.out.println("  ✓ Found: " + book.getTitle() + " (ID: " + getBookId(library, book) + ")");
            }
        }
        System.out.println();

        // Test user history
        System.out.println("--- Testing User History in " + library.getName() + " ---");

        List<BorrowRecord> user1History = library.getUserBorrowHistory(userId1);
        System.out.println("  " + user1.getName() + "'s history: " + user1History.size() + " records");

        double user1Fees = library.getTotalLateFeesForUser(userId1);
        System.out.println("  " + user1.getName() + "'s total late fees: $" + String.format("%.2f", user1Fees));
        System.out.println();

        // Final state
        System.out.println("--- Final State of " + library.getName() + " ---");
        System.out.println("Books: " + library.getBookCount());
        System.out.println("Users: " + library.getUserCount());
        System.out.println("Active loans: " + library.getActiveLoanCount());
        System.out.println("Borrow history: " + library.getBorrowHistoryCount());
    }

    // Helper method to find a book's ID (since Book class doesn't store its ID)
    private static Integer getBookId(Library library, Book targetBook) {
        // This is a simplified approach - in reality, you might want to add a method to Library
        // to get book ID by ISBN or title, or store ID in Book class
        return null; // Placeholder
    }
}