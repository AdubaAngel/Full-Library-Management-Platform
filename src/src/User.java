import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int borrowLimit = 7;
    private List<Integer> booksBorrowed = new ArrayList<>();
    private UserRole role;

    // New tracking fields
    private int infractionPoints;
    private int sickDaysTaken;
    private int lateArrivals;
    private int lateReturns;
    private UserStatus userStatus;
    private double pendingLateFees;
    private double paidLateFees;
    private String password;
    private int suspensionCount;
    private boolean hasUsedLeave;
    private LocalDate suspensionEndDate;

    public User(String name, String email, String phone, UserRole role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;

        // Initialize tracking fields
        this.infractionPoints = 0;
        this.sickDaysTaken = 0;
        this.lateArrivals = 0;
        this.lateReturns = 0;
        this.userStatus = UserStatus.ACTIVE;
        this.pendingLateFees = 0.0;
        this.paidLateFees = 0.0;
        this.suspensionCount = 0;
        this.hasUsedLeave = false;
        this.suspensionEndDate = null;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public UserRole getRole() { return role; }
    public int getInfractionPoints() { return infractionPoints; }
    public int getSickDaysTaken() { return sickDaysTaken; }
    public int getLateArrivals() { return lateArrivals; }
    public int getLateReturns() { return lateReturns; }
    public UserStatus getUserStatus() { return userStatus; }
    public double getPendingLateFees() { return pendingLateFees; }
    public double getPaidLateFees() { return paidLateFees; }
    public int getSuspensionCount() { return suspensionCount; }
    public boolean hasUsedLeave() { return hasUsedLeave; }
    public LocalDate getSuspensionEndDate() { return suspensionEndDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(UserRole role) { this.role = role; }
    public void setUserStatus(UserStatus status) { this.userStatus = status; }
    public void setPassword(String password) { this.password = password; }
    public void setSuspensionEndDate(LocalDate date) { this.suspensionEndDate = date; }

    // Infraction methods
    public void addInfractionPoints(int points) {
        this.infractionPoints += points;
    }

    public void resetInfractionPoints() {
        this.infractionPoints = 0;
    }

    // Attendance methods
    public void addSickDay() {
        this.sickDaysTaken++;
    }

    public void recordLateArrival() {
        this.lateArrivals++;
    }

    public void addLateReturn() {
        this.lateReturns++;
    }

    // Fee methods
    public void addPendingFee(double amount) {
        this.pendingLateFees += amount;
    }

    public void payFee(double amount) {
        if (amount <= this.pendingLateFees) {
            this.pendingLateFees -= amount;
            this.paidLateFees += amount;
        } else {
            this.paidLateFees += this.pendingLateFees;
            this.pendingLateFees = 0;
        }
    }

    // Suspension methods
    public void applySuspension(int weeks) {
        this.suspensionEndDate = LocalDate.now().plusWeeks(weeks);
        this.suspensionCount++;

        // For employees/managers, mark that they've used their one chance
        if (role == UserRole.EMPLOYEE || role == UserRole.MANAGER) {
            this.hasUsedLeave = true;
        }
    }

    public boolean isCurrentlySuspended() {
        if (suspensionEndDate == null) return false;
        return LocalDate.now().isBefore(suspensionEndDate);
    }

    public void liftSuspension() {
        this.suspensionEndDate = null;
        this.userStatus = UserStatus.ACTIVE;
    }

    // Calculate wait time for next suspension (patrons only)
    public int getSuspensionWaitWeeks() {
        return this.suspensionCount; // Each suspension adds 1 week
    }

    // Borrowing methods
    public boolean borrowBook(int bookID) {
        if (booksBorrowed.size() >= borrowLimit) {
            System.out.println("Sorry " + name + " you have reached the limit of books you're allowed to borrow.");
            return false;
        }

        if (!booksBorrowed.contains(bookID)) {
            booksBorrowed.add(bookID);
            System.out.println("Book " + bookID + " added to your borrowed list.");
            return true;
        } else {
            System.out.println("You already have book " + bookID + " checked out.");
            return false;
        }
    }

    public void addBorrowedBook(int bookId) {
        booksBorrowed.add(bookId);
    }

    public void removeBorrowedBook(int bookId) {
        booksBorrowed.remove((Integer) bookId);
    }

    public int getBorrowedBookCount() {
        return booksBorrowed.size();
    }

    public boolean hasBorrowedBook(int bookId) {
        return booksBorrowed.contains(bookId);
    }

    // Password methods
    public boolean verifyPassword(String input) {
        // Simple comparison - in production, use hashed passwords
        return this.password != null && this.password.equals(input);
    }

    // Status check methods
    public boolean isTerminated() {
        return userStatus == UserStatus.TERMINATED;
    }

    public boolean isActive() {
        return userStatus == UserStatus.ACTIVE && !isCurrentlySuspended();
    }

    @Override
    public String toString() {
        return name + " (ID: " + id + ", Role: " + role + ", Status: " + userStatus + ")";
    }
}