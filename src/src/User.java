import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int borrowLimit = 7;
    private List<Integer> booksBorrowed = new ArrayList<>();


    User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
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


    public void borrowBook(String bookName) {
        if (!booksBorrowed.contains(bookName)) {
            booksBorrowed.add(Integer.valueOf(bookName));
        }

        if(booksBorrowed.size() <= borrowLimit) {
            System.out.println("Sorry " + name + " you have reached the limit of books you're allowed to borrow.");
        }

    }
}
