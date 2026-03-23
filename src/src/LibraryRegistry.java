import java.util.*;

public class LibraryRegistry {
    private Map<Integer, Library> libraries;
    private Map<String, Integer> nameToId;
    private Map<String, Integer> patternToId;
    private int nextLibraryId;

    public LibraryRegistry() {
        this.libraries = new HashMap<>();
        this.nameToId = new HashMap<>();
        this.patternToId = new HashMap<>();
        this.nextLibraryId = 1;
    }

    public Library registerLibrary(String name, int startId, int increment,
                                   int employeeBaseLimit, int userBaseLimit) {
        if (nameToId.containsKey(name)) {
            System.out.println("❌ Library name '" + name + "' already exists!");
            return null;
        }

        String patternKey = startId + "+" + increment;
        if (patternToId.containsKey(patternKey)) {
            System.out.println("❌ Pattern " + patternKey + " already in use!");
            return null;
        }

        int libraryId = nextLibraryId++;
        PublicLibrary newLibrary = new PublicLibrary(name, startId, increment,
                employeeBaseLimit, userBaseLimit);
        libraries.put(libraryId, newLibrary);
        nameToId.put(name, libraryId);
        patternToId.put(patternKey, libraryId);

        System.out.println("✅ Library registered: " + name + " (ID: " + libraryId +
                ", Pattern: " + patternKey + ", Emp Limit: " + employeeBaseLimit +
                ", User Limit: " + userBaseLimit + ")");
        return newLibrary;
    }

    public Library getLibraryByName(String name) {
        Integer id = nameToId.get(name);
        return id != null ? libraries.get(id) : null;
    }

    public Library getLibraryById(int libraryId) {
        return libraries.get(libraryId);
    }

    public List<Library> getAllLibraries() {
        return new ArrayList<>(libraries.values());
    }

    public int getLibraryCount() {
        return libraries.size();
    }
}