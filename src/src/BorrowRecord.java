import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private int bookId;
    private int userId;
    private String bookName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;  // Changed to LocalDate for consistency
    private double lateFee;

    // Constructor - only what you know at borrow time
    public BorrowRecord(int bookId, int userId, String bookName,
                        LocalDate borrowDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.bookName = bookName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;  // Not returned yet
        this.lateFee = 0.0;      // No fee yet
    }

    // Getters
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public String getBookName() { return bookName; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getLateFee() { return lateFee; }

    // Check if book is currently overdue (not returned and past due date)
    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    // Check if book was overdue when returned
    public boolean wasOverdue() {
        if (returnDate == null) {
            return false;  // Not returned yet
        }
        return returnDate.isAfter(dueDate);
    }

    // Calculate days late (if returned)
    public long getDaysLate() {
        if (returnDate == null || !returnDate.isAfter(dueDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, returnDate);
    }

    // When book is returned
    // Default rate
    public double returnBook(LocalDate returnDate) {
        return returnBook(returnDate, 0.50);
    }

    // Custom rate
    public double returnBook(LocalDate returnDate, double dailyLateFee) {
        this.returnDate = returnDate;
        this.lateFee = calculateLateFee(dailyLateFee);
        return this.lateFee;
    }

    // Calculate late fee based on daily rate
    private double calculateLateFee(double dailyRate) {
        if (!wasOverdue()) {
            return 0.0;
        }

        long daysLate = getDaysLate();
        double fee = daysLate * dailyRate;

        // Optional: Cap the late fee
        double MAX_FEE = 20.00;
        return Math.min(fee, MAX_FEE);
    }

    // Overloaded method with default rate
    public double calculateLateFee() {
        return calculateLateFee(0.50);  // Default rate
    }

    @Override
    public String toString() {
        String status = (returnDate == null) ?
                (isOverdue() ? "OVERDUE" : "Borrowed") :
                "Returned";

        return bookName + " (User " + userId + ") - " +
                "Borrowed: " + borrowDate + ", Due: " + dueDate +
                ", Status: " + status +
                (returnDate != null ? ", Returned: " + returnDate +
                        ", Fee: $" + lateFee : "");
    }
}