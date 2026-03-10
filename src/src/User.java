import java.util.ArrayList;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    ArrayList<String> booksBorrowed = new ArrayList<String>();


    User(int id, String name, String email, String phone) {
        this.id = id;
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

    public void borrowBook(String bookName) {
        if (!booksBorrowed.contains(bookName)) {
            booksBorrowed.add(bookName);
        }

        if(booksBorrowed.size() <= 7){
            System.out.println("Sorry " + name + " you have reached the limit of books you're allowed to borrow.");
        }

    }
}
