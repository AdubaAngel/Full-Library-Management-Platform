public class GenericMainClass {
    public static void main(String[] args) {
        System.out.println("=== LIBRARY SYSTEM TEST ===\n");

        // ===== STEP 1: Create a library =====
        BaseLibrary library = new BaseLibrary("Downtown Library");
        System.out.println("Created: " + library.getName() + "\n");

        // ===== STEP 2: Add books =====
        System.out.println("--- Adding Books ---");

        Book book1 = new Book("The Hobbit", "J.R.R. Tolkien", "9780547928227", 1937);
        Book book2 = new Book("Dune", "Frank Herbert", "9780441172719", 1965);
        Book book3 = new Book("1984", "George Orwell", "9780451524935", 1949);

        int bookId1 = library.addBook(book1);
        int bookId2 = library.addBook(book2);
        int bookId3 = library.addBook(book3);

        System.out.println("Added: " + book1.getTitle() + " (ID: " + bookId1 + ")");
        System.out.println("Added: " + book2.getTitle() + " (ID: " + bookId2 + ")");
        System.out.println("Added: " + book3.getTitle() + " (ID: " + bookId3 + ")\n");

        // ===== STEP 3: Register users =====
        System.out.println("--- Registering Users ---");

        User user1 = new User("Alice Smith", "alice@email.com", "555-1234");
        User user2 = new User("Bob Jones", "bob@email.com", "555-5678");

        int userId1 = library.registerUser(user1);
        int userId2 = library.registerUser(user2);

        System.out.println("Registered: " + user1.getName() + " (ID: " + userId1 + ")");
        System.out.println("Registered: " + user2.getName() + " (ID: " + userId2 + ")\n");

        // ===== STEP 4: Test borrowing =====
        System.out.println("--- Testing Borrowing ---");

        // Alice borrows The Hobbit
        System.out.println("Alice borrowing The Hobbit (ID: " + bookId1 + "):");
        BorrowRecord record1 = library.borrowBook(userId1, bookId1);
        if (record1 != null) {
            System.out.println("  ✓ Success! Due: " + record1.getDueDate());
            System.out.println("  ✓ Book available? " + book1.isAvailable());
            System.out.println("  ✓ Alice's book count: " + user1.getBorrowedBookCount());
        }
        System.out.println();

        // Bob borrows Dune
        System.out.println("Bob borrowing Dune (ID: " + bookId2 + "):");
        BorrowRecord record2 = library.borrowBook(userId2, bookId2);
        if (record2 != null) {
            System.out.println("  ✓ Success! Due: " + record2.getDueDate());
            System.out.println("  ✓ Book available? " + book2.isAvailable());
            System.out.println("  ✓ Bob's book count: " + user2.getBorrowedBookCount());
        }
        System.out.println();

        // Alice tries to borrow the same book again (should fail)
        System.out.println("Alice trying to borrow The Hobbit again:");
        BorrowRecord record3 = library.borrowBook(userId1, bookId1);
        if (record3 == null) {
            System.out.println("  ✓ Correctly failed - book unavailable");
        }
        System.out.println();

        // Alice tries to borrow a 4th book (should work, limit is 7)
        System.out.println("Alice borrowing 1984 (ID: " + bookId3 + "):");
        BorrowRecord record4 = library.borrowBook(userId1, bookId3);
        if (record4 != null) {
            System.out.println("  ✓ Success! Alice now has " + user1.getBorrowedBookCount() + " books");
        }
        System.out.println();

        // ===== STEP 5: Test returns =====
        System.out.println("--- Testing Returns ---");

        // Alice returns The Hobbit
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

        // Alice tries to return the same book again (should fail)
        System.out.println("Alice trying to return The Hobbit again:");
        double fee2 = library.returnBook(userId1, bookId1);
        if (fee2 == -1.0) {
            System.out.println("  ✓ Correctly failed - no active loan");
        }
        System.out.println();

        // ===== STEP 6: Test ID validation =====
        System.out.println("--- Testing ID Validation ---");

        // Try to return with invalid ID format
        System.out.println("Trying to return book with invalid ID (999):");
        double fee3 = library.returnBook(userId1, 999);
        if (fee3 == -1.0) {
            System.out.println("  ✓ Correctly rejected invalid ID");
        }
        System.out.println();

        // Try to return with valid format but non-existent ID
        System.out.println("Trying to return non-existent book (ID: 1007 - valid format but not added):");
        double fee4 = library.returnBook(userId1, 1007);
        if (fee4 == -1.0) {
            System.out.println("  ✓ Correctly rejected - no active loan found");
        }
        System.out.println();

        // ===== STEP 7: Check final state =====
        System.out.println("--- Final State ---");
        System.out.println("Books in library: " + library.getBookCount());
        System.out.println("Users registered: " + library.getUserCount());
        System.out.println("Active loans: " + library.getActiveLoanCount());
        System.out.println("Borrow history entries: " + library.getBorrowHistoryCount());

        System.out.println("\n=== TEST COMPLETE ===");
    }
}