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
        // Check if pattern already exists
        // Generate new library ID
        // Create new PublicLibrary with all parameters
        // Store in maps
        // Return the library
        return null;
    }
}
