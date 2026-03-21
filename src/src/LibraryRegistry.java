import java.util.*;

public class LibraryRegistry {
    /**
     * Register a new library with a specific ID pattern
     * @param name Library name (must be unique)
     * @param startId Starting ID (e.g., 1000, 2000)
     * @param increment Increment value (e.g., 7, 5, 10)
     * @return The registered Library object
     */
    private Map<Integer, Library> libraries;
    private Map<String, Integer> nameToId;
    private Map<String, Integer> patternToId; // Track used patterns
    private int nextLibraryId;

    public LibraryRegistry() {
        this.libraries = new HashMap<>();
        this.nameToId = new HashMap<>();
        this.patternToId = new HashMap<>();
        this.nextLibraryId = 1;
    }

    public Library registerLibrary(String name, int startId, int increment,
                                   int maxBooks, double lateFee, int loanDays) {
        // Check if name already exists
        if(nameToId.containsKey(name)) {
            System.out.println(name + " already exists");
        }
        // Check if pattern already exists

        String patternKey = startId + "+" + increment;
        if(patternToId.containsKey(patternKey)) {
            System.out.println( "This pattern is already being used at another library");
        }
        // Generate new library ID
        int libraryId = nextLibraryId;
        nextLibraryId++;
        // Create new PublicLibrary with all parameters
        PublicLibrary newLibrary = new PublicLibrary(name, startId, increment,
                maxBooks, lateFee, loanDays);
        libraries.put(libraryId, newLibrary);
        nameToId.put(name, libraryId);
        patternToId.put(patternKey, libraryId);

        // Step 6: Success message
        System.out.println("✅ Library registered: " + name +
                " (ID: " + libraryId + ", Pattern: " + patternKey + ")");

        // Step 7: Return the new library
        return newLibrary;
    }

    public Library getLibraryByName(String name) {
        if(nameToId.containsKey(name)) {
            return libraries.get(nameToId.get(name));
        }
        return null;
    }

    public Library getLibraryById(int libraryId) {
        // Your code here
        return libraries.getOrDefault(libraryId, null);
    }


    public List<Library> getAllLibraries() {
        // Return a list of all libraries from the map
        ArrayList<Library> libraryList = new ArrayList<>(libraries.values());
        // using libraries.values() and wrapping it in an ArrayList. Makes it easier to display all the items in the map
        return libraryList;
    }

    public int getLibraryCount() {
        int libraryCount = libraries.size();
        return libraryCount;
    }
}
