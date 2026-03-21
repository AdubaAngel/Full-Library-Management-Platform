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


    User(String name, String email, String phone, UserRole role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    // Optional: Add setter if role can change
    public void setRole(UserRole role) {
        this.role = role;
    }


    public boolean borrowBook(int bookID) {
        if(booksBorrowed.size() >= borrowLimit) {
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
}
